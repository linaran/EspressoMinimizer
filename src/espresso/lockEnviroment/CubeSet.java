package espresso.lockEnviroment;

import espresso.InputState;

import java.util.Collection;
import java.util.HashSet;

import static espresso.InputState.*;

/**
 * Class was created to enforce a condition that all {@link Cube}s in a {@link Cover}
 * must have the same number of inputs and outputs. Also the class keeps track of the
 * number of {@link InputState#ONE}s and {@link InputState#ZERO}s in each column of the
 * the whole set. That way there is no need to iterate over all of the cubes in order
 * to check certain properties of a cover.
 */
class CubeSet extends HashSet<Cube> {
  int[] oneColumnCount; // package-local is not a mistake
  int[] zeroColumnCount;

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

  public int getOneCount(int columnIndex) {
    return oneColumnCount[columnIndex];
  }

  public int getZeroCount(int columnIndex) {
    return zeroColumnCount[columnIndex];
  }

  public int columnLength() {
    if (oneColumnCount == null) return 0;
    else return oneColumnCount.length;
  }

  @Override
  public boolean add(Cube cube) {
    if (iterator().hasNext()) {
      Cube fromSet = iterator().next();
      if (fromSet.inputLength() != cube.inputLength() || fromSet.outputLength() != cube.outputLength())
        throw new IllegalArgumentException(
            "Given cube must have the same number of inputs and outputs as the cubes in the set."
        );
    } else {
      if (oneColumnCount == null) oneColumnCount = new int[cube.inputLength()];
      if (zeroColumnCount == null) zeroColumnCount = new int[cube.inputLength()];
    }

    for (int i = 0; i < cube.inputLength(); i++) {
      if (cube.input(i) == ONE) oneColumnCount[i]++;
      if (cube.input(i) == ZERO) zeroColumnCount[i]++;
    }

    cube.lockInputStates = true;
    return super.add(cube);
  }

  @Override
  public boolean remove(Object o) {
    if (!super.remove(o))
      return false;

    Cube cube = (Cube) o;
    for (int i = 0; i < cube.inputLength(); i++) {
      if (cube.input(i) == ONE) oneColumnCount[i]--;
      if (cube.input(i) == ZERO) zeroColumnCount[i]--;
    }

    if (!iterator().hasNext()) {
      oneColumnCount = null;
      zeroColumnCount = null;
    }

    cube.lockInputStates = false;
    return super.remove(o);
  }
}
