package ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import ui.data.Clause;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class ClauseParser {

  private final List<Clause> clauses;
  private Clause goalClause;

  public ClauseParser(String filePath) throws IOException {
    this(filePath, false);
  }

  public ClauseParser(String filePath, boolean includeLastClause) throws IOException {
    Path path = Paths.get(filePath);

    List<String> clauseLines = Files.readAllLines(path);

    clauses = new LinkedList<>();

    for (String clauseLine : clauseLines) {
      // Comments are allowed; we simply don't parse them
      // We also do not wish to parse empty lines
      if (clauseLine.startsWith(Utility.COMMENT_SYMBOL) || clauseLine.isBlank()) {
        continue;
      }

      try {
        // We turn all read data into lowercase - because we can
        // We also do not add any clauses that are tautologies - they will throw an exception
        clauses.add(Clause.parseClause(clauseLine.toLowerCase(Locale.ROOT)));
      } catch (Exception ignored) {
      }
    }

    if (!includeLastClause) {
      // The last clause that we find will be the goal clause
      goalClause = clauses.remove(clauses.size() - 1);
    }
  }

  public List<Clause> getClauses() {
    return clauses;
  }

  public Clause getGoalClause() {
    return goalClause;
  }
}
