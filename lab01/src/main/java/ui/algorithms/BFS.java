package ui.algorithms;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class BFS extends Algorithm {

  public BFS(String stateSpacePath) {
    super(stateSpacePath);

    System.out.println(Utility.COMMENT + " BFS");
  }

  @Override
  public void run() {
    String beginState = getBeginState();

    Node root = new Node(beginState);

    if (isGoalState(beginState)) {
      backtrack(root);
      return;
    }

    Deque<Node> open = new LinkedList<>();
    HashSet<String> closed = new HashSet<>();

    // We do not visit states whos nodes we've already visited

    open.add(root);

    while (!open.isEmpty()) {
      Node currentNode = open.removeFirst();
      statesVisited++;

      for (String state : getTransitions().get(currentNode.currentState).keySet()) {
        // The Nodes do not have a reference to the transitions so we do the dirty hashmap get here
        Node nextNode = new Node(state, currentNode, getTransitions().get(currentNode.currentState).get(state));

        if (isGoalState(state)) {
          backtrack(nextNode);
          return;
        }

        if (!closed.contains(nextNode.currentState)) {
          open.addLast(nextNode);
          closed.add(nextNode.currentState);
        }
      }
    }
  }

  /**
   * Stores algorithm execution results in required variables.s
   *
   * @param node The final node from which we will track to the root node
   */
  private void backtrack(Node node) {
    foundSolution = true;
    pathLength = node.depth;
    totalPath = node.path;
    totalCost = node.cost;
  }

  /**
   * Helper data structure, allows us to track a path from a leaf to the root of the search tree.
   */
  private static class Node {

    private final String currentState;
    private final String path;
    private final double cost;
    private final int depth;

    public Node(String current, Node node, double additionalCost) {
      this.currentState = current;
      this.path = node.path + " => " + current;
      this.cost = node.cost + additionalCost;
      this.depth = node.depth + 1;

    }

    public Node(String current) {
      this.currentState = current;
      this.path = current;
      this.cost = 0;
      this.depth = 1;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Node node = (Node) o;
      return currentState.equals(node.currentState);
    }

    @Override
    public int hashCode() {
      return Objects.hash(currentState);
    }
  }
}
