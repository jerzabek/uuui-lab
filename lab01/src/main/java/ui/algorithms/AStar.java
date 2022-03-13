package ui.algorithms;

import java.util.HashMap;
import java.util.List;
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

    for(String line : heuristicDescriptorLines) {
      // All lines will have the following format:
      // state: heuristic_value
      String[] details = line.split(":\\s");

      heuristicValues.put(details[0], Integer.parseInt(details[1]));
    }

    System.out.println(Utility.COMMENT + " A-STAR " + heuristicDescriptorPath);
  }

  @Override
  public void run() {

  }
}
