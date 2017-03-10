package espresso.boolFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CubeArray extends ArrayList<Cube> {
  private int inputLength = 0;
  private int outputLength = 0;

  /**
   * This reference is shared among all {@link Cube}s contained in this data structure.
   * This is because one cube can choose to change its values and the data structure needs
   * to be aware of these changes.
   */
  private int[][] bitCount;

  private void validateCube(Cube cube) {
    if (inputLength != cube.inputLength() || outputLength != cube.outputLength()) {
      throw new IllegalArgumentException(
          "Given cube must have the same number of inputs and outputs as the cubes in the set."
      );
    }
  }

  private void initializeData(Cube cube) {
    inputLength = cube.inputLength();
    outputLength = cube.outputLength();
    bitCount = new int[2][inputLength];
  }

  private void removeMaintenance(Cube cube) {
    for (int i = 0; i < inputLength; i++) {
      int oldState = cube.input(i).valueOf();
      if (oldState < 2)
        bitCount[oldState][i]--;
    }

    cube.setBitCount(null);
  }

  private void addMaintenance(Cube cube) {
    if (inputLength != 0 && outputLength != 0) {
      validateCube(cube);
    } else {
      initializeData(cube);
    }

    for (int i = 0; i < inputLength; i++) {
      int newState = cube.input(i).valueOf();
      if (newState < 2)
        bitCount[newState][i]++;
    }

    cube.setBitCount(bitCount);
  }

  public CubeArray(int initialCapacity) {
    super(initialCapacity);
  }

  public CubeArray() {
  }

  public CubeArray(Collection<? extends Cube> c) {
    super(c);
  }

  public int getInputLength() {
    return inputLength;
  }

  public int getOutputLength() {
    return outputLength;
  }

  public int getOneColumnCount(int i) {
    return bitCount[1][i];
  }

  public int getZeroColumnCount(int i) {
    return bitCount[0][i];
  }

  @Override
  public boolean add(Cube cube) {
    addMaintenance(cube);
    return super.add(cube);
  }

  @Override
  public void add(int index, Cube cube) {
    addMaintenance(cube);
    super.add(index, cube);
  }

  @Override
  public Cube remove(int index) {
    Cube cube = get(index);
    removeMaintenance(cube);
    return super.remove(index);
  }

  @Override
  public boolean remove(Object o) {
    Cube cube = (Cube) o;
    removeMaintenance(cube);
    return super.remove(o);
  }

  /**
   * A wrapper around default {@link ArrayList} iterator in order to properly maintain
   * {@link CubeArray#bitCount} values.
   */
  public class CubeArrayIterator implements Iterator<Cube> {
    private Iterator<Cube> iterator = CubeArray.this.iterator();
    private Cube currentCube;

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Cube next() {
      currentCube = iterator.next();
      return currentCube;
    }

    @Override
    public void remove() {
//      CubeArray.this.removeMaintenance(currentCube);
      iterator.remove();
    }
  }
}
