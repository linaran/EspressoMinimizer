package espresso.boolFunction;

import espresso.boolFunction.cube.Cube;

import static espresso.boolFunction.Containment.*;

/**
 * Value representations for input entries in {@link Cube}.
 */
public enum InputState {
  ZERO(0),
  ONE(1),
  DONTCARE(2),
  EMPTY(5);

  private int numState;

  InputState(int numState) {
    this.numState = numState;
  }

  private static final InputState[][] andInputMatrix = new InputState[][]{
      {ZERO, EMPTY, ZERO},
      {EMPTY, ONE, ONE},
      {ZERO, ONE, DONTCARE}
  };

  private static final InputState[][] unionInputMatrix = new InputState[][]{
      {ZERO, DONTCARE, DONTCARE},
      {DONTCARE, ONE, DONTCARE},
      {DONTCARE, DONTCARE, DONTCARE}
  };

  public int valueOf() {
    return numState;
  }

  /**
   * Returns an InputState for first given int value.
   * Integer to InputState mappings are: ZERO(0),
   * ONE(1), DONTCARE(2), EMPTY(5).
   *
   * @param value int
   * @return {@link InputState}
   */
  public static InputState fromValue(int value) {
    switch (value) {
      case 0:
        return ZERO;
      case 1:
        return ONE;
      case 2:
        return DONTCARE;
      case 5:
        return EMPTY;
      default:
        throw new IllegalArgumentException(
            "Accepted values are 0, 1, 2, 5."
        );
    }
  }

  public static InputState and(InputState o1, InputState o2) {
    if (o1 == EMPTY || o2 == EMPTY)
      throw new IllegalArgumentException("One of the input states are empty.");

    return andInputMatrix[o1.valueOf()][o2.valueOf()];
  }

  public static InputState union(InputState o1, InputState o2) {
    if (o1 == EMPTY || o2 == EMPTY) {
      throw new IllegalArgumentException("One of the input states are empty.");
    }

    return unionInputMatrix[o1.valueOf()][o2.valueOf()];
  }

  /**
   * Complement of {@link InputState#DONTCARE} is {@link InputState#EMPTY}.
   * Complement of {@link InputState#ONE} is {@link InputState#ZERO}.
   *
   * @return {@link InputState}.
   */
  public InputState complement() {
    if (this == EMPTY)
      throw new UnsupportedOperationException("Complement of an empty cube doesn't exist.");

    if (this == DONTCARE) return DONTCARE;

    return this == ONE ? ZERO : ONE;
  }

  /**
   * Method tells whether this {@link InputState} contains the other
   * {@link InputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * strictly contains or doesn't contain the given parameter.<br/>
   * For general containment refer to {@link InputState#generalContains(InputState)}.
   *
   * @param other {@link InputState}.
   * @return true if this object contains the given parameter.
   * @see Containment
   */
  public boolean contains(InputState other) {
    return inputContainment[numState][other.valueOf()] == CONTAIN;
  }

  /**
   * Method tells whether this {@link InputState} strictly contains the other
   * {@link InputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * contains or doesn't contain the given parameter.<br/>
   * For general containment refer to {@link InputState#generalContains(InputState)}.
   *
   * @param other {@link InputState}.
   * @return true if this object strictly contains the given parameter.
   * @see Containment
   */
  public boolean strictContains(InputState other) {
    return inputContainment[numState][other.valueOf()] == STRICT_CONTAIN;
  }

  /**
   * Method tells whether this {@link InputState} doesn't contain the other
   * {@link InputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object either
   * contains or strictly contains the given parameter.<br/>
   * For general containment refer to {@link InputState#generalContains(InputState)}.
   *
   * @param other {@link InputState}.
   * @return true if this object doesn't contain the given parameter.
   * @see Containment
   */
  public boolean notContains(InputState other) {
    return inputContainment[numState][other.valueOf()] == NOT_CONTAIN;
  }

  /**
   * Method tells whether this {@link InputState} contains OR strictly contains
   * the other {@link InputState} (given parameter).<br/>
   * <br/>
   * Note: If the method returns false then that means this object does not
   * contain the given parameter at all. Among all containment methods this
   * is the intuitive one.
   *
   * @param other {@link InputState}.
   * @return {@link InputState}.
   * @see Containment
   */
  public boolean generalContains(InputState other) {
    Containment containment = inputContainment[numState][other.valueOf()];
    return containment == CONTAIN || containment == STRICT_CONTAIN;
  }


  @Override
  public String toString() {
    return String.valueOf(numState);
  }
}
