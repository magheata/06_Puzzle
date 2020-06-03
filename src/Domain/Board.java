/* Created by andreea on 26/05/2020 */
package Domain;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {

    private ArrayList<Tile> tiles;
    private Map<Integer, Tile> tileForValue;
    private Map<Position, Tile> tileForPosition;
    private int size;
    private ShiftDirection previousMove;
    private int movesCount = 0;

    public Board(ArrayList<Tile> tiles, int size) {
        this.tiles = tiles;
        this.size = size;
        initTileVariables();
    }

    private void initTileVariables() {
        tileForValue = new HashMap<>();
        tileForPosition = new HashMap<>();
        for (Tile tile: tiles){
            tileForValue.put(tile.getGoalValue(), tile);
            tileForPosition.put(tile.getPosition(), tile);
        }
    }

    public void resetTiles() {
        initTileVariables();
        setNeighbourTiles(tiles);
    }

    public boolean getIsWinning() {
        for (Tile tile: tiles){
            if (tile.getValue() != tile.getGoalValue()){
                return false;
            }
        }
        return true;
    }

    public int getManhattanDistance() {
        int manhattanDistance = 0;
        for (Tile tile : tiles){
            Tile goal = tileForValue.get(tile.getGoalValue());
            manhattanDistance += countManhattanDistance(tile, goal);
        }
        return manhattanDistance;
    }

    private int countManhattanDistance(Tile firstTile, Tile secondTile) {
        int manhattanDistance = 0;
        manhattanDistance += Math.abs(firstTile.getPosition().getX() - secondTile.getPosition().getX());
        manhattanDistance += Math.abs(firstTile.getPosition().getY() - secondTile.getPosition().getY());
        return manhattanDistance;
    }

    public Tile getBlankTile() {
        return tileForValue.get(0);
    }

    public boolean shift(ShiftDirection move) {
        Tile blankTile = getBlankTile();
        Tile neighbourToSwap = null;
        switch (move){
            case UP:
                neighbourToSwap = blankTile.getBottomTile();
                break;
            case DOWN:
                neighbourToSwap = blankTile.getUpTile();
                break;
            case LEFT:
                neighbourToSwap = blankTile.getRightTile();
                break;
            case RIGHT:
                neighbourToSwap = blankTile.getLeftTile();
                break;
        }
        if (neighbourToSwap != null){
            movesCount++;
            replaceTiles(blankTile, neighbourToSwap);
            return true;
        }
        return false;
    }

    private void replaceTiles(Tile firstTile, Tile secondTile){
        Position firstTilePos = firstTile.getPosition();
        Position secondTilePos = secondTile.getPosition();

        int firstTileValue = firstTile.getValue();
        int secondTileValue = secondTile.getValue();

        firstTile.setPosition(secondTilePos);
        firstTile.setValue(secondTileValue);

        secondTile.setPosition(firstTilePos);
        secondTile.setValue(firstTileValue);

        replaceTileForPosition(firstTilePos, secondTile);
        replaceTileForPosition(secondTilePos, firstTile);

        setNeighbourTiles(new ArrayList<>(tileForPosition.values()));
    }

    public void setNeighbourTiles(ArrayList<Tile> tiles){
        for (Tile tile : tiles){
            Position up = new Position(tile.getPosition().getX(), tile.getPosition().getY() - 1);
            tile.setUpTile(getTileForPosition(up));

            Position bottom = new Position(tile.getPosition().getX(), tile.getPosition().getY() + 1);
            tile.setBottomTile(getTileForPosition(bottom));

            Position left = new Position(tile.getPosition().getX() - 1, tile.getPosition().getY());
            tile.setLeftTile(getTileForPosition(left));

            Position right = new Position(tile.getPosition().getX() + 1, tile.getPosition().getY());
            tile.setRightTile(getTileForPosition(right));
        }
    }

    public Tile getTileForPosition(Position pos){
        for (Position position : tileForPosition.keySet()){
            if (pos.samePosition(position)){
                return tileForPosition.get(position);
            }
        }
        return null;
    }

    private void replaceTileForPosition(Position pos, Tile newTile){
        for (Position position : tileForPosition.keySet()){
            if (pos.samePosition(position)){
                tileForPosition.replace(position, newTile);
                break;
            }
        }
    }

    public void shuffle(ArrayList<ShiftDirection> moves){
        for (ShiftDirection move: moves){
            if (move != getReverseMove()){
                shift(move);
                previousMove = move;
            }
        }
        System.out.println(getBlankTile().getPosition());
        System.out.println(this);
        System.out.println("Total moves: " + movesCount);
        System.out.println("Done shuffling");
        movesCount = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                sb.append(getTileForPosition(new Position(x, y)) + " | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private ShiftDirection getReverseMove(){
        if (previousMove != null){
            switch (previousMove){
                case UP:
                    return ShiftDirection.DOWN;
                case DOWN:
                    return  ShiftDirection.UP;
                case LEFT:
                    return ShiftDirection.RIGHT;
                case RIGHT:
                    return ShiftDirection.LEFT;
            }
        }
        return null;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public int getSize() {
        return size;
    }
}
