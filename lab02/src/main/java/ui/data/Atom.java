package ui.data;

import java.util.Objects;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Atom {

  private final String name;

  public Atom(String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Atom atom = (Atom) o;
    return name.equals(atom.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
