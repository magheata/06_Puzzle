/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents the whole Puzzle.
 */
public class Board {

    private ArrayList<Tile> tiles; /*List of tiles of*/
    private Map<Integer, Tile> tileForValue; /*Maps the Tile for the values*/
    private Map<Position, Tile> tileForPosition; /*Maps the Tile for the Positions*/
    private int size;
    private ShiftDirection previousMove;

    public Board(ArrayList<Tile> tiles, int size) {
        this.tiles = tiles;
        this.size = size;
        initTileVariables();
    }

    /**
     * Initalizes the Tile's variables
     */
    private void initTileVariables() {
        tileForValue = new HashMap<>();
        tileForPosition = new HashMap<>();
        for (Tile tile: tiles){
            tileForValue.put(tile.getGoalValue(), tile);
            tileForPosition.put(tile.getPosition(), tile);
        }
    }

    /**
     * Resets the Tiles
     */
    public void resetTiles() {
        initTileVariables(); /*Initialize the tiles again*/
        setNeighbourTiles(tiles); /*Set the neighbour Tiles*/
    }

    /**
     * Return wether all the Tiles have the desired values or not
     * @return
     */
    public boolean getIsSolved() {
        for (Tile tile: tiles){
            if (tile.getValue() != tile.getGoalValue()){
                return false;
            }
        }
        return true;
    }

    /**
     * Computes the Manhattan distance of the whole board
     * @return
     */
    public int getManhattanDistance() {
        int manhattanDistance = 0;
        //For every tile we compute the manhattan distance and add it to the whole cost
        for (Tile tile : tiles){
            Tile goal = tileForValue.get(tile.getGoalValue());
            manhattanDistance += countManhattanDistance(tile, goal);
        }
        return manhattanDistance;
    }

    /**
     * Computes the manhattan distance between two tiles
     * @param firstTile
     * @param secondTile
     * @return cost
     */
    private int countManhattanDistance(Tile firstTile, Tile secondTile) {
        int manhattanDistance = 0;
        manhattanDistance += Math.abs(firstTile.getPosition().getX() - secondTile.getPosition().getX());
        manhattanDistance += Math.abs(firstTile.getPosition().getY() - secondTile.getPosition().getY());
        return manhattanDistance;
    }

    /**
     * Returns the blank tile of the Board
     * @return tile with value 0
     */
    public Tile getBlankTile() {
        return tileForValue.get(0);
    }

    /**
     * Method used to shift the position of the Tiles
     * @param move move to shift
     * @return wether or not the shift was possible
     */
    public boolean shift(ShiftDirection move) {
        Tile blankTile = getBlankTile();
        Tile neighbourToSwap = null;
        // For the given move we get the blank tile's neighbour
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
        // If it's not null it means the move can be made
        if (neighbourToSwap != null){
            // Make the move and update the tiles
            replaceTiles(blankTile, neighbourToSwap);
            return true;
        }
        // Otherwise, move can't be made
        return false;
    }

    /**
     * Method used to swap two tiles
     * @param firstTile
     * @param secondTile
     */
    private void replaceTiles(Tile firstTile, Tile secondTile){
        Position firstTilePos = firstTile.getPosition();
        Position secondTilePos = secondTile.getPosition();

        int firstTileValue = firstTile.getValue();
        int secondTileValue = secondTile.getValue();

        /*For each tile we set the position to the other's position*/
        firstTile.setPosition(secondTilePos);
        firstTile.setValue(secondTileValue);

        secondTile.setPosition(firstTilePos);
        secondTile.setValue(firstTileValue);

        // Update the list of tiles for position
        replaceTileForPosition(firstTilePos, secondTile);
        replaceTileForPosition(secondTilePos, firstTile);

        // Update the neighbours for each Tile
        setNeighbourTiles(new ArrayList<>(tileForPosition.values()));
    }

    /**
     * Method used to update the neighbour's of every Tile
     * @param tiles
     */
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

    /**
     * Method used to obtain the Tile for the given position
     * @param pos position wanted
     * @return Tile for the position
     */
    public Tile getTileForPosition(Position pos){
        for (Position position : tileForPosition.keySet()){
            if (pos.samePosition(position)){
                return tileForPosition.get(position);
            }
        }
        return null;
    }

    /**
     * Add the given tile to the position
     * @param pos position in the board
     * @param newTile new tile
     */
    private void replaceTileForPosition(Position pos, Tile newTile){
        for (Position position : tileForPosition.keySet()){
            if (pos.samePosition(position)){
                tileForPosition.replace(position, newTile);
                break;
            }
        }
    }

    /**
     * Method used to shuffle the board's tiles
     * @param moves
     */
    public void shuffle(ArrayList<ShiftDirection> moves){
        for (ShiftDirection move: moves){
            // If the move if not the reverse of the previous move we apply it;
            if (move != getReverseMove()){
                shift(move);
                previousMove = move;
            }
        }
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

    /**
     * Method used to get the reverse move of the previous move made
     * @return
     */
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
