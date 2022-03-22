package ui.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class AStar extends Algorithm {

  private final HashMap<String, Integer> heuristicValues = new HashMap<>();

  public AStar(String stateSpacePath, String heuristicDescriptorPath) {
    super(stateSpacePath);

    if (heuristicDescriptorPath == null) {
      System.out.println("No heuristic descriptor path provided");
      System.exit(1);
    }

    List<String> heuristicDescriptorLines = Utility.readLines(heuristicDescriptorPath);

    for (String line : heuristicDescriptorLines) {
      // All lines will have the following format:
      // state: heuristic_value
      String[] details = line.split(":\\s");

      heuristicValues.put(details[0], Integer.parseInt(details[1]));
    }

    System.out.println(Utility.COMMENT + " A-STAR " + heuristicDescriptorPath);
  }

  @Override
  public void run() {
    Queue<Node> open = new PriorityQueue<>(Node.nodeComparator);
    Set<String> visited = new HashSet<>();
    Map<String, Node> openIndex = new HashMap<>();

    open.add(new Node(null, getBeginState(), 0, heuristicValues.get(getBeginState())));

    while (!open.isEmpty()) {
      Node currentNode = open.remove();
      statesVisited++;

      if (isGoalState(currentNode.state)) {
        backtrack(currentNode);
        return;
      }

      visited.add(currentNode.state);

      for (Entry<String, Double> child : getTransitions().get(currentNode.state).entrySet()) {
        if (visited.contains(child.getKey())) {
          continue;
        }

        double cost = currentNode.cost + child.getValue();
        double total = cost + heuristicValues.get(child.getKey());

        boolean openHasCheaper = false;

        // We use helper data structure to allow us O(1) access to other nodes with the same state, in order to compare prices
        if (openIndex.containsKey(child.getKey())) {
          Node sameNode = openIndex.get(child.getKey());

          if (total < sameNode.totalEstimatedCost) {
            open.remove(sameNode);
          } else {
            openHasCheaper = true;
          }
        }

        if (!openHasCheaper) {
          Node newNode = new Node(currentNode, child.getKey(), cost, total);
          open.add(newNode);
          openIndex.put(newNode.state, newNode);
        }
      }
    }
  }

  private void backtrack(Node currentNode) {
    foundSolution = true;
    totalCost = currentNode.cost;
    pathLength = 0;
    List<String> path = new ArrayList<>();

    do {
      pathLength++;
      path.add(currentNode.state);

      currentNode = currentNode.parent;
    } while (currentNode != null);

    Collections.reverse(path);
    totalPath = String.join(" => ", path);
  }

  private static class Node {

    private final Node parent;
    private final String state;
    private final double cost, totalEstimatedCost;
    public static final Comparator<Node> nodeComparator = Comparator.comparing(Node::getTotalEstimatedCost).thenComparing(Node::getState);

    public Node(Node parent, String state, double cost, double totalEstimatedCost) {
      this.parent = parent;
      this.state = state;
      this.cost = cost;
      this.totalEstimatedCost = totalEstimatedCost;
    }

    public String getState() {
      return state;
    }

    public double getTotalEstimatedCost() {
      return totalEstimatedCost;
    }
  }
}
