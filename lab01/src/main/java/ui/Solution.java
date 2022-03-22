package ui;

import static ui.util.Utility.ALG_ASTAR;
import static ui.util.Utility.ALG_BFS;
import static ui.util.Utility.ALG_UCS;
import static ui.util.Utility.FLAG_ALGORITHM;
import static ui.util.Utility.FLAG_STATE_SPACE;

import java.util.HashMap;
import ui.algorithms.Algorithm;
import ui.algorithms.check.HeuristicConsistency;
import ui.algorithms.check.HeuristicOptimism;
import ui.algorithms.search.AStar;
import ui.algorithms.search.BFS;
import ui.algorithms.search.UCS;
import ui.util.Utility;

public class Solution {

  public static void main(String... args) {
    HashMap<String, String> arguments = new HashMap<>();

    for (int i = 0; i < args.length; i += 2) {
      // We pair up the arguments with their values
      // Arguments without values are possible

      if (args[i].equals(Utility.FLAG_CHECK_OPTIMISTIC) || args[i].equals(Utility.FLAG_CHECK_CONSISTENT)) {
        arguments.put(args[i], args[i]);
        i--; // In the next iteration we will increase i by 2, placing us on the next argument
        continue;
      }

      arguments.put(args[i], args[i + 1]);
    }

    String selectedAlgorithmName = arguments.get(FLAG_ALGORITHM);
    String stateSpacePath = arguments.get(FLAG_STATE_SPACE);
    String heuristicDescriptorPath = arguments.get(Utility.FLAG_HEURISTIC_DESCRIPTOR);

    boolean checkOptimistic = arguments.containsKey(Utility.FLAG_CHECK_OPTIMISTIC);
    boolean checkConsistent = arguments.containsKey(Utility.FLAG_CHECK_CONSISTENT);

    Algorithm selectedAlgorithm;

    if (checkOptimistic) {
      selectedAlgorithm = new HeuristicOptimism(stateSpacePath, heuristicDescriptorPath);

    } else if (checkConsistent) {
      selectedAlgorithm = new HeuristicConsistency(stateSpacePath, heuristicDescriptorPath);

    } else {
      // We select an algorithm implementing a strategy design pattern
      selectedAlgorithm = switch (selectedAlgorithmName) {
        case ALG_BFS -> new BFS(stateSpacePath);
        case ALG_UCS -> new UCS(stateSpacePath);
        case ALG_ASTAR -> new AStar(stateSpacePath, heuristicDescriptorPath);
        default -> {
          System.out.println("Invalid algorithm " + selectedAlgorithmName);
          System.exit(1);
          yield null;
        }
      };
    }

    selectedAlgorithm.run();
    System.out.print(selectedAlgorithm);
  }

}
