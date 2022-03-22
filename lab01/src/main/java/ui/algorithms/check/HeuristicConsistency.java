package ui.algorithms.check;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import ui.algorithms.HeuristicAlgorithm;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class HeuristicConsistency extends HeuristicAlgorithm {

  private final List<String> logs = new LinkedList<>();
  private boolean isConsistent = true;

  public HeuristicConsistency(String stateSpacePath, String heuristicDescriptorPath) {
    super(stateSpacePath, heuristicDescriptorPath);

    System.out.printf("%s %s %s\n", Utility.COMMENT, Utility.LOG_HEURISTIC_CONSISTENT, heuristicDescriptorPath);
  }

  @Override
  public void run() {
    for (Entry<String, Double> parent : heuristicValues.entrySet()) {
      String parentState = parent.getKey();
      Double parentHeuristicValue = parent.getValue();

      if (!getTransitions().containsKey(parentState)) {
        continue;
      }

      for (Entry<String, Double> child : getTransitions().get(parentState).entrySet()) {
        String childState = child.getKey();
        Double childHeuristicValue = heuristicValues.get(child.getKey());
        Double childTransitionCost = child.getValue();

        boolean isCurrentConsistent = parentHeuristicValue <= childHeuristicValue + childTransitionCost;

        isConsistent = isConsistent && isCurrentConsistent;

        logs.add(
            String.format(Locale.ROOT, Utility.LOG_CONSISTENCY,
                Utility.LOG_CONDITION,
                isCurrentConsistent ? Utility.LOG_OK : Utility.LOG_ERR,
                parentState,
                childState,
                parentHeuristicValue,
                childHeuristicValue,
                childTransitionCost
            )
        );
      }
    }

    logs.add(String.format("%s: %s",
        Utility.LOG_CONCLUSION,
        isConsistent ? Utility.LOG_HEURISTIC_IS_CONSISTENT : Utility.LOG_HEURISTIC_IS_NOT_CONSISTENT
    ));
  }

  @Override
  public String toString() {
    return String.join("\n", logs);
  }
}
