/* Created by andreea on 26/05/2020 */
package Infrastructure;

import Domain.Node;
import Domain.ShiftDirection;
import Domain.Tile;

import java.util.ArrayList;

public class SolutionService {

    private Node startingNode;

    public SolutionService(ArrayList<Tile> grid, int size) {
        this.startingNode = new Node(grid, size);
    }

    public ArrayList<ShiftDirection> getWinningNodes(){
        return idaWinningMoves();
    }

    private ArrayList<ShiftDirection> idaWinningMoves() {
        int threshold = startingNode.cost();
        Node node = startingNode;

        while(!node.isSolution()){
            node = ida(startingNode, 0, threshold);
            if (!node.isSolution()){
                threshold = node.getEstimatedMinimumCost();
            }
        }
        return recreatePathFrom(node);
    }

    private Node ida (Node node, int cost, int threshold){
        int estimatedCost = node.cost() + cost;
        if (node.isSolution() || estimatedCost > threshold){
            node.setEstimatedMinimumCost(estimatedCost);
            return node;
        }
        int mimimumCost = Integer.MAX_VALUE;
        Tile blankTile = node.getBoard().getBlankTile();
        for (ShiftDirection move : getPossibleMovesWithoutReversals(node, blankTile)){
            Node childNode = getChildOfNodeAndMove(node, move);
            Node childIda = ida(childNode, cost + 1, threshold);
            if (childIda.isSolution()){
                return childIda;
            } if (childIda.getEstimatedMinimumCost() < mimimumCost){
                mimimumCost = childIda.getEstimatedMinimumCost();
            }
        }
        node.setEstimatedMinimumCost(mimimumCost);
        return node;
    }

    private ArrayList<ShiftDirection> getPossibleMovesWithoutReversals(Node node, Tile blankTile) {
        ArrayList<ShiftDirection> possibleMoves = blankTile.possibleMoveDirections();
        ShiftDirection reverseMove = null;

        ShiftDirection previousMove = node.getPreviousMove();

        switch (previousMove){
            case UP:
                reverseMove = ShiftDirection.DOWN;
                break;
            case DOWN:
                reverseMove = ShiftDirection.UP;
                break;
            case LEFT:
                reverseMove = ShiftDirection.RIGHT;
                break;
            case RIGHT:
                reverseMove = ShiftDirection.LEFT;
                break;
        }

        if (possibleMoves.contains(reverseMove)){
            possibleMoves.remove(reverseMove);
        }
        return possibleMoves;
    }

    private ArrayList<ShiftDirection> recreatePathFrom(Node node) {
        return getMovesFrom(new ArrayList<>(), node);
    }

    private ArrayList<ShiftDirection> getMovesFrom(ArrayList<ShiftDirection> moves, Node node){
        if((node.getPreviousMove() != null) && (node.getPreviousNode() != null)){
            moves.add(node.getPreviousMove());
            return getMovesFrom(moves, node.getPreviousNode());
        } else {
            return moves;
        }
    }

    private Node getChildOfNodeAndMove(Node node, ShiftDirection move){
        Node childNode = new Node(new ArrayList<>(node.getGrid()), node.getSize());
        childNode.getBoard().shift(move);
        childNode.setPreviousMove(move);
        childNode.setPreviousNode(node);
        return childNode;
    }
}
