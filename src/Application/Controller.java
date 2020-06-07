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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that acts as the controller of the application. It is used in order to manage the communications between the
 * Model and the View. It implemenets the IController interface.
 */
public class Controller implements IController {
    private int size = Constants.DEFAULT_BOARD_SIZE;

    private Window window;
    private Board board;
    private Presentation.Board boardView;
    private SolutionService solutionService;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ArrayList<Tile> tiles; /*List of the tiles in the puzzle*/
    private ArrayList<ShiftDirection> hintsMoves; /*Stores the different moves neccesary to solve the puzzle*/

    private boolean tilesShuffled = false;
    private boolean tilesMovedByUser = false;
    private boolean hintsEnabled;
    private boolean imageUsed = false;

    private int movesMadeByUser = 0; /*Used to know how many moves the user has made when swapping the tiles*/

    /**
     * Used to know if a selected tile can be swapped with the blank tile. In order for that to happen the tile in the
     * (x, y) position in the board has to be the neighbour of the blank tile. It returns an array of objects:
     *      - first position contains the move made
     *      - second position contains array with the positions of the tile and the blank tile
     * @param x x coordinate of the tile to check
     * @param y y coordinate of the tile to check
     * @return object array which contains the move and the positions of the tile and blank tile, null otherwise
     */
    @Override
    public Object[] checkIfCanMove(int x, int y) {
        ShiftDirection move = board.getBlankTile().isTileNeighbour(board.getTileForPosition(new Position(x, y)));
        if (move != null){
            return new Object[] {move, new Position[]{board.getBlankTile().getPosition(), new Position(x, y)}};
        }
        return null;
    }

