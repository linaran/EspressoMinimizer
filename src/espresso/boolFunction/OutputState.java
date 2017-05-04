package espresso.boolFunction;

import espresso.boolFunction.cube.Cube;

import static espresso.boolFunction.Containment.*;

/**
 * Value representations for output entries in {@link Cube}.
 */
public enum OutputState {
  NOT_OUTPUT(3),
  OUTPUT(4);

  private int numState;

  OutputState(int numState) {
    this.numState = numState;
  }

  private static final OutputState[][] andOutputMatrix = new OutputState[][]{
      {NOT_OUTPUT, NOT_OUTPUT},
      {NOT_OUTPUT, OUTPUT}
  };

  private static final OutputState[][] unionOutputMatrix = new OutputState[][]{
      {NOT_OUTPUT, OUTPUT},
      {OUTPUT, OUTPUT}
  };

  public int valueOf() {
    return numState;
  }

  public static OutputState fromValue(int value) {
    switch (value) {
      case 3:
        return NOT_OUTPUT;
      case 4:
        return OUTPUT;
      default:
        throw new IllegalArgumentException(
            "Accepted values are 3, 4."
        );
    }
  }

  public static OutputState and(OutputState o1, OutputState o2) {
    return andOutputMatrix[o1.valueOf() - 3][o2.valueOf() - 3];
  }

  public static OutputState union(OutputState o1, OutputState o2) {
    return unionOutputMatrix[o1.valueOf() - 3][o2.valueOf() - 3];
  }

  /**
   * Complement of {@link OutputState#NOT_OUTPUT} is {@link OutputState#OUTPUT}.
   *
   * @return {@link OutputState}.
   */
  public OutputState complement() {
    return this == NOT_OUTPUT ? OUTPUT : NOT_OUTPUT;
  }

  /**
   * Method tells whether this {@link OutputState} contains the other
   * {@link OutputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * strictly contains or doesn't contain the given parameter.<br/>
   * For general containment refer to {@link OutputState#generalContains(OutputState)}.
   *
   * @param other {@link OutputState}.
   * @return true if this object contains the given parameter.
   * @see Containment
   */
  public boolean contains(OutputState other) {
    return outputContainment[numState - 3][other.valueOf() - 3] == CONTAIN;
  }

  /**
   * Method tells whether this {@link OutputState} strictly contains the other
   * {@link OutputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * strictly contains or doesn't contain the given parameter.<br/>
   * For general containment refer to {@link OutputState#generalContains(OutputState)}.
   *
   * @param other {@link OutputState}.
   * @return true if this object contains the given parameter.
   * @see Containment
   */
  public boolean strictContains(OutputState other) {
    return outputContainment[numState - 3][other.valueOf() - 3] == STRICT_CONTAIN;
  }

  /**
   * Method tells whether this {@link OutputState} doesn't contain the other
   * {@link OutputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * strictly contains or doesn't contain the given parameter.<br/>
   * For general containment refer to {@link OutputState#generalContains(OutputState)}.
   *
   * @param other {@link OutputState}.
   * @return true if this object contains the given parameter.
   * @see Containment
   */
  public boolean notContains(OutputState other) {
    return outputContainment[numState - 3][other.valueOf() - 3] == NOT_CONTAIN;
  }

  /**
   * Method tells whether this {@link OutputState} contains OR strictly contains
   * the other {@link OutputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object does not
   * contain the given parameter at all. Among all containment methods this
   * is the intuitive one.
   *
   * @param other {@link OutputState}.
   * @return {@link OutputState}.
   * @see Containment
   */
  public boolean generalContains(OutputState other) {
    Containment containment = outputContainment[numState - 3][other.valueOf() - 3];
    return containment == CONTAIN || containment == STRICT_CONTAIN;
  }


  @Override
  public String toString() {
    return String.valueOf(numState);
  }
}
