package ui.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public abstract class Algorithm {

  private final LinkedHashMap<String, LinkedHashMap<String, Double>> transitions = new LinkedHashMap<>();
  private String beginState;
  private List<String> goalStates;

  // We will be updating these values so we shall leave them open to the algorithms
  protected boolean foundSolution = false;
  protected int statesVisited = 0;
  protected double totalCost = 0;
  protected int pathLength = 0;
  protected String totalPath;

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

        LinkedHashMap<String, Double> transitionsForCurrentState = new LinkedHashMap<>();

        if (tempTransitionDefinition.length >= 2) {
          String[] tempTransitions = tempTransitionDefinition[1].split("\\s");

          // We sort the successors states (transitions) here so that we can access them in order later
          List<String> sortedTransitions = Arrays.asList(tempTransitions);
          Collections.sort(sortedTransitions);

          for (String tempTransition : sortedTransitions) {
            // For each next_state_i,cost pair we parse them and store in the inner HashMap
            String[] tempTransitionDetails = tempTransition.split(",");

            transitionsForCurrentState.put(tempTransitionDetails[0], Double.parseDouble(tempTransitionDetails[1]));
          }
        }

        transitions.put(currentState, transitionsForCurrentState);
      }
    }
  }

  public abstract void run();

  public LinkedHashMap<String, LinkedHashMap<String, Double>> getTransitions() {
    return transitions;
  }

  public String getBeginState() {
    return beginState;
  }

  public List<String> getGoalStates() {
    return goalStates;
  }

  public boolean isGoalState(String state) {
    return goalStates.contains(state);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(String.format("%s: %s\n", Utility.LOG_FOUND_SOLUTION, foundSolution ? "yes" : "no"));

    if (!foundSolution) {
      return result.toString();
    }

    result.append(String.format("%s: %d\n", Utility.LOG_STATES_VISITED, statesVisited));
    result.append(String.format("%s: %d\n", Utility.LOG_PATH_LENGTH, pathLength));
    result.append(String.format(Locale.ROOT, "%s: %.1f\n", Utility.LOG_TOTAL_COST, totalCost));
    result.append(String.format("%s: %s", Utility.LOG_PATH, totalPath));

    return result.toString();
  }
}
