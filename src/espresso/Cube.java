package espresso;

import java.util.Arrays;

/**
 * By definition cube consists of its input part and output part.<br/>
 * <br/>
 * Both parts are simple arrays. Input part consists of {@link InputState}s and
 * the output part consists of {@link OutputState}s.
 */
public class Cube {
  private InputState[] inputPart;
  private OutputState[] outputPart;

  public Cube(InputState[] input, OutputState[] output) {
    inputPart = input;
    outputPart = output;
  }

  public int getInputCount() {
    return inputPart.length;
  }

  public int getOutputCount() {
    return outputPart.length;
  }

  /**
   * Method returns the input part of the cube.
   * Freely use this object as a simple array.
   *
   * @return Array of {@link InputState}s.
   */
  public InputState[] input() {
    return inputPart;
  }

  /**
   * Method returns the output part of the cube.
   * Freely use this object as a simple array.
   *
   * @return Array of {@link OutputState}s.
   */
  public OutputState[] output() {
    return outputPart;
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

    for (InputState state : cube.inputPart)
      if (state == InputState.EMPTY) {
        emptyInput = true;
        break;
      }

    if (emptyInput) return true;

    for (OutputState state : cube.outputPart)
      if (state != OutputState.NOT_OUTPUT) {
        emptyOutput = false;
        break;
      }

    return emptyOutput;
  }

  @Override
  public String toString() {
    return Arrays.toString(inputPart) + " " + Arrays.toString(outputPart);
  }
}
