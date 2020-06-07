/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to save the cost of the Nodes
 */
public final class CostTranspositionTable {
    public static Map<Node, Integer> transpositionTable = new HashMap<>();
}
