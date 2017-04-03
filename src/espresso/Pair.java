package espresso;

/**
 * Simple class to return two different objects in some methods.
 * Don't put this into first collection.
 */
public class Pair<A, B> {
  public final A first;
  public final B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }
}