package ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

  public RefutationResolution(List<Clause> clauses, Clause goalClause) {
    this.premises = clauses;

    this.goalClause = new Clause(goalClause.getLiterals());

    this.negatedGoalClause = new LinkedList<>();
    this.negatedGoalClause.addAll(goalClause.negateClause());
  }

  public boolean refute() {
    // Set of support collection
    Set<Clause> sos = new LinkedHashSet<>(negatedGoalClause);

    Queue<Clause> queue = new ArrayDeque<>(sos);
    Set<Clause> newClauses = new LinkedHashSet<>();

    while (!queue.isEmpty()) {
      Clause sosClause = queue.poll();
//      System.out.println("Trenutno: " + sosClause + " sos size: " + sos.size() + " queue size: " + queue.size());
      newClauses = new LinkedHashSet<>();


      for (Clause otherClause : premises) {
        Optional<Clause> resolvent = resolve(sosClause, otherClause);

        if (resolvent.isEmpty()) {
          continue;
        }

//        System.out.println("1: " + sosClause + " -- " + otherClause);
//        System.out.println("Result: " + resolvent.get());

        // NIL
        if (resolvent.get().getLiterals().isEmpty()) {
//          System.out.println("1: nil");
          return true;
        }

        if (!sos.contains(resolvent.get())) {
          newClauses.add(resolvent.get());
        }
      }

      for (Clause otherSosClause : sos) {
        if (otherSosClause.equals(sosClause)) {
          continue;
        }

        Optional<Clause> resolvent = resolve(sosClause, otherSosClause);

        if (resolvent.isEmpty()) {
          continue;
        }

//        System.out.println("2: " + sosClause + " -- " + otherSosClause);
//        System.out.println("Result: " + resolvent.get());

        // NIL
        if (resolvent.get().getLiterals().isEmpty()) {
//          System.out.println("2: nil");
          return true;
        }

        if (!sos.contains(resolvent.get())) {
          newClauses.add(resolvent.get());
        }
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
}
