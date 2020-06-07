/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Infrastructure;

import Domain.Node;
import Domain.ShiftDirection;
import Domain.Tile;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that calculates the moves needed to solve the puzzle. It uses the A* algorithm and uses as cost function the
 * Manhattan distance.
 */
public class SolutionService {

    private Node startingNode;

    public SolutionService(ArrayList<Tile> grid, int size) {
        this.startingNode = new Node(grid, size);
    }

    /**
     * Method used to return the list of moves needed to solve the Puzzle
     * @return list of moves
     */
    public ArrayList<ShiftDirection> getWinningNodes(){
        return idaWinningMoves();
    }

    /**
     * Calculates the winning moves.
     * @return list of moves
     */
    private ArrayList<ShiftDirection> idaWinningMoves() {
        try{
            int threshold = startingNode.cost();
            Node node = startingNode;
            // If node is not solution, which means the board is not solved, we keep looking
            while(!node.isSolution()){
                node = ida(startingNode, 0, threshold);
                // Update the cost
                if (!node.isSolution()){
                    threshold = node.getEstimatedMinimumCost();
                }
            }
            // Get the winning moves
            ArrayList<ShiftDirection> moves = recreatePathFrom(node);
            Collections.reverse(moves);
            return moves;
        } catch (OutOfMemoryError error){
        }
        return null;
    }

    /**
     * Recursive method used to
     * @param node
     * @param cost
     * @param threshold
     * @return
     */
    private Node ida (Node node, int cost, int threshold){
        int estimatedCost = node.cost() + cost;
        // If we have reached a solution or the cost is bigger than the threshold we return the node
        if (node.isSolution() || estimatedCost > threshold){
            node.setEstimatedMinimumCost(estimatedCost);
            return node;
        }
        int mimimumCost = Integer.MAX_VALUE;
        // get the blank tile
        Tile blankTile = node.getBoard().getBlankTile();
        // For every move that the blank tile can make we create a child node
        for (ShiftDirection move : getPossibleMovesWithoutReversals(node, blankTile)){
            // Create child node which will have the outcome of making the move
            Node childNode = getChildOfNodeAndMove(node, move);
            // Recursive call to keep calculating the child nodes
            Node childIda = ida(childNode, cost + 1, threshold);
            // If the child Node is solution we return it
            if (childIda.isSolution()){
                return childIda;
            }
            // If the cost of the childNode is less than the minimumCost we update the minimum cost
            if (childIda.getEstimatedMinimumCost() < mimimumCost){
                mimimumCost = childIda.getEstimatedMinimumCost();
            }
        }
        // Set the minimum cost for the node
        node.setEstimatedMinimumCost(mimimumCost);
        return node;
    }

    /**
     * Method used to obtain the moves we can make excluding the reverse of the previously made move.
     * @param node
     * @param blankTile
     * @return list of moves
     */
    private ArrayList<ShiftDirection> getPossibleMovesWithoutReversals(Node node, Tile blankTile) {
        ArrayList<ShiftDirection> possibleMoves = blankTile.possibleMoveDirections();
        ShiftDirection reverseMove = null;
        ShiftDirection previousMove = node.getPreviousMove(); /*Last move made*/
        if (previousMove != null){
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
            // If the list contains the reverse of the last move made we delete it
            if (possibleMoves.contains(reverseMove)){
                possibleMoves.remove(reverseMove);
            }
        }
        return possibleMoves;
    }

    /**
     * Method used to obtain the moves from all the nodes in order to recreate all the moves made to solve the puzzle.
     * @param node
     * @return
     */
    private ArrayList<ShiftDirection> recreatePathFrom(Node node) {
        return getMovesFrom(new ArrayList<>(), node);
    }

    /**
     * Method that given a node it obtains the node's previous move made and adds it to the list
     * @param moves
     * @param node
     * @return
     */
    private ArrayList<ShiftDirection> getMovesFrom(ArrayList<ShiftDirection> moves, Node node){
        // While there are nodes and moves
        if((node.getPreviousMove() != null) && (node.getPreviousNode() != null)){
            moves.add(node.getPreviousMove()); // we add the move to the list
            return getMovesFrom(moves, node.getPreviousNode());
        } else {
            // Return the list of moves
            return moves;
        }
    }

    /**
     * Method that creates a new node given a Parent node and a specific move
     * @param node
     * @param move
     * @return
     */
    private Node getChildOfNodeAndMove(Node node, ShiftDirection move){
        Node childNode = new Node(node.getGridClone(), node.getSize()); // Clone the state of the node
        childNode.getBoard().shift(move); // shift the childNode's board with the given move
        // Update parent node and last move made of the childNode
        childNode.setPreviousMove(move);
        childNode.setPreviousNode(node);
        return childNode;
    }
}
