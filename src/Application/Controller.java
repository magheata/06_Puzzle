/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */

package Application;

import Config.Constants;
import Domain.Board;
import Domain.Interfaces.IController;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements IController {
    private int size = Constants.DEFAULT_BOARD_SIZE;
    private Window window;
    private Board board;
    private Presentation.Board boardView;
    private ArrayList<Tile> tiles;
    private ArrayList<ShiftDirection> hintsMoves;
    private SolutionService solutionService;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean tilesShuffled = false;
    private boolean tilesMovedByUser = false;
    private boolean hintsEnabled;
    private int movesMadeByUser = 0;
    private boolean imageUsed = false;

    @Override
    public Object[] checkIfCanMove(int x, int y) {
        ShiftDirection move = board.getBlankTile().isTileNeighbour(board.getTileForPosition(new Position(x, y)));
        if (move != null){
            return new Object[] {move, new Position[]{board.getBlankTile().getPosition(), new Position(x, y)}};
        }
        return null;
    }

    @Override
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
                    imageUsed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void prepare(BufferedImage image) {
        executor.submit(() -> {
            boardView.resetBoard();
            initTiles(image);
            board = new Board(tiles, size);
            board.setNeighbourTiles(tiles);
            boardView.initTiles(tiles);
        });
    }

    @Override
    public void resetBoard() {
        boardView.resetBoard();
    }

    @Override
    public void shuffleTiles(){
        tilesShuffled = true;
        window.enableSolve(true);
        window.enableHint(true);
        board.resetTiles();
        shuffleInitialTiles();
    }

    @Override
    public void solvePuzzle(){
        if (!board.getIsWinning()){
            if (!hintsEnabled){
                SwingUtilities.invokeLater(() -> {
                    window.startProgressBar();
                    boardView.enableFigureButtons(false);
                });
                executor.submit(() -> playWinningMoves(performAIMoves()));
            } else {
                executor.submit(() -> playWinningMoves(hintsMoves));
                window.showHint(false);
            }
        }
    }

    @Override
    public void swapTiles(ShiftDirection move, Position[] positions) {
        executor.submit(() -> {
            if (!window.isHintEnabled()){
                window.enableHint(true);
            }
            if (!window.isSolveEnabled()){
                window.enableSolve(true);
            }
            tilesMovedByUser = true;
            movesMadeByUser++;

            board.shift(move);

            if (hintsEnabled){
                updateHints(move);
            }

            SwingUtilities.invokeLater(() -> {
                boardView.updateTilePos(positions);
                boardView.updateFiguresInPanels(board.getTiles());
                boardView.repaintFigures(board.getTiles());
            });

            if (board.getIsWinning()){
                window.showDialog("Solved in " + movesMadeByUser + " moves.", "Solved", JOptionPane.INFORMATION_MESSAGE);
                movesMadeByUser = 0;
                tilesShuffled = false;
                tilesMovedByUser = false;
                toggleHints();
                window.enableHint(false);
                window.enableSolve(false);
            }
        });
    }

    @Override
    public void toggleHints() {
        executor.submit(() -> {
            if (!hintsEnabled){
                if (tilesShuffled || tilesMovedByUser){
                    hintsEnabled = true;
                    SwingUtilities.invokeLater(() -> {
                        window.startProgressBar();
                    });
                    hintsMoves = performAIMoves();
                    SwingUtilities.invokeLater(() -> {
                        window.stopProgressBar();
                    });
                    if (!imageUsed){
                        window.editHint(board.getBlankTile().getSwappedTile(hintsMoves.get(0)).toString());
                    } else {
                        window.editHint(getReverseMove(hintsMoves.get(0)).toString());
                    }
                }
            } else {
                hintsEnabled = false;
            }
            window.showHint(hintsEnabled);
        });
    }

    //region PRIVATE METHODS
    private ShiftDirection getReverseMove(ShiftDirection move) {
        switch (move){
            case UP:
                return ShiftDirection.DOWN;
            case DOWN:
                return ShiftDirection.UP;
            case LEFT:
                return ShiftDirection.RIGHT;
            case RIGHT:
                return ShiftDirection.LEFT;
        }
        return null;
    }

    private void initTiles(BufferedImage image){
        tiles = new ArrayList<>();
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                Tile tile;
                int goalValue = tiles.size() + 1;
                int value = tiles.size() + 1;
                if (tiles.size() == Math.pow(size, 2) - 1){
                    goalValue = 0;
                    value = 0;
                }
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
    }

    private ArrayList<ShiftDirection> performAIMoves() {
        solutionService = new SolutionService(board.getTiles(), board.getSize());
        return solutionService.getWinningNodes();
    }

    private void playWinningMoves(ArrayList<ShiftDirection> winningMoves){
        SwingUtilities.invokeLater(() -> {
            window.stopProgressBar();
            window.enableHint(false);
        });
        if (winningMoves == null){
            SwingUtilities.invokeLater(() -> {
                window.showDialog("No solution found.", "Unsolved", JOptionPane.ERROR_MESSAGE);
            });
            executor.shutdown();
        } else {
            recreatePath(winningMoves);
            SwingUtilities.invokeLater(() -> {
                window.showDialog("Solved in " + winningMoves.size() + " moves.", "Solved", JOptionPane.INFORMATION_MESSAGE);
                boardView.enableFigureButtons(true);
            });
        }
        SwingUtilities.invokeLater(() -> {
            if (window.getProgressBar().isIndeterminate()){
                window.stopProgressBar();
            }
            window.enableHint(false);
            window.enableSolve(false);
        });
        tilesMovedByUser = false;
        tilesShuffled = false;
    }

    private void recreatePath(ArrayList<ShiftDirection> winningMoves){
        Board auxBoard = new Board(board.getTiles(), board.getSize());
        for (ShiftDirection move : winningMoves){
            if (tilesMovedByUser){
                Tile swappedTile = auxBoard.getBlankTile().getSwappedTile(move);
                boardView.updateTilePos(new Position[]{
                        auxBoard.getBlankTile().getPosition(),
                        swappedTile.getPosition()
                });
            }
            auxBoard.shift(move);
            SwingUtilities.invokeLater(() -> {
                boardView.updateFiguresInPanels(auxBoard.getTiles());
                boardView.repaintFigures(auxBoard.getTiles());
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        board = auxBoard;
    }

    private void shuffleInitialTiles() {
        executor.submit(() -> {
            ArrayList<ShiftDirection> moves = new ArrayList<>();
            for (int i = 0; i < size * 60; i++){
                ShiftDirection randomMove = Constants.POSSIBLE_MOVES.get((int) (Math.random() * Constants.POSSIBLE_MOVES.size()));
                moves.add(randomMove);
            }
            board.shuffle(moves);
            SwingUtilities.invokeLater(() -> {
                boardView.updateFiguresInPanels(board.getTiles());
                boardView.repaintFigures(board.getTiles());
            });
        });
    }

    public int getSize() {
        return size;
    }

    public void setBoardView(Presentation.Board boardView) {
        this.boardView = boardView;
    }

    public void setImageUsed(boolean imageUsed) {
        this.imageUsed = imageUsed;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private void updateHints(ShiftDirection move){
        if (tilesMovedByUser || tilesShuffled){
            if (hintsMoves.get(0).equals(move)){
                hintsMoves.remove(0);
                if (!hintsMoves.isEmpty()){
                    if (!imageUsed){
                        window.editHint(board.getBlankTile().getSwappedTile(hintsMoves.get(0)).toString());
                    } else {
                        window.editHint(getReverseMove(hintsMoves.get(0)).toString());
                    }
                }
            }
            else {
                hintsMoves.add(0, getReverseMove(move));
                if (!imageUsed){
                    window.editHint(board.getBlankTile().getSwappedTile(hintsMoves.get(0)).toString());
                } else {
                    window.editHint(getReverseMove(hintsMoves.get(0)).toString());
                }
            }
        }
    }
    //endregion
}
