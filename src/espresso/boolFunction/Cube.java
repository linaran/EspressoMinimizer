package espresso.boolFunction;

import java.util.Arrays;

import static espresso.boolFunction.InputState.*;
import static espresso.boolFunction.OutputState.*;

/**
 * By definition cube consists of its input part and output part.<br/>
 * <br/>
 * Both parts are simple arrays. Input part consists of {@link InputState}s and
 * the output part consists of {@link OutputState}s. Nulls represent empty cubes.
 * Nulls can happen in this class only to make sure the {@link Cover} class doesn't
 * contain empty ({@link Cube#isEmpty(Cube)}) cubes.
 */
public class Cube {
  private InputState[] input;
  private OutputState[] output;

  /**
   * This field is is null if the {@link Cube} doesn't belong to a {@link Cover} or {@link CubeArray}.
   * Otherwise this field is not null and shares this field with a {@link CubeArray}.
   */
  private int[][] bitCount = null;

  /**
   * See {@link #Cube(int, int)}.
   *
   * @param inputCount  primitive int.
   * @param outputCount primitive int.
   */
  private void totalUniverseInit(int inputCount, int outputCount) {
    input = new InputState[inputCount];
    output = new OutputState[outputCount];

    for (int i = 0; i < inputCount; i++) setInput(DONTCARE, i);
    for (int i = 0; i < outputCount; i++) output[i] = OUTPUT;
  }

  /**
   * Creates a cube with the given number of input variables
   * and the given number of output variables. Cube is initialized
   * to be a total universal cube (all inputs are in {@link InputState#DONTCARE}
   * state and all outputs are in {@link OutputState#OUTPUT}).
   *
   * @param inputCount  primitive int.
   * @param outputCount primitive int.
   */
  public Cube(int inputCount, int outputCount) {
    totalUniverseInit(inputCount, outputCount);
  }

  /**
   * Creates a new cube with the given array of input and output states.
   * Note: given parameters are value-copied so there are no implicit
   * reference chains. Also the new copied cube is free to be added to
   * any {@link CubeArray} or {@link Cover}.
   *
   * @param input  array of {@link InputState}s.
   * @param output array of {@link OutputState}s.
   */
  public Cube(InputState[] input, OutputState[] output) {
    this.input = new InputState[input.length];
    this.output = new OutputState[output.length];

    System.arraycopy(input, 0, this.input, 0, input.length);
    System.arraycopy(output, 0, this.output, 0, output.length);
  }

  /**
   * Copy constructor.
   *
   * @param cube {@link Cube}.
   */
  public Cube(Cube cube) {
    input = new InputState[cube.input.length];
    output = new OutputState[cube.output.length];
    System.arraycopy(cube.input, 0, input, 0, cube.input.length);
    System.arraycopy(cube.output, 0, output, 0, cube.output.length);
  }

  /**
   * Convenience copy method.
   *
   * @return copy of this object.
   */
  public Cube copy() {
    return new Cube(this);
  }

  @Override
  public String toString() {
    return Arrays.toString(input) + " " + Arrays.toString(output);
  }

  /**
   * {@link Arrays#equals(Object)} doesn't work properly so this is a reimplementation
   * of the method.
   *
   * @param o1 array of {@link InputState}s.
   * @param o2 array of {@link InputState}s.
   * @return true if arrays are equal.
   * @see Arrays#equals(Object)
   */
  private boolean inputStateArrayEquals(InputState[] o1, InputState[] o2) {
    if (o1 == o2)
      return true;
    if (o1 == null || o2 == null)
      return false;

    int length = o1.length;
    if (o2.length != length)
      return false;

    for (int i = 0; i < length; i++) {
      InputState state1 = o1[i];
      InputState state2 = o2[i];
      if (!(state1 == null ? state2 == null : state1 == state2))
        return false;
    }

    return true;
  }

  /**
   * {@link Arrays#equals(Object)} doesn't work properly so this is a reimplementation
   * of the method.
   *
   * @param o1 array of {@link OutputState}s.
   * @param o2 array of {@link OutputState}s.
   * @return true if arrays are equal.
   * @see Arrays#equals(Object)
   */
  private boolean outputStateArrayEquals(OutputState[] o1, OutputState[] o2) {
    if (o1 == o2)
      return true;
    if (o1 == null || o2 == null)
      return false;

    int length = o1.length;
    if (o2.length != length)
      return false;

    for (int i = 0; i < length; i++) {
      OutputState state1 = o1[i];
      OutputState state2 = o2[i];
      if (!(state1 == null ? state2 == null : state1 == state2))
        return false;
    }

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Cube cube = (Cube) o;

    return inputStateArrayEquals(input, cube.input) && outputStateArrayEquals(output, cube.output);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(input);
    result = 31 * result + Arrays.hashCode(output);
    return result;
  }

