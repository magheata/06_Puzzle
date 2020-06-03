/* Created by andreea on 26/05/2020 */
package Domain;

import java.util.ArrayList;

public class Tile implements Cloneable{

    private Position position;
    private int value, goalValue;
    private Tile leftTile, rightTile, upTile, bottomTile;

    public Tile(Position position, int value, int goalValue) {
        this.position = position;
        this.value = value;
        this.goalValue = goalValue;
    }

    public ArrayList<ShiftDirection> possibleMoveDirections(){
        ArrayList<ShiftDirection> possibleMoveDirections = new ArrayList<>();
        if (upTile != null){
            possibleMoveDirections.add(ShiftDirection.DOWN);
        }
        if (bottomTile != null){
            possibleMoveDirections.add(ShiftDirection.UP);
        }
        if (leftTile != null){
            possibleMoveDirections.add(ShiftDirection.RIGHT);
        }
        if (rightTile != null){
            possibleMoveDirections.add(ShiftDirection.LEFT);
        }
        return possibleMoveDirections;
    }

    public boolean equals (Tile tile){
        if (tile != null){
            return (this.position.getX() == tile.position.getX()) && (this.position.getY() == tile.position.getY());
        }
        return false;
    }

    public ShiftDirection isTileNeighbour(Tile tile){
        if (tile.equals(upTile)){
            return ShiftDirection.DOWN;
        }
        if (tile.equals(rightTile)){
            return ShiftDirection.LEFT;
        }
        if (tile.equals(bottomTile)){
            return ShiftDirection.UP;
        }
        if (tile.equals(leftTile)){
            return ShiftDirection.RIGHT;
        }
        return null;
    }

    @Override
    protected Object clone() {
        Tile clone;
        try
        {
            clone = (Tile) super.clone();
            //Copy new date object to cloned method
        }
        catch (CloneNotSupportedException e)
        {
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

    public void setGoalValue(int goalValue) {
        this.goalValue = goalValue;
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
    //endregion
}
