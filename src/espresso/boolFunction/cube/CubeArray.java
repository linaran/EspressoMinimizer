package espresso.boolFunction.cube;

import espresso.boolFunction.Cover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CubeArray implements Iterable<Cube> {
  private ArrayList<Cube> list;

  private int inputLength = 0;
  private int outputLength = 0;

  /**
   * This reference is shared among all {@link Cube}s contained in this data structure.
   * This is because one cube can choose to change its values and the data structure needs
   * to be aware of these changes.
   */
  private int[][] bitCount;

  /**
   * Initialize an empty cube array that accepts any
   * {@link Cube}s.
   */
  public CubeArray() {
    list = new ArrayList<>();
  }

  /**
   * Initialize an empty cube array that accepts only
   * {@link Cube}s with given input count and output count.
   *
   * @param inputCount  int
   * @param outputCount int
   */
  public CubeArray(int inputCount, int outputCount) {
    list = new ArrayList<>();

    inputLength = inputCount;
    outputLength = outputCount;
    bitCount = new int[2][inputLength];
  }

  public CubeArray(Collection<? extends Cube> c) {
    list = new ArrayList<>(c);
  }

  /**
   * Copy constructor.
   *
   * @param cubeArray {@link CubeArray}
   */
  public CubeArray(CubeArray cubeArray) {
    list = new ArrayList<>();
    addAll(cubeArray);
  }

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

  public int size() {
    return list.size();
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

  public Cube get(int index) {
    return list.get(index);
  }

  /**
   * Add a given {@link Cube} to the array.
   * Note: Don't add add cubes directly from another {@link Cover}
   * or {@link CubeArray}.
   *
   * @param cube {@link Cube}
   */
  public void add(Cube cube) {
    if (cube == null) {
      return;
    }

    if (cube.isBitCountTaken()) {
      throw new IllegalArgumentException(
          "This cube already belongs to a CubeArray or Cover. Copy it."
      );
    }

    if (list.add(cube)) {
      addMaintenance(cube);
    }
  }

  /**
   * Adds copies of {@link Cube}s inside given collection.
   *
   * @param c {@link Collection}
   */
  public void addAll(Collection<? extends Cube> c) {
    for (Cube cube : c) {
      add(cube.copy());
    }
  }

  /**
   * Adds copies of {@link Cube}s from another {@link CubeArray}.
   *
   * @param cubes {@link CubeArray}
   */
  public void addAll(CubeArray cubes) {
    for (Cube cube : cubes) {
      add(cube.copy());
    }
  }

  public void add(int index, Cube cube) {
    if (cube.isBitCountTaken()) {
      throw new IllegalArgumentException(
          "This cube already belongs to a CubeArray or Cover. Copy it."
      );
    }

    addMaintenance(cube);
    list.add(index, cube);
  }

  public Cube remove(int index) {
    Cube cube = list.get(index);
    removeMaintenance(cube);
    return list.remove(index);
  }

  public void remove(Object o) {
    Cube cube = (Cube) o;
    if (list.remove(o)) {
      removeMaintenance(cube);
    }
  }

  @Override
  public Iterator<Cube> iterator() {
    return new CubeArrayIterator();
  }

  /**
   * A wrapper around default {@link ArrayList} iterator in order to properly maintain
   * {@link CubeArray#bitCount} values.
   */
  public class CubeArrayIterator implements Iterator<Cube> {
    private Iterator<Cube> iterator = CubeArray.this.list.iterator();
    private Cube currentCube;


    public boolean hasNext() {
      return iterator.hasNext();
    }


    public Cube next() {
      currentCube = iterator.next();
      return currentCube;
    }

    public void remove() {
      iterator.remove();
      CubeArray.this.removeMaintenance(currentCube);
    }
  }
}
