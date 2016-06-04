package espresso.lockEnviroment;

import espresso.InputState;

import java.util.Collection;
import java.util.HashSet;

/**
 * Class was created to enforce a condition that all {@link Cube}s in a {@link Cover}
 * must have the same number of inputs and outputs. Also the class keeps track of the
 * number of {@link InputState#ONE}s and {@link InputState#ZERO}s in each column of the
 * the whole set. That way there is no need to iterate over all of the cubes in order
 * to check certain properties of a cover.
 */
public class CubeSet extends HashSet<Cube> {
  private int inputLength = 0;
  private int outputLength = 0;

  public CubeSet() {
  }

  public CubeSet(Collection<? extends Cube> c) {
    super(c);
  }

  public CubeSet(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  public CubeSet(int initialCapacity) {
    super(initialCapacity);
  }

  public int getInputLength() {
    return inputLength;
  }

  public int getOutputLength() {
    return outputLength;
  }

  @Override
  public boolean add(Cube cube) {
    if (inputLength != 0 && outputLength != 0) {
      if (inputLength != cube.inputLength() || outputLength != cube.outputLength())
        throw new IllegalArgumentException(
            "Given cube must have the same number of inputs and outputs as the cubes in the set."
        );
    } else {
      inputLength = cube.inputLength();
      outputLength = cube.outputLength();
    }

    return super.add(cube);
  }
}
