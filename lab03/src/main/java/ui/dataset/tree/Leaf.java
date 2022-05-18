package ui.dataset.tree;

import java.util.HashMap;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Leaf extends Node {

  public Leaf(String feature) {
    super(feature, new HashMap<>());
  }
}
