package ui.algorithms;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import ui.util.Utility;

/**
 * Uniform cost search algorithm.
 *
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class UCS extends Algorithm {

  public UCS(String stateSpacePath) {
    super(stateSpacePath);

    System.out.println(Utility.COMMENT + " UCS");
  }

  @Override
  public void run() {
    Queue<Node> open = new PriorityQueue<>(Node.nodeComparator);
    Set<String> visited = new HashSet<>();
    Map<String, Node> openIndex = new HashMap<>();

    open.add(new Node(getBeginState()));

    while (!open.isEmpty()) {
      Node n = open.remove();
      statesVisited++;

      if (isGoalState(n.state)) {
        complete(n);
        return;
      }

      visited.add(n.state);

      if (!getTransitions().containsKey(n.state)) {
        continue;
      }

      for (Entry<String, Double> state : getTransitions().get(n.state).entrySet()) {
        if (visited.contains(state.getKey())) {
          continue;
        }

        double childPathCost = n.getCost() + state.getValue();
        boolean openHasCheaper = false;

        // We use helper data structure to allow us O(1) access to other nodes with the same state, in order to compare prices
        if (openIndex.containsKey(state.getKey())) {
          Node sameNode = openIndex.get(state.getKey());

          if (childPathCost < sameNode.getCost()) {
            open.remove(sameNode);
          } else {
            openHasCheaper = true;
          }
        }

        if (!openHasCheaper) {
          Node newNode = new Node(state.getKey(), n, childPathCost);
          open.add(newNode);
          openIndex.put(newNode.getState(), newNode);
        }
      }
    }
  }

  private void complete(Node node) {
    foundSolution = true;
    totalCost = node.cost;
    pathLength = node.depth;
    totalPath = node.path;
  }

  private static class Node {

    private final String state;
    private final double cost;
    private final String path;
    private final int depth;
    public static final Comparator<Node> nodeComparator = Comparator.comparing(Node::getCost).thenComparing(Node::getState);

    public Node(String state, Node node, double cost) {
      this.state = state;
      this.cost = cost;
      this.path = node.path + " => " + state;
      this.depth = node.depth + 1;
    }

    public Node(String state) {
      this.state = state;
      this.cost = 0;
      this.path = state;
      this.depth = 1;
    }

    public String getState() {
      return state;
    }

    public double getCost() {
      return cost;
    }
  }
}
