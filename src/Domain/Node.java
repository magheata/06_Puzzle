/* Created by andreea on 26/05/2020 */
package Domain;

import java.util.ArrayList;

public class Node {

    private ArrayList<Tile> grid;
    private int size;
    private ShiftDirection previousMove;
    private Node previousNode;
    private Board board;
    private int estimatedMinimumCost = Integer.MAX_VALUE;

    public Node(ArrayList<Tile> grid, int size) {
        this.grid = grid;
        this.size = size;
        this.board = new Board(grid, size);
    }

    public boolean isSolution(){
        return board.getIsWinning();
    }

    public int cost(){
        if (CostTranspositionTable.transpositionTable.get(this) == null){
            CostTranspositionTable.transpositionTable.put(this, board.getManhattanDistance());
        }
        return CostTranspositionTable.transpositionTable.get(this);
    }

    //region GETTERS & SETTERS
    public void setEstimatedMinimumCost(int estimatedMinimumCost) {
        this.estimatedMinimumCost = estimatedMinimumCost;
    }

    public int getEstimatedMinimumCost() {
        return estimatedMinimumCost;
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Tile> getGrid() {
        return grid;
    }

    public void setGrid(ArrayList<Tile> grid) {
        this.grid = grid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ShiftDirection getPreviousMove() {
        return previousMove;
    }

    public void setPreviousMove(ShiftDirection previousMove) {
        this.previousMove = previousMove;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
    //endregion
}
