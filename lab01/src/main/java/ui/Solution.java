package ui;

import static ui.util.Utility.ALG_ASTAR;
import static ui.util.Utility.ALG_BFS;
import static ui.util.Utility.ALG_UCS;
import static ui.util.Utility.FLAG_ALGORITHM;
import static ui.util.Utility.FLAG_STATE_SPACE;

import java.util.HashMap;
import ui.algorithms.AStar;
import ui.algorithms.Algorithm;
import ui.algorithms.BFS;
import ui.algorithms.UCS;
import ui.util.Utility;

public class Solution {

  public static void main(String... args) {
    if (args.length < 2 || args.length % 2 != 0) {
      System.out.println("Not enough arguments");
      System.exit(1);
      return;
    }

    HashMap<String, String> arguments = new HashMap<>();

    for (int i = 0; i < args.length; i += 2) {
      // We pair up the arguments with their values
      arguments.put(args[i], args[i + 1]);
    }

    // Required arguments
    if (!arguments.containsKey(FLAG_ALGORITHM) || !arguments.containsKey(FLAG_STATE_SPACE)) {
      System.out.println("Missing arguments");
      System.exit(1);
      return;
    }

    String selectedAlgorithmName = arguments.get(FLAG_ALGORITHM);
    String stateSpacePath = arguments.get(FLAG_STATE_SPACE);
    String heuristicDescriptorPath = arguments.get(Utility.FLAG_HEURISTIC_DESCRIPTOR);

    // We select an algorithm implementing a strategy design pattern
    Algorithm selectedAlgorithm = switch (selectedAlgorithmName) {
      case ALG_BFS -> new BFS(stateSpacePath);
      case ALG_UCS -> new UCS(stateSpacePath);
      case ALG_ASTAR -> new AStar(stateSpacePath, heuristicDescriptorPath);
      default -> {
        System.out.println("Invalid algorithm " + selectedAlgorithmName);
        System.exit(1);
        yield null;
      }
    };

    selectedAlgorithm.run();
    System.out.println(selectedAlgorithm);
  }

}
