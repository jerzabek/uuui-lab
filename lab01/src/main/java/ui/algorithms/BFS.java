package ui.algorithms;

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
    // Red, na čiji kraj ubacujemo nodeove
    // idemo po nodeovima, gledamo jesu li oni ciljni, ako jesu win ako ne idemo dalje i ubacujeo successore u red
    // Gleda se samo path length i found solution
  }
}
