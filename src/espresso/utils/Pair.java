package espresso.utils;

/**
 * I don't really recommend using this other than to maybe return two objects from methods.
 * Even then see if it can be avoided.
 */
public class Pair<A, B> {
  public final A first;
  public final B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

}
