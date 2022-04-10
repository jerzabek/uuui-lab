package ui.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Clause {

  private final List<Literal> literals;

  public Clause(List<Literal> literals) {
    Set<Atom> index = new HashSet<>();

    for(Literal literal : literals) {
      if(!index.add(literal.getAtom())) {
        // The set already contains this atom, if the two atoms are opposite we invalidate this clause
        throw new IllegalStateException("Tautology clause");
      }
    }

    this.literals = literals;
  }

  public List<Clause> negateClause() {
    List<Clause> conjunction = new LinkedList<>();

    for (Literal literal : literals) {
      conjunction.add(new Clause(List.of(new Literal(literal.getAtom(), !literal.isNegated()))));
    }

    return conjunction;
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
        .collect(Collectors.joining(Utility.DISJUNCTION_SYMBOL));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Clause clause = (Clause) o;
    return literals.equals(clause.literals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(literals);
  }
}
