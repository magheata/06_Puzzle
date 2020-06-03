package Application;/* Created by andreea on 26/05/2020 */

import Config.Constants;
import Domain.Board;
import Domain.Position;
import Domain.ShiftDirection;
import Domain.Tile;
import Infrastructure.SolutionService;
import Presentation.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {

    private int size = Constants.DEFAULT_BOARD_SIZE;
    private Window window;
    private Board board;
    private Presentation.Board boardView;
    private ArrayList<Tile> tiles;
    private SolutionService solutionService;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void prepare(BufferedImage image) {
        boardView.resetBoard();
        tiles = new ArrayList<>();
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                int goalValue = tiles.size() + 1;
                int value = tiles.size() + 1;
                if (tiles.size() == Math.pow(size, 2) - 1){
                    goalValue = 0;
                    value = 0;
                }
                Tile tile;
                if (image == null){
                    tile = new Tile(new Position(x, y), value, goalValue);
                } else {
                    tile = new Tile(new Position(x, y),
                            image.getSubimage(x * Constants.TILE_SIZE,
                                    y * Constants.TILE_SIZE,
                                    Constants.TILE_SIZE,
                                    Constants.TILE_SIZE),
                            value,
                            goalValue);
                }
                tiles.add(tile);
            }
        }
        board = new Board(tiles, size);
        board.setNeighbourTiles(tiles);
        boardView.initTiles(tiles);
    }

    public void shuffleTiles(){
        board.resetTiles();
        shuffleInitialTiles();
    }
    public void shuffleInitialTiles() {
        executor.submit(() -> {
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
            board.shuffle(moves);
            SwingUtilities.invokeLater(() -> {
                boardView.updateFigures(board.getTiles());
            });
        });
    }

    public void solvePuzzle(){
        executor.submit(() -> performAIMoves());
    }

    private void performAIMoves() {
        Board auxBoard = new Board(board.getTiles(), board.getSize());

        solutionService = new SolutionService(board.getTiles(), board.getSize());
        Future<ArrayList<ShiftDirection>> winningMoves = solutionService.getWinningNodes();
        while (!winningMoves.isDone()){}
        try {
            ArrayList<ShiftDirection> moves = winningMoves.get();
            System.out.println(winningMoves.get());
            for (ShiftDirection move : moves){
                auxBoard.shift(move);
                boardView.updateFigures(auxBoard.getTiles());
                Thread.sleep(500);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setBoardView(Presentation.Board boardView) {
        this.boardView = boardView;
    }

    public Object[] checkIfCanMove(int x, int y) {
        ShiftDirection move = board.getBlankTile().isTileNeighbour(board.getTileForPosition(new Position(x, y)));
        if (move != null){
            return new Object[] {move, new Position[]{board.getBlankTile().getPosition(), new Position(x, y)}};
        }
        return null;
    }

    public void swapTiles(ShiftDirection move, Position[] positions) {
        board.shift(move);
        System.out.println(board.getBlankTile().getPosition());
        System.out.println(board.getBlankTile().possibleMoveDirections());
        boardView.moveBlankTile(positions);
        boardView.updateTilePos(positions);
    }

    public void importImage() {
        JFileChooser fileChooser = new JFileChooser();
        switch (fileChooser.showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                try {
                    BufferedImage auxImage = ImageIO.read(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                    BufferedImage resized = new BufferedImage(Constants.WIDTH_BOARD, Constants.HEIGHT_BOARD, auxImage.getType());
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(auxImage, 0, 0, Constants.WIDTH_BOARD, Constants.HEIGHT_BOARD, 0, 0, auxImage.getWidth(),
                            auxImage.getHeight(), null);
                    g.dispose();
                    prepare(resized);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
