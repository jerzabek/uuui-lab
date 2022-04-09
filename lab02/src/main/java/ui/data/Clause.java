package ui.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Clause {

  private final List<Literal> literals;

  public Clause(List<Literal> literals) {
    this.literals = literals;
  }

  public void negateClause() {
    for(Literal literal : literals) {
      literal.negate();
    }
  }

  public static Clause parseClause(String rawClause) {
    if (rawClause.isBlank()) {
      throw new IllegalArgumentException("Clause can not be empty");
    }

    List<Literal> parsedLiterals = Arrays.stream(rawClause.split(Utility.DISJUNCTION_SYMBOL))
        .map(String::trim)
        .map(literalName -> {
          if (literalName.startsWith(Utility.NEGATION_SYMBOL)) {
            // If the literal begins with a negation symbol we make sure to pass that information to the appropriate constructor
            return new Literal(new Atom(literalName.substring(Utility.NEGATION_SYMBOL.length())), true);
          }

          return new Literal(new Atom(literalName));
        })
        .collect(Collectors.toList());

    return new Clause(parsedLiterals);
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  @Override
  public String toString() {
    return literals.stream()
        .map(Literal::toString)
        .collect(Collectors.joining(" " + Utility.DISJUNCTION_SYMBOL + " "));
  }
}
