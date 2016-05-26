package espresso;

/**
 * Value representations for input entries in {@link Cube}.
 */
public enum InputState {
  ZERO(0),
  ONE(1),
  DONTCARE(2),
  EMPTY(5);

  private int numState;

  private static final InputState[][] andInputMatrix = new InputState[][]{
      {ZERO, EMPTY, ZERO},
      {EMPTY, ONE, ONE},
      {ZERO, ONE, DONTCARE}
  };

  InputState(int numState) {
    this.numState = numState;
  }

  public int valueOf() {
    return numState;
  }

  public static InputState and(InputState o1, InputState o2) {
    if (o1 == EMPTY || o2 == EMPTY)
      throw new IllegalArgumentException("One of the input states are empty");

    return andInputMatrix[o1.valueOf()][o2.valueOf()];
  }
}
