package ui.data;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Literal literal = (Literal) o;
    return negated == literal.negated && atom.equals(literal.atom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(negated, atom);
  }
}
