package Application;/* Created by andreea on 26/05/2020 */

import Config.Constants;
import Domain.Board;
import Domain.Position;
import Domain.ShiftDirection;
import Domain.Tile;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    private int size = Constants.DEFAULT_BOARD_SIZE;
    private Board board;
    private ArrayList<Tile> tiles;

    public void start(){
        prepare();
        shuffleInitialTiles();
    }

    private void shuffleInitialTiles() {
        ArrayList<ShiftDirection> moves = new ArrayList<>();
        ArrayList<ShiftDirection> possibleMoves = new ArrayList<>(Arrays.asList(
                ShiftDirection.UP,
                ShiftDirection.DOWN,
                ShiftDirection.LEFT,
                ShiftDirection.RIGHT));
        for (int i = 0; i < size * 60; i++){
            ShiftDirection randomMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
            moves.add(randomMove);
        }
        performAIMoves(moves);
    }

    private void performAIMoves(ArrayList<ShiftDirection> moves) {
        board.shuffle(moves);
    }

    private void prepare() {
        tiles = new ArrayList<>();
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                int goalValue = tiles.size() + 1;
                int value = tiles.size() + 1;
                if (tiles.size() == Math.pow(size, 2) - 1){
                    goalValue = 0;
                    value = 0;
                }
                Tile tile = new Tile(new Position(x, y), value, goalValue);
                tiles.add(tile);
            }
        }
        board = new Board(tiles, size);
        board.setNeighbourTiles(tiles);
    }
}
