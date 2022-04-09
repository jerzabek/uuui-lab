package ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ui.data.Atom;
import ui.data.Clause;
import ui.data.Literal;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class RefutationResolution {

  private final List<Clause> premises;
  private final Clause goalClause;

  public RefutationResolution(List<Clause> clauses, Clause goalClause) {
    this.premises = clauses;

    this.goalClause = new Clause(goalClause.getLiterals());
    this.goalClause.negateClause(); // In this resolution strategy we only need the negated goal clause
  }

  public boolean refute() {
    // Set of support collection
    List<Clause> sos = new LinkedList<>();

    sos.add(goalClause);

    root:
    while (true) {
      for (Clause sosClause : sos) {
        for (Clause otherClause : premises) {
          if (eligibleForResolvement(sosClause, otherClause)) {
            // TODO: Implement
            break root;
          }
        }
      }
    }

    return false;
  }

  /**
   * We check if the two clauses share opposing literals (one being negated and the other one not).
   *
   * @param a First clause
   * @param b Second clause
   * @return Whether the clauses contain a matching atom whose literals are opposite
   */
  private boolean eligibleForResolvement(Clause a, Clause b) {
    Map<Atom, Literal> index = new HashMap<>();

    for (Literal literal : a.getLiterals()) {
      index.put(literal.getAtom(), literal);
    }

    for (Literal literal : b.getLiterals()) {
      if (!index.containsKey(literal.getAtom())) {
        continue;
      }

      // We compare two literals that have the same atom
      // We compare if they are opposing negations (~a & a or a & ~a)
      if (index.get(literal.getAtom()).isNegated() ^ literal.isNegated()) {
        return true;
      }
    }

    return false;
  }

  public List<Clause> getPremises() {
    return premises;
  }

  public Clause getGoalClause() {
    return goalClause;
  }
}
