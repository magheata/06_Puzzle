package Application;/* Created by andreea on 26/05/2020 */

import Config.Constants;
import Domain.Board;
import Domain.Position;
import Domain.ShiftDirection;
import Domain.Tile;
import Infrastructure.SolutionService;
import Presentation.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Controller {

    private int size = Constants.DEFAULT_BOARD_SIZE;
    private Window window;
    private Board board;
    private ArrayList<Tile> tiles;
    private SolutionService solutionService;

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
        solutionService = new SolutionService(board.getTiles(), board.getSize());
        Future<ArrayList<ShiftDirection>> winningMoves = solutionService.getWinningNodes();
        while (!winningMoves.isDone()){}
        try {
            System.out.println(winningMoves.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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


    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
