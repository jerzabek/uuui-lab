package ui.algorithms.check;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import ui.algorithms.Algorithm;
import ui.algorithms.HeuristicAlgorithm;
import ui.algorithms.search.UCS;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class HeuristicOptimism extends HeuristicAlgorithm {

  private final List<String> logs = new LinkedList<>();
  private final Algorithm searchAlgorithm;
  private boolean isOptimistic = true;

  public HeuristicOptimism(String stateSpacePath, String heuristicDescriptorPath) {
    super(stateSpacePath, heuristicDescriptorPath);

    searchAlgorithm = new UCS(stateSpacePath);
    System.out.printf("%s %s %s\n", Utility.COMMENT, Utility.LOG_HEURISTIC_OPTIMISTIC, heuristicDescriptorPath);
  }

  @Override
  public void run() {
    for (Entry<String, Double> child : heuristicValues.entrySet()) {
      String state = child.getKey();
      Double heuristicValue = child.getValue();

      searchAlgorithm.initialize(state);
      searchAlgorithm.run();

      double actualCost = searchAlgorithm.getTotalCost();

      isOptimistic = heuristicValue <= actualCost && isOptimistic;

      logs.add(
          String.format(Locale.ROOT, Utility.LOG_OPTIMISM,
              Utility.LOG_CONDITION,
              heuristicValue <= actualCost ? Utility.LOG_OK : Utility.LOG_ERR,
              state,
              heuristicValue,
              actualCost
          )
      );
    }

    logs.add(String.format("%s: %s",
        Utility.LOG_CONCLUSION,
        isOptimistic ? Utility.LOG_HEURISTIC_IS_OPTIMISTIC : Utility.LOG_HEURISTIC_IS_NOT_OPTIMISTIC
    ));
  }

  @Override
  public String toString() {
    return String.join("\n", logs);
  }
}
