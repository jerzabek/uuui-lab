package ui;

import java.util.List;
import ui.data.Clause;
import ui.data.CookingAssistantInstruction;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class CookingAssistant {

  private final List<Clause> premises;
  private final List<CookingAssistantInstruction> instructions;

  public CookingAssistant(List<Clause> clauses, List<CookingAssistantInstruction> instructions) {
    this.premises = clauses;
    this.instructions = instructions;
  }

  public void cookPerInstructions() {
    for (CookingAssistantInstruction instruction : instructions) {
      switch (instruction.getType()) {
        case TEST_CLAUSE -> {
          RefutationResolution resolution = new RefutationResolution(premises, instruction.getClause());

          boolean result = resolution.refute();

          System.out.printf("[CONCLUSION]: %s is %s\n", resolution.getGoalClause(), result ? "true" : "unknown");
        }
        case ADD_CLAUSE -> {
          premises.add(instruction.getClause());
          System.out.println("Added clause " + instruction.getClause());
        }
        case REMOVE_CLAUSE -> {
          premises.remove(instruction.getClause());
          System.out.println("Removed clause " + instruction.getClause());
        }
      }
    }
  }
}
