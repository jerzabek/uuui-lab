package ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import ui.data.Atom;
import ui.data.Clause;
import ui.data.Literal;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class RefutationResolution {

  private final List<Clause> premises;
  private final Clause goalClause;
  private final List<Clause> negatedGoalClause;
  private final Map<Clause, Clause[]> parents;
  private List<Clause> clauseTree;

  public RefutationResolution(List<Clause> clauses, Clause goalClause) {
    this.premises = clauses;

    this.goalClause = new Clause(goalClause.getLiterals());
    this.clauseTree = new ArrayList<>();
    this.parents = new HashMap<>();

    this.negatedGoalClause = new LinkedList<>();
    this.negatedGoalClause.addAll(goalClause.negateClause());
  }

  public boolean refute() {
    // Set of support collection
    Set<Clause> sos = new LinkedHashSet<>(negatedGoalClause);

    Queue<Clause> queue = new ArrayDeque<>(sos);
    Set<Clause> newClauses;

    while (!queue.isEmpty()) {
      Clause sosClause = queue.poll();
      newClauses = new LinkedHashSet<>();

      // Firstly we iterate over premises
      if (attemptResolvement(sos, newClauses, premises, sosClause)) {
        return true;
      }

      // Then we iterate over all established set-of-support clauses (this doesn't include newly generated clauses)
      if (attemptResolvement(sos, newClauses, sos, sosClause)) {
        return true;
      }

      for (Clause c : newClauses) {
        if (!queue.contains(c)) {
          queue.add(c);
        }
      }
//      queue.addAll(newClauses);
      sos.addAll(newClauses);
    }

    return false;
  }

  private boolean attemptResolvement(Set<Clause> sos, Set<Clause> newClauses, Collection<Clause> clauses, Clause clause) {
    for (Clause otherClause : clauses) {
      Optional<Clause> resolvent = resolve(clause, otherClause);

      if (resolvent.isEmpty()) {
        continue;
      }

      // NIL
      if (resolvent.get().getLiterals().isEmpty()) {
        resolveClauseTree(clause, otherClause);
        return true;
      }

      if (!sos.contains(resolvent.get())) {
        parents.put(resolvent.get(), new Clause[]{clause, otherClause});
        newClauses.add(resolvent.get());
      }
    }
    return false;
  }

  private void resolveClauseTree(Clause a, Clause b) {
    List<Clause> leftParent = resolveClauseParents(a, 0);
    List<Clause> rightParent = resolveClauseParents(b, 0);

    Set<Clause> parentalClauses = new HashSet<>(leftParent);
    parentalClauses.addAll(rightParent);

    clauseTree = new ArrayList<>(parentalClauses);
  }

  private List<Clause> resolveClauseParents(Clause clause, int depth) {
    if (!parents.containsKey(clause) || depth > 10) {
      return List.of(clause);
    }

    Clause[] parentClauses = parents.get(clause);

    List<Clause> leftParent = resolveClauseParents(parentClauses[0], depth + 1);
    List<Clause> rightParent = resolveClauseParents(parentClauses[1], depth + 1);

    Set<Clause> parentalClauses = new HashSet<>(leftParent);
    parentalClauses.addAll(rightParent);
    parentalClauses.add(clause);

    return new ArrayList<>(parentalClauses);
  }

  private Optional<Clause> resolve(Clause a, Clause b) {
    Map<Atom, Literal> index = new HashMap<>();

    for (Literal literal : a.getLiterals()) {
      index.put(literal.getAtom(), literal);
    }

    Literal literalToResolve = null;

    for (Literal literal : b.getLiterals()) {
      if (!index.containsKey(literal.getAtom())) {
        continue;
      }

      // We compare two literals that have the same atom
      // We compare if they are opposing negations using XOR operator (~a & a or a & ~a)
      if (index.get(literal.getAtom()).isNegated() ^ literal.isNegated()) {
        literalToResolve = index.remove(literal.getAtom());
        break;
      }
    }

    if (literalToResolve == null) {
      // We did not find any matching literals and can not resolve these two clauses
      return Optional.empty();
    }

    Set<Literal> newLiterals = new HashSet<>();

    for (Literal litA : index.values()) {
      for (Literal litB : b.getLiterals()) {
        if (litA.getAtom().equals(litB.getAtom()) && litA.isNegated() ^ litB.isNegated()) {
          // If XOR of two atoms == true that means we have a useless clause, so we reject it
          return Optional.empty();
        }

        newLiterals.add(litA);
      }
    }
    newLiterals.addAll(b.getLiterals());

    Literal finalLiteralToResolve = literalToResolve;
    newLiterals.removeIf(literal -> literal.getAtom().equals(finalLiteralToResolve.getAtom()));

    LinkedList<Literal> sortedLiterals = new LinkedList<>(newLiterals);

    sortedLiterals.sort(Comparator.comparing(Literal::toString));

    return Optional.of(new Clause(new ArrayList<>(sortedLiterals)));
  }

  public List<Clause> getPremises() {
    return premises;
  }

  public Clause getGoalClause() {
    return goalClause;
  }

  public List<Clause> getClauseTree() {
    return clauseTree;
  }
}
