package ui.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public abstract class Algorithm {

  private final HashMap<String, HashMap<String, Double>> transitions = new HashMap<>();
  private String beginState;
  private List<String> goalStates;

  // We will be updating these values so we shall leave them open to the algorithms
  protected boolean foundSolution = false;
  protected int statesVisited = 0;
  protected double totalCost = 0;
  protected int pathLength = 0;

  public Algorithm(String stateSpacePath) {
    List<String> lines = Utility.readLines(stateSpacePath);

    int nonCommentLine = 0;

    for (String line : lines) {
      if (line.startsWith(Utility.COMMENT)) {
        continue;
      }
      nonCommentLine++;

      if (nonCommentLine == 1) {
        // First line defines the beginning state
        beginState = line;
      } else if (nonCommentLine == 2) {
        // Second line defined goal states
        // Goal states are separated by a single whitespace
        goalStates = Arrays.asList(line.split("\\s"));
      } else {
        // Line three and after define transitions in the following format:
        // stateA: next_state_1,cost next_state_2,cost
        String[] tempTransitionDefinition = line.split(":\\s");

        String currentState = tempTransitionDefinition[0];
        String[] tempTransitions = tempTransitionDefinition[1].split("\\s");

        HashMap<String, Double> transitionsForCurrentState = new LinkedHashMap<>();

        for (String tempTransition : tempTransitions) {
          // For each next_state_i,cost pair we parse them and store in the inner HashMap
          String[] tempTransitionDetails = tempTransition.split(",");

          transitionsForCurrentState.put(tempTransitionDetails[0], Double.parseDouble(tempTransitionDetails[1]));
        }

        transitions.put(currentState, transitionsForCurrentState);
      }
    }
  }

  public abstract void run();

  public HashMap<String, HashMap<String, Double>> getTransitions() {
    return transitions;
  }

  public String getBeginState() {
    return beginState;
  }

  public List<String> getGoalStates() {
    return goalStates;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(String.format("%s: %s", Utility.LOG_FOUND_SOLUTION, foundSolution ? "yes" : "no"));

    if (!foundSolution) {
      return result.toString();
    }

    result.append(String.format("%s: %d\n", Utility.LOG_STATES_VISITED, statesVisited));
    result.append(String.format("%s: %d\n", Utility.LOG_PATH_LENGTH, pathLength));
    result.append(String.format("%s: %f", Utility.LOG_TOTAL_COST, totalCost));

    // TODO: Implement output za path kad skuzim kako cu implementirat to
    // result.append(String.format("%s: %s", Utility.LOG_PATH, path));

    return result.toString();
  }
}
