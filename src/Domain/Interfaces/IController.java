package Domain.Interfaces;

import Domain.Position;
import Domain.ShiftDirection;

import java.awt.image.BufferedImage;

public interface IController {
    Object[] checkIfCanMove(int x, int y);
    void importImage();
    void prepare(BufferedImage image);
    void resetBoard();
    void shuffleTiles();
    void solvePuzzle();
    void swapTiles(ShiftDirection move, Position[] positions);
    void toggleHints();
}