  public InputState input(int i) {
    return input[i];
  }

  public OutputState output(int i) {
    return output[i];
  }

  public void setInput(InputState inputState, int i) {
    if (bitCount != null) {
      int newState = inputState.valueOf();

      if (input[i] != null && input[i].valueOf() < 2)
        bitCount[input[i].valueOf()][i]--;
      if (newState < 2)
        bitCount[newState][i]++;
    }

    input[i] = inputState; // okay
  }

  public void setOutput(OutputState outputState, int i) {
    output[i] = outputState;
  }

  public int inputLength() {
    return input.length;
  }

  public int outputLength() {
    return output.length;
  }

  /**
   * This is a method and a field with close correlation to {@link Cover}.
   *
   * @param bitCount primitive int[][]
   * @see Cube#bitCount
   */
  void setBitCount(int[][] bitCount) {
    this.bitCount = bitCount;
  }

  /**
   * This method is closely related to {@link Cover}.
   *
   * @return true this {@link Cube} belongs to a {@link CubeArray} or {@link Cover}.
   * @see Cube#bitCount
   */
  boolean isBitCountTaken() {
    return bitCount != null;
  }
  //////////////////////////////////////////////////////////////////////////////
//  Cube operations
//////////////////////////////////////////////////////////////////////////////

  /**
   * Method returns a new cube that represents an intersection
   * between this cube and another cube (given parameter).<br/>
   * Note: This operation can produce an {@link InputState#EMPTY} cube.
   * In case an empty cube is noticed this method will return null if
   * parameter returnEmpty is false. Otherwise an empty Cube will be returned.
   *
   * @param other       {@link Cube}.
   * @param returnEmpty primitive boolean, if false empty cubes will be returned as null.
   * @return {@link Cube}.
   */
  public Cube and(Cube other, boolean returnEmpty) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    InputState[] inputStates = new InputState[input.length];
    OutputState[] outputStates = new OutputState[output.length];

    for (int i = 0; i < inputStates.length; i++) {
      inputStates[i] = InputState.and(input[i], other.input[i]);
      if (inputStates[i] == EMPTY && !returnEmpty) return null;
    }

    boolean isEmpty = true;
    for (int i = 0; i < outputStates.length; i++) {
      outputStates[i] = OutputState.and(output[i], other.output[i]);
      if (outputStates[i] != NOT_OUTPUT && isEmpty) isEmpty = false;
    }

