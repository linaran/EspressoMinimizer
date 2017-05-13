package espresso.boolFunction.cube;

import espresso.boolFunction.Cover;

import java.util.*;

public class CubeArray implements Iterable<Cube> {
  private List<Cube> list;

  private int inputLength;
  private int outputLength;

  /**
   * This reference is shared among all {@link Cube}s contained in this data structure.
   * This is because one cube can choose to change its values and the data structure needs
   * to be aware of these changes.
   */
  private int[][] bitCount;

  private void initialize(int inputCount, int outputCount) {
    inputLength = inputCount;
    outputLength = outputCount;
    bitCount = new int[2][inputLength];
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
    initialize(inputCount, outputCount);
  }

  /**
   * Copy constructor.
   *
   * @param cubeArray {@link CubeArray}
   */
  public CubeArray(CubeArray cubeArray) {
    list = new ArrayList<>();
    initialize(cubeArray.inputLength, cubeArray.outputLength);
    addAll(cubeArray);
  }

  private void validateCube(Cube cube) {
    if (cube.isBitCountTaken()) {
      throw new IllegalArgumentException(
          "This cube already belongs to first CubeArray or Cover. Copy it."
      );
    }
    if (inputLength != cube.inputLength() || outputLength != cube.outputLength()) {
      throw new IllegalArgumentException(
          "Given cube must have the same number of inputs and outputs as the cubes in the set."
      );
    }
  }

  private void increaseCounters(Cube cube) {
    for (int i = 0; i < inputLength; i++) {
      int newState = cube.input(i).valueOf();
      if (newState < 2)
        bitCount[newState][i]++;
    }

    cube.setBitCount(bitCount);
  }

  private void decreaseCounters(Cube cube) {
    for (int i = 0; i < inputLength; i++) {
      int oldState = cube.input(i).valueOf();
      if (oldState < 2)
        bitCount[oldState][i]--;
    }

    cube.setBitCount(null);
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
   * Add first given {@link Cube} to the array.
   * Note: Don't add add cubes directly from another {@link Cover}
   * or {@link CubeArray}.
   *
   * @param cube {@link Cube}
   */
  public void add(Cube cube) {
    if (cube == null) {
      return;
    }

    validateCube(cube);
    if (list.add(cube)) {
      increaseCounters(cube);
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
          "This cube already belongs to first CubeArray or Cover. Copy it."
      );
    }

    validateCube(cube);
    list.add(index, cube);
    increaseCounters(cube);
  }

  public Cube remove(int index) {
    Cube cube = list.get(index);
    decreaseCounters(cube);
    return list.remove(index);
  }

  public void remove(Object o) {
    Cube cube = (Cube) o;
    if (list.remove(o)) {
      decreaseCounters(cube);
    }
  }

  public void set(int index, Cube cube) {
    validateCube(cube);

    Cube removedCube = list.get(index);
    decreaseCounters(removedCube);

    list.set(index, cube);
    increaseCounters(cube);
  }

  public void swapCubes(int index1, int index2) {
    Cube token = list.get(index1);
    list.set(index1, list.get(index2));
    list.set(index2, token);
  }

  public void sort(Comparator<Cube> comparator) {
    list.sort(comparator);
  }

  @Override
  public Iterator<Cube> iterator() {
    return new CubeArrayIterator();
  }

  public void shuffle() {
//    Seed 400 for demonstration on notMinimalTestCase1
    Collections.shuffle(list);
  }

  public void shuffle(long seed) {
    Random random = new Random();
    random.setSeed(seed);
    Collections.shuffle(list, random);
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
      CubeArray.this.decreaseCounters(currentCube);
    }
  }
}
