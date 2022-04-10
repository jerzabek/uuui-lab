package ui.data;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class CookingAssistantInstruction {

  private final Clause clause;
  private final CookingAssistantInstructionType type;

  public CookingAssistantInstruction(Clause clause, CookingAssistantInstructionType type) {
    this.clause = clause;
    this.type = type;
  }

  public Clause getClause() {
    return clause;
  }

  public CookingAssistantInstructionType getType() {
    return type;
  }

  public enum CookingAssistantInstructionType {
    TEST_CLAUSE, ADD_CLAUSE, REMOVE_CLAUSE
  }
}
