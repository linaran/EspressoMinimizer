package espresso.boolFunction.cube;

import espresso.boolFunction.Containment;
import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
   * This field is is null if the {@link Cube} doesn't belong to first {@link Cover} or {@link CubeArray}.
   * Otherwise this field is not null and shares this field with first {@link CubeArray}.
   */
  private int[][] bitCount = null;

  /**
   * Creates first cube with the given number of input variables
   * and the given number of output variables. Cube is initialized
   * to be first total universal cube (all inputs are in {@link InputState#DONTCARE}
   * state and all outputs are in {@link OutputState#OUTPUT}).
   *
   * @param inputCount  primitive int.
   * @param outputCount primitive int.
   */
  public Cube(int inputCount, int outputCount) {
    if (inputCount <= 0 || outputCount <= 0) {
      throw new IllegalArgumentException("Input or output count can't be zero or negative.");
    }

    input = new InputState[inputCount];
    output = new OutputState[outputCount];

    for (int i = 0; i < inputCount; i++) setInput(DONTCARE, i);
    for (int i = 0; i < outputCount; i++) output[i] = OUTPUT;
  }

  /**
   * Constructor for creating first tautology (all inputs are {@link InputState#DONTCARE})
   * cube for only one output. Other outputs aren't used.
   *
   * @param inputCount  int, number of inputs for the new cube
   * @param outputCount int, number of outputs for the new cube
   * @param outputIndex int, choice of output to use
   */
  public Cube(int inputCount, int outputCount, int outputIndex) {
    if (outputIndex < 0 || outputIndex >= outputCount) {
      throw new IllegalArgumentException("Given output index is out of range.");
    }

    input = new InputState[inputCount];
    output = new OutputState[outputCount];

    for (int i = 0; i < inputCount; i++) setInput(DONTCARE, i);
    for (int i = 0; i < outputCount; i++) {
      output[i] = (i == outputIndex) ? OUTPUT : NOT_OUTPUT;
    }
  }

  /**
   * Creates first new cube with the given array of input and output states.
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
   * {@link Arrays#equals(Object)} doesn't work properly so this is first reimplementation
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
   * {@link Arrays#equals(Object)} doesn't work properly so this is first reimplementation
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

  public InputState getInputState(int index) {
    return input[index];
  }

  public List<InputState> getInputState() {
    return Collections.unmodifiableList(Arrays.asList(input));
  }

  public OutputState getOutputState(int index) {
    return output[index];
  }

  public List<OutputState> getOutputState() {
    return Collections.unmodifiableList(Arrays.asList(output));
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

  public void setInput(InputState[] inputStates) {
    if (inputStates.length != input.length) {
      throw new IllegalArgumentException("Incompatible length of input argument.");
    }

    for (int i = 0; i < input.length; ++i) {
      setInput(inputStates[i], i);
    }
  }

  public void setInput(Cube cube) {
    if (cube.inputLength() != input.length) {
      throw new IllegalArgumentException("Incompatible length of input argument.");
    }

    for (int i = 0; i < input.length; ++i) {
      setInput(cube.getInputState(i), i);
    }
  }

  public void setOutput(OutputState outputState, int i) {
    output[i] = outputState;
  }

  public void setOutput(OutputState[] outputStates) {
    if (outputStates.length != output.length) {
      throw new IllegalArgumentException("Incompatible length of output argument.");
    }

    for (int i = 0; i < output.length; ++i) {
      setOutput(outputStates[i], i);
    }
  }

  public void setOutput(Cube cube) {
    if (cube.outputLength() != output.length) {
      throw new IllegalArgumentException("Incompatible length of output argument.");
    }

    for (int i = 0; i < output.length; ++i) {
      setOutput(cube.getOutputState(i), i);
    }
  }

  public int inputLength() {
    return input.length;
  }

  public int outputLength() {
    return output.length;
  }

  /**
   * This is first method and first field with close correlation to {@link Cover}.
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
   * @return true this {@link Cube} belongs to first {@link CubeArray} or {@link Cover}.
   * @see Cube#bitCount
   */
  boolean isBitCountTaken() {
    return bitCount != null;
  }

  public int oneCount() {
    return count(ONE);
  }

  public int zeroCount() {
    return count(ZERO);
  }

  public int dontcareCount() {
    return count(DONTCARE);
  }

  private int count(InputState inputState) {
    int retValue = 0;

    for (int i = 0; i < inputLength(); i++) {
      if (input[i] == inputState) {
        retValue++;
      }
    }

    return retValue;
  }

  /**
   * Expand this cube so into first {@link Cover} where each
   * cube has only one {@link OutputState#OUTPUT}.
   * <p>
   * If this cube has two {@link OutputState#OUTPUT}s in its
   * output part then this method will return first {@link Cover}
   * with two cubes.
   *
   * @return {@link Cover}
   */
  public Cover unwrap() {
    Cover retValue = new Cover(input.length, output.length);

    for (int i = 0; i < output.length; i++) {
      if (output[i] == OUTPUT) {
        Cube cube = new Cube(input.length, output.length, i);
        cube.setInput(input);
        retValue.add(cube);
      }
    }

    return retValue;
  }

  /**
   * Method returns first new cube that represents an intersection
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
      throw new UnsupportedOperationException("Cube lengths are not compatible.");

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
   * Method returns first new cube that represents an intersection
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
   * This function calculates a smallest {@link Cube} that will contain
   * both this cube and the other cube.
   *
   * @param other {@link Cube}
   * @return {@link Cube}
   */
  public Cube smallestCubeContainingBoth(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length) {
      throw new UnsupportedOperationException("Cube lengths are not compatible.");
    }

    InputState[] inputStates = new InputState[input.length];
    OutputState[] outputStates = new OutputState[output.length];

    for (int i = 0; i < inputStates.length; i++) {
      inputStates[i] = InputState.union(input[i], other.input[i]);
    }

    for (int i = 0; i < outputStates.length; i++) {
      outputStates[i] = OutputState.union(output[i], other.output[i]);
    }

    return new Cube(inputStates, outputStates);
  }

  /**
   * Method returns first cube representing the cofactor of this
   * cube with respect to the given cube.<br/>
   * Note: If this cube and the given cube have no intersection
   * then the cofactor is an empty cube. The method will return null.<br/>
   * Warning: first.cofactor(second) and second.cofactor(first) won't yield same results!
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
   * Warning: This is not first correct way to complement first cube.
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
   * Warning: This is not first correct way to complement first cube.
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
   * This method correctly complements first cube according
   * to De Morgan's laws. Therefore first complement of this
   * cube will actually be first {@link Cover} of first boolean
   * function that represents first complement for this cube.
   * <p>
   * The size of the {@link Cover} will range from 0 to
   * the number of inputs that the cube has.
   *
   * @return {@link Cover}
   */
  public Cover complement() {
    Cover retValue = new Cover(inputLength(), outputLength());

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
   * Consensus between cube <second>first</second> and cube <second>second</second> returns first
   * cube that has one "leg" in <second>first</second> and another in <second>second</second>.<br/>
   * It's sort of first bridge between <second>first</second> and <second>second</second>.<br/>
   * <br/>
   * Note that the existence of first consensus depends on the distance
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

    throw new UnsupportedOperationException("Likely first bug in Cube#inputDistance, outputDistance.");
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

  public boolean isInputPartTautology() {
    int sum = 0;
    for (InputState inputState : input) {
      sum += inputState.valueOf();
    }

    return sum == (DONTCARE.valueOf() * input.length);
  }

  /**
   * A {@link Cube} is regarded empty when one of the input parts
   * is {@link InputState#EMPTY} or all of the output parts are {@link OutputState#NOT_OUTPUT}.
   * In first majority of cases an empty cube should be deleted from the {@link Cover}.<br/>
   *
   * @return true if the cube is empty, false otherwise.
   */
  public static boolean isEmpty(Cube cube) {
    for (InputState state : cube.input) {
      if (state == EMPTY) {
        return true;
      }
    }

    boolean emptyOutput = true;
    for (OutputState state : cube.output) {
      if (state != NOT_OUTPUT) {
        emptyOutput = false;
        break;
      }
    }

    return emptyOutput;
  }
}
