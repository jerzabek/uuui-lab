package ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import ui.data.Clause;
import ui.data.CookingAssistantInstruction;
import ui.data.CookingAssistantInstruction.CookingAssistantInstructionType;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class CookingAssistantInstructionParser {

  private final List<CookingAssistantInstruction> instructions;

  public CookingAssistantInstructionParser(String filePath) throws IOException {
    Path path = Paths.get(filePath);

    List<String> lines = Files.readAllLines(path);

    instructions = new LinkedList<>();

    for (String line : lines) {
      // Comments are allowed; we simply don't parse them
      // We also do not wish to parse empty lines
      if (line.startsWith(Utility.COMMENT_SYMBOL) || line.isBlank()) {
        continue;
      }

      CookingAssistantInstructionType type;
      Clause clause;

      if (line.endsWith(Utility.TEST_CLAUSE_SYMBOL)) {
        type = CookingAssistantInstructionType.TEST_CLAUSE;
        line = line.replace(Utility.TEST_CLAUSE_SYMBOL, "");

      } else if (line.endsWith(Utility.ADD_CLAUSE_SYMBOL)) {
        type = CookingAssistantInstructionType.ADD_CLAUSE;
        line = line.replace(Utility.ADD_CLAUSE_SYMBOL, "");

      } else if (line.endsWith(Utility.REMOVE_CLAUSE_SYMBOL)) {
        type = CookingAssistantInstructionType.REMOVE_CLAUSE;
        line = line.replace(Utility.REMOVE_CLAUSE_SYMBOL, "");

      } else {
        throw new IllegalStateException("Invalid user commands");
      }

      try {
        clause = Clause.parseClause(line.toLowerCase(Locale.ROOT));
      } catch (Exception ignored) {
        throw new IllegalStateException("Tautology clause detected");
      }

      instructions.add(new CookingAssistantInstruction(clause, type));
    }
  }

  public List<CookingAssistantInstruction> getInstructions() {
    return instructions;
  }
}
