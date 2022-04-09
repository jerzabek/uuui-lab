package ui.data;

import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Literal {

  private boolean negated = false;
  private final Atom atom;

  public Literal(Atom atom) {
    this.atom = atom;
  }

  public Literal(Atom atom, boolean negated) {
    this.negated = negated;
    this.atom = atom;
  }

  public void negate() {
    negated = !negated;
  }

  public boolean isNegated() {
    return negated;
  }

  public Atom getAtom() {
    return atom;
  }

  @Override
  public String toString() {
    if (negated) {
      return Utility.NEGATION_SYMBOL + atom;
    }

    return atom.toString();
  }
}
