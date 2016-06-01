package espresso;

import static espresso.Containment.*;

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

  public int valueOf() {
    return numState;
  }

  public static OutputState and(OutputState o1, OutputState o2) {
//    TODO: Refactor to work as OUTPUT.and(NOT_OUTPUT)
    return andOutputMatrix[o1.valueOf() - 3][o2.valueOf() - 3];
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
}
