package espresso;

/**
 * Value representations for output entries in {@link Cube}.
 */
public enum OutputState {
  NOT_OUTPUT(3),
  OUTPUT(4);

  private int numState;

  private static final OutputState[][] andOutputMatrix = new OutputState[][]{
      {NOT_OUTPUT, NOT_OUTPUT},
      {NOT_OUTPUT, OUTPUT}
  };

  OutputState(int numState) {
    this.numState = numState;
  }

  public int valueOf() {
    return numState;
  }

  public static OutputState and(OutputState o1, OutputState o2) {
    return andOutputMatrix[o1.valueOf() - 3][o2.valueOf() - 3];
  }
}
