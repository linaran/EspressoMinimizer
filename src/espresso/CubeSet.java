package espresso;

import java.util.Collection;
import java.util.HashSet;

import static espresso.InputState.*;

/**
 *
 */
public class CubeSet extends HashSet<Cube> {
  private int[] oneColumnCount;
  private int[] zeroColumnCount;

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
      if (fromSet.input.length != cube.input.length || fromSet.output.length != cube.output.length)
        throw new IllegalArgumentException(
            "Given cube must have the same number of inputs and outputs as the cubes in the set."
        );
    } else {
      if (oneColumnCount == null) oneColumnCount = new int[cube.input.length];
      if (zeroColumnCount == null ) zeroColumnCount = new int[cube.input.length];
    }

    for (int i = 0; i < cube.input.length; i++) {
      if (cube.input[i] == ONE) oneColumnCount[i]++;
      if (cube.input[i] == ZERO) zeroColumnCount[i]++;
    }

    return super.add(cube);
  }

  @Override
  public boolean remove(Object o) {
    if (!super.remove(o))
      return false;

    Cube cube = (Cube) o;
    for (int i = 0; i < cube.input.length; i++) {
      if (cube.input[i] == ONE) oneColumnCount[i]--;
      if (cube.input[i] == ZERO) zeroColumnCount[i]--;
    }

    return super.remove(o);
  }
}
