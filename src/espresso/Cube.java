package espresso;

import java.util.Arrays;

import static espresso.InputState.*;
import static espresso.OutputState.*;

/**
 * By definition cube consists of its input part and output part.<br/>
 * <br/>
 * Both parts are simple arrays. Input part consists of {@link InputState}s and
 * the output part consists of {@link OutputState}s.
 */
public class Cube {
  public InputState[] input;
  public OutputState[] output;

  /**
   * Reference copy constructor.
   *
   * @param input  array of {@link InputState}s.
   * @param output array of {@link OutputState}s.
   */
  public Cube(InputState[] input, OutputState[] output) {
    this.input = input;
    this.output = output;
  }

  @Override
  public String toString() {
    return Arrays.toString(input) + " " + Arrays.toString(output);
  }

//////////////////////////////////////////////////////////////////////////////
//  Cube operations
//////////////////////////////////////////////////////////////////////////////

  /**
   * Method returns a new cube that represents an intersection
   * between this cube and another cube (given parameter).<br/>
   * Note: This operation can produce an {@link InputState#EMPTY} cube.
   * In case an empty cube is noticed this method will return null.
   *
   * @param other {@link Cube}.
   * @return {@link Cube}.
   */
  public Cube and(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    InputState[] inputStates = new InputState[input.length];
    OutputState[] outputStates = new OutputState[output.length];

    for (int i = 0; i < inputStates.length; i++) {
      inputStates[i] = InputState.and(input[i], other.input[i]);
      if (inputStates[i] == EMPTY) return null;
    }

    boolean isEmpty = true;
    for (int i = 0; i < outputStates.length; i++) {
      outputStates[i] = OutputState.and(output[i], other.output[i]);
      if (outputStates[i] != NOT_OUTPUT && isEmpty) isEmpty = false;
    }

    if (isEmpty) return null;
    else return new Cube(inputStates, outputStates);
  }

  /**
   * Method returns the number of {@link InputState#EMPTY} in the
   * intersection of this cube and another cube (given parameter).
   *
   * @param other {@link Cube}.
   * @return primitive int.
   */
  public int inputDistance(Cube other) {
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
    int retValue = 0;

    for (int i = 0; i < output.length; i++)
      if (OutputState.and(output[i], other.output[i]) != OUTPUT)
        retValue++;

    return retValue;
  }

  /**
   * Method returns the number of {@link InputState#EMPTY} and {@link OutputState#NOT_OUTPUT}
   * in the intersection of this cube and another cube (given parameter).
   *
   * @param other {@link Cube}.
   * @return primitive int.
   */
  public int distance(Cube other) {
    if (input.length != other.input.length || output.length != other.output.length)
      throw new IllegalArgumentException("Cube lengths are not compatible.");

    return inputDistance(other) + outputDistance(other);
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

    Cube retValue = and(other);

    if (inputDistance == 1 && outputDistance == 0) {
      for (int i = 0; i < retValue.input.length; i++)
        if (retValue.input[i] == EMPTY)
          retValue.input[i] = DONTCARE;
      return retValue;
    }

    if (inputDistance == 0 && outputDistance == 1) {
      for (int i = 0; i < retValue.output.length; i++)
        if (output[i] == OUTPUT || other.output[i] == OUTPUT)
          retValue.output[i] = OUTPUT;
      return retValue;
    }

    throw new UnsupportedOperationException("Likely a bug in Cube#distance, inputDistance, outputDistance.");
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