    if (isEmpty && !returnEmpty) return null;
    else return new Cube(inputStates, outputStates);
  }

  /**
   * Method returns a new cube that represents an intersection
   * between this cube and another cube (given parameter).<br/>
   * Note: In case an empty cube is noticed this method will return null.
   *
   * @param other {@link Cube}.
   * @return {@link Cube}.
   */
  public Cube and(Cube other) {
    return and(other, false);
  }

  /**
   * Method returns a cube representing the cofactor of this
   * cube with respect to the given cube.<br/>
   * Note: If this cube and the given cube have no intersection
   * then the cofactor is an empty cube. The method will return null.<br/>
   * Warning: a.cofactor(b) and b.cofactor(a) won't yield same results!
   *
   * @param other {@link Cube}.
   * @return {@link Cube}.
   */
  public Cube cofactor(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    if (and(other) == null) return null;
    Cube retValue = new Cube(this);

    for (int i = 0; i < retValue.input.length; i++)
      if (other.input[i] == ZERO || other.input[i] == ONE)
        retValue.setInput(DONTCARE, i);

    for (int i = 0; i < retValue.output.length; i++)
      if (other.output[i] == NOT_OUTPUT)
        retValue.output[i] = OUTPUT;

    return retValue;
  }

  /**
   * This method runs over all outputs of the cube and
   * complements all states individually.
   * Warning: This is not a correct way to complement a cube.
   * For that see {@link Cube#complement()}.
   *
   * @return this object for convenience.
   * @see OutputState#complement()
   */
  public Cube outputComplement() {
    for (int i = 0; i < output.length; i++)
      output[i] = output[i].complement();

    return this;
  }

  /**
   * This method runs over all inputs of the cube and
   * complements all states individually.
   * <p>
   * Warning: This is not a correct way to complement a cube.
   * For that see {@link Cube#complement()}.
   *
   * @return this object, for convenience.
   * @see InputState#complement()
   * @see Cube#complement()
   */
  public Cube inputComplement() {
    for (int i = 0; i < input.length; i++)
      setInput(input[i].complement(), i);

    return this;
  }

  /**
   * This method correctly complements a cube according
   * to De Morgan's laws. Therefore a complement of this
   * cube will actually be a {@link Cover} of a boolean
   * function that represents a complement for this cube.
   * <p>
   * The size of the {@link Cover} will range from 0 to
   * the number of inputs that the cube has.
   *
   * @return {@link Cover}
   */
  public Cover complement() {
    Cover retValue = new Cover();

    for (int i = 0; i < input.length; ++i) {
      InputState literal = input[i];
      if (literal == ONE || literal == ZERO) {
        InputState[] newInputs = new InputState[input.length];
        Arrays.fill(newInputs, DONTCARE);
        newInputs[i] = literal.complement();

        OutputState[] outputCopy = new OutputState[output.length];
        System.arraycopy(output, 0, outputCopy, 0, output.length);

        retValue.add(new Cube(newInputs, outputCopy));
      }
    }

    return retValue;
  }

  /**
   * Method returns the number of {@link InputState#EMPTY} in the
   * intersection of this cube and another cube (given parameter).
   *
   * @param other {@link Cube}.
   * @return primitive int.
   */
  public int inputDistance(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    int retValue = 0;

    for (int i = 0; i < input.length; i++)
      if (InputState.and(input[i], other.input[i]) == EMPTY)
        retValue++;

    return retValue;
  }

  /**
   * Method returns the number of {@link OutputState#NOT_OUTPUT} in the
   * intersection of this cube and another cube (given parameter).
   *
   * @param other {@link Cube}.
   * @return primitive int.
   */
  public int outputDistance(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    int retValue = 0;

    for (int i = 0; i < output.length; i++)
      if (OutputState.and(output[i], other.output[i]) != OUTPUT)
        retValue++;

    return retValue;
  }

  /**
   * Consensus between cube <b>a</b> and cube <b>b</b> returns a
   * cube that has one "leg" in <b>a</b> and another in <b>b</b>.<br/>
   * It's sort of a bridge between <b>a</b> and <b>b</b>.<br/>
   * <br/>
   * Note that the existence of a consensus depends on the distance
   * between cubes. If the distance is greater than 2 then this method
   * will return null.
   *
   * @param other {@link Cube}.
   * @return {@link Cube}.
   */
  public Cube consensus(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    int inputDistance = inputDistance(other);
    int outputDistance = outputDistance(other);
    int distance = inputDistance + outputDistance;

    if (distance == 0)
      return and(other);
    else if (distance >= 2)
      return null;

    Cube retValue = and(other, true);

    if (inputDistance == 1 && outputDistance == 0) {
      for (int i = 0; i < retValue.input.length; i++)
        if (retValue.input[i] == EMPTY)
          retValue.setInput(DONTCARE, i);
      return retValue;
    }

    if (inputDistance == 0 && outputDistance == 1) {
      for (int i = 0; i < retValue.output.length; i++)
        if (output[i] == OUTPUT || other.output[i] == OUTPUT)
          retValue.output[i] = OUTPUT;
      return retValue;
    }

    throw new UnsupportedOperationException("Likely a bug in Cube#inputDistance, outputDistance.");
  }

//  TODO: contain, strictContain, notContain if needed.

  /**
   * Method tells whether this cube {@link Containment#CONTAIN} or {@link Containment#STRICT_CONTAIN}
   * the other cube (given parameter).
   *
   * @param other {@link Cube}.
   * @return true if this cube contains or strictly contains the other cube.
   */
  public boolean generalContain(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    for (int i = 0; i < input.length; i++)
      if (!input[i].generalContains(other.input[i]))
        return false;
    for (int i = 0; i < output.length; i++)
      if (!output[i].generalContains(other.output[i]))
        return false;

    return true;
  }

  public boolean strictContain(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    for (int i = 0; i < input.length; ++i) {
      if (!input[i].strictContains(other.input[i])) {
        return false;
      }
    }
    for (int i = 0; i < output.length; ++i) {
      if (!output[i].strictContains(other.output[i])) {
        return false;
      }
    }

    return true;
  }

  /**
   * A {@link Cube} is regarded empty when one of the input parts
   * is {@link InputState#EMPTY} or all of the output parts are {@link OutputState#NOT_OUTPUT}.
   * In a majority of cases an empty cube should be deleted from the {@link Cover}.<br/>
   * <b>Warning:</b> This method is not an efficient check for emptiness. The best way to do
   * this is to check for emptiness on entries that have been recently changed.
   *
   * @return true if the cube is empty, false otherwise.
   * @deprecated Used for debugging only.
   */
  public static boolean isEmpty(Cube cube) {
    boolean emptyInput = false;
    boolean emptyOutput = true;

    for (InputState state : cube.input)
      if (state == EMPTY) {
        emptyInput = true;
        break;
      }

    if (emptyInput) return true;

    for (OutputState state : cube.output)
      if (state != NOT_OUTPUT) {
        emptyOutput = false;
        break;
      }

    return emptyOutput;
  }
}
