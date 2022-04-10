package ui;

import ui.data.Clause;
import ui.util.ClauseParser;
import ui.util.CookingAssistantInstructionParser;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Solution {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Invalid number of arguments. Expected mode.");
      System.exit(1);
      return;
    }

    String mode = args[0]; // resolution || cooking

    try {
      switch (mode) {
        case Utility.RESOLUTION_MODE -> {
          if (args.length < 2) {
            System.out.println("Invalid number of arguments. Expected path to file containing clauses.");
            System.exit(1);
            return;
          }

          ClauseParser clauseParser = new ClauseParser(args[1]); // clause descriptor file path

          RefutationResolution resolution = new RefutationResolution(clauseParser.getClauses(), clauseParser.getGoalClause());

          refuteResolution(resolution);
        }
        case Utility.COOKING_MODE -> {
          if (args.length < 3) {
            System.out.println("Invalid number of arguments. Expected path to file containing clauses and path to file containing user commands.");
            System.exit(1);
            return;
          }

          ClauseParser clauseParser = new ClauseParser(args[1], true); // clause descriptor file path
          CookingAssistantInstructionParser instructionParser = new CookingAssistantInstructionParser(args[2]); // user commands file path

          CookingAssistant cookingAssistant = new CookingAssistant(clauseParser.getClauses(), instructionParser.getInstructions());

          cookingAssistant.cookPerInstructions();
        }
        default -> throw new IllegalArgumentException("Invalid mode " + mode);
      }
    } catch (Throwable t) {
      System.out.println("An error occurred: " + t.getMessage());
      t.printStackTrace();
    }
  }

  private static void refuteResolution(RefutationResolution resolution) {
    boolean result = resolution.refute();

    System.out.println("Parsed clauses:");
    for (Clause clause : resolution.getPremises()) {
      System.out.println(clause);
    }

    System.out.printf("[CONCLUSION]: %s is %s\n", resolution.getGoalClause(), result ? "true" : "unknown");
  }
}
