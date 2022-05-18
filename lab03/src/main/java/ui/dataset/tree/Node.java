package ui.dataset.tree;

import java.util.Map;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Node {

  public String feature;
  public Map<String, Node> subtrees;

  public Node(String feature, Map<String, Node> subtrees) {
    this.feature = feature;
    this.subtrees = subtrees;
  }
}
