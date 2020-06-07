/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Domain;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Class that represents a Tile in the Board. The Tile has:
 *      - value: current value of the Tile
 *      - goalValue: desired value for the tile (for example value could be 1 but the goalValue might be 2)
 */
public class Tile implements Cloneable {

    private Position position;
    private int value, goalValue;
    private Tile leftTile, rightTile, upTile, bottomTile;
    private BufferedImage image;

    /**
     * Constructor used when the tile is numbered
     * @param position
     * @param value
     * @param goalValue
     */
    public Tile(Position position, int value, int goalValue) {
        this.position = position;
        this.value = value;
        this.goalValue = goalValue;
    }

    /**
     * Constructor used when the tile has an image
     * @param position
     * @param image
     * @param value
     * @param goalValue
     */
    public Tile(Position position, BufferedImage image, int value, int goalValue) {
        this.position = position;
        this.value = value;
        this.goalValue = goalValue;
        this.image = image;
    }

    /**
     * Method used to know in which directions the tile can move
     * @return list of moves that the Tile can make
     */
    public ArrayList<ShiftDirection> possibleMoveDirections() {
        ArrayList<ShiftDirection> possibleMoveDirections = new ArrayList<>();
        if (upTile != null) {
            possibleMoveDirections.add(ShiftDirection.DOWN);
        }
        if (bottomTile != null) {
            possibleMoveDirections.add(ShiftDirection.UP);
        }
        if (leftTile != null) {
            possibleMoveDirections.add(ShiftDirection.RIGHT);
        }
        if (rightTile != null) {
            possibleMoveDirections.add(ShiftDirection.LEFT);
        }
        return possibleMoveDirections;
    }

    public boolean equals(Tile tile) {
        if (tile != null) {
            return (this.position.getX() == tile.position.getX()) && (this.position.getY() == tile.position.getY());
        }
        return false;
    }

    /**
     * Method used to know if the tile passed through parameter is the neighbour of this tile
     * @param tile tile to check
     * @return move that can be made
     */
    public ShiftDirection isTileNeighbour(Tile tile) {
        if (tile.equals(upTile)) {
            return ShiftDirection.DOWN;
        }
        if (tile.equals(rightTile)) {
            return ShiftDirection.LEFT;
        }
        if (tile.equals(bottomTile)) {
            return ShiftDirection.UP;
        }
        if (tile.equals(leftTile)) {
            return ShiftDirection.RIGHT;
        }
        return null;
    }

    @Override
    protected Object clone() {
        Tile clone;
        try {
            clone = (Tile) super.clone();
            //Copy new date object to cloned method
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "[" + goalValue + ']';
    }

    //region SETTERS & GETTERS
    public Position getPosition() {
        return position;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getGoalValue() {
        return goalValue;
    }

    public Tile getLeftTile() {
        return leftTile;
    }

    public void setLeftTile(Tile leftTile) {
        this.leftTile = leftTile;
    }

    public Tile getRightTile() {
        return rightTile;
    }

    public void setRightTile(Tile rightTile) {
        this.rightTile = rightTile;
    }

    public Tile getUpTile() {
        return upTile;
    }

    public void setUpTile(Tile upTile) {
        this.upTile = upTile;
    }

    public Tile getBottomTile() {
        return bottomTile;
    }

    public void setBottomTile(Tile bottomTile) {
        this.bottomTile = bottomTile;
    }

    public Tile getSwappedTile(ShiftDirection move) {
        switch (move) {
            case UP:
                return bottomTile;
            case DOWN:
                return upTile;
            case LEFT:
                return rightTile;
            case RIGHT:
                return leftTile;
        }
        return null;
    }
    //endregion

}