    /**
     * Imports an image chosen by the user and divides it into tiles. The image is resized in order to fit the board and
     * tile's dimensions.
     */
    @Override
    public void importImage() {
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG", "jpg"));

        switch (fileChooser.showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                try {
                    BufferedImage auxImage = ImageIO.read(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                    BufferedImage resized = new BufferedImage(Constants.WIDTH_BOARD, Constants.HEIGHT_BOARD, auxImage.getType());
                    Graphics2D g = resized.createGraphics();
                    // Resize the image to the board's dimension
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(auxImage, 0, 0, Constants.WIDTH_BOARD, Constants.HEIGHT_BOARD, 0, 0, auxImage.getWidth(),
                            auxImage.getHeight(), null);
                    g.dispose();
                    // Create the board with the image tiles
                    prepare(resized);
                    imageUsed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Initilizes the board's tiles.
     * @param image Image used, can be null
     */
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

    /**
     * Resets the Board's View
     */
    @Override
    public void resetBoard() {
        boardView.resetBoard();
    }

    /**
     * Shuffles the tiles of the Board
     */
    @Override
    public void shuffleTiles(){
        tilesShuffled = true;
        window.enableSolveInMenu(true);
        window.enableHintInMenu(true);
        board.resetTiles();
        shuffleInitialTiles();
    }

    /**
     * Solves the puzzle
     */
    @Override
    public void solvePuzzle(){
        // If the board is not solved
        if (!board.getIsSolved()){
            // If there are no hints enabled it means that we have to calculate the moves
            if (!hintsEnabled){
                SwingUtilities.invokeLater(() -> {
                    window.startProgressBar();
                    boardView.enableFigureButtons(false);
                });
                // Invoke the method that calculates the moves to make
                executor.submit(() -> playWinningMoves(performAIMoves()));
            } else {
                // If hints are enabled it means that we already have the moves, so we only display them
                executor.submit(() -> playWinningMoves(hintsMoves));
                window.showHint(false);
            }
        }
    }

    /**
     * Used to swap two tiles after the user clicks on a tile
     * @param move
     * @param positions
     */
    @Override
    public void swapTiles(ShiftDirection move, Position[] positions) {
        executor.submit(() -> {
            // When we swap the tiles we can enable the solve and show hints options
            if (!window.isHintEnabled()){
                window.enableHintInMenu(true);
            }
            if (!window.isSolveEnabled()){
                window.enableSolveInMenu(true);
            }
            tilesMovedByUser = true;
            movesMadeByUser++;

            // We make the move
            board.shift(move);

            // If the hints are enabled we update them accordingly
            if (hintsEnabled){
                updateHints(move);
            }

            // Update the Board's View with the new positions of the Tiles
            SwingUtilities.invokeLater(() -> {
                boardView.updateTilePos(positions);
                boardView.updateFiguresInPanels(board.getTiles());
                boardView.repaintFigures(board.getTiles());
            });


            // If we have solved the board we notify it and reset the variables
            if (board.getIsSolved()){
                window.showDialog("Solved in " + movesMadeByUser + " moves.", "Solved", JOptionPane.INFORMATION_MESSAGE);
                movesMadeByUser = 0;
                tilesShuffled = false;
                tilesMovedByUser = false;
                // Disable the hints because the board is solved
                toggleHints();
                window.enableHintInMenu(false);
                window.enableSolveInMenu(false);
            }
        });
    }

    /**
     * Method used when the hints are enabled/disabled
     */
    @Override
    public void toggleHints() {
        executor.submit(() -> {
            // If we enable the hints
            if (!hintsEnabled){
                /* If the user has shuffled or swapped the tiles we can provide hints because it means the board is not
                   solved */
                if (tilesShuffled || tilesMovedByUser){
                    hintsEnabled = true;
                    SwingUtilities.invokeLater(() -> {
                        window.startProgressBar();
                    });
                    // Calculate the moves
                    hintsMoves = performAIMoves();
                    SwingUtilities.invokeLater(() -> {
                        window.stopProgressBar();
                    });
                    /* If we are using an image we display the hints as ShiftDirection (UP, DOWN, LEFT, RIGHT); otherwise,
                       we use the Tile's value */
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

    /**
     * Method that given a move returns its reverse.
     * @param move move given
     * @return reverse of the move
     */
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

    /**
     * Initializes the Tiles of the Board. If an image is provided we assign the Tile its corresponding subimage.
     * @param image
     */
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

    /**
     * Method used to call the method that calculates the moves needed to solve the Puzzle.
     * @return
     */
    private ArrayList<ShiftDirection> performAIMoves() {
        window.enableSolveInMenu(false);
        solutionService = new SolutionService(board.getTiles(), board.getSize());
        window.enableSolveInMenu(true);
        return solutionService.getWinningNodes();
    }

    /**
     * Method used to display the movements made to solve the puzzle.
     * @param winningMoves list of the different moves needed to solve the puzzle
     */
    private void playWinningMoves(ArrayList<ShiftDirection> winningMoves){
        SwingUtilities.invokeLater(() -> {
            window.stopProgressBar();
            window.enableHintInMenu(false);
        });
        // If list is empty it means that the puzzle could not be solved
        if (winningMoves == null){
            SwingUtilities.invokeLater(() -> {
                window.showDialog("No solution found.", "Unsolved", JOptionPane.ERROR_MESSAGE);
            });
            executor.shutdown();
        } else {
            // Else, we recreate the moved in the View
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
            window.enableHintInMenu(false);
            window.enableSolveInMenu(false);
        });
        tilesMovedByUser = false;
        tilesShuffled = false;
    }

    /**
     * Method used to display the moves made to solve the puzzle
     * @param winningMoves list of the moves to make
     */
    private void recreatePath(ArrayList<ShiftDirection> winningMoves){
        Board auxBoard = new Board(board.getTiles(), board.getSize());
        for (ShiftDirection move : winningMoves){
            // If tile was swapped we update the tile's position
            if (tilesMovedByUser){
                Tile swappedTile = auxBoard.getBlankTile().getSwappedTile(move);
                boardView.updateTilePos(new Position[]{
                        auxBoard.getBlankTile().getPosition(),
                        swappedTile.getPosition()
                });
            }
            // We make the move
            auxBoard.shift(move);

            // And update the board to display the move
            SwingUtilities.invokeLater(() -> {
                boardView.updateFiguresInPanels(auxBoard.getTiles());
                boardView.repaintFigures(auxBoard.getTiles());
            });
            try {
                Thread.sleep(Constants.PAUSE_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        board = auxBoard;
    }

    /**
     * Method used to shuffle the initial tiles of the board
     */
    private void shuffleInitialTiles() {
        executor.submit(() -> {
            ArrayList<ShiftDirection> moves = new ArrayList<>();
            // We create 180 random moves to suffle
            for (int i = 0; i < size * 60; i++){
                ShiftDirection randomMove = Constants.POSSIBLE_MOVES.get((int) (Math.random() * Constants.POSSIBLE_MOVES.size()));
                moves.add(randomMove);
            }
            // We shuffle the board
            board.shuffle(moves);
            // Update the Board's View
            SwingUtilities.invokeLater(() -> {
                boardView.updateFiguresInPanels(board.getTiles());
                boardView.repaintFigures(board.getTiles());
            });
        });
    }

    /**
     * Method used to update the hints
     * @param move
     */
    private void updateHints(ShiftDirection move){
        // If board was modified
        if (tilesMovedByUser || tilesShuffled){
            // if the move made was taken from the hints we delete it and update the list
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
            // If another move was made we add its reverse to the start of the hints' list
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

    //region GETTERS & SETTERS
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
    //endregion
}
