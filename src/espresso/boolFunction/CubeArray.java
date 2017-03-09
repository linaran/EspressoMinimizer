package espresso.boolFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("Duplicates")
public class CubeArray extends ArrayList<Cube> implements Iterable<Cube> {
  private int inputLength = 0;
  private int outputLength = 0;
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
  public Iterator<Cube> iterator() {
    return new CubeArrayIterator();
  }

  @Override
  public boolean add(Cube cube) {
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

    return super.add(cube);
  }

  @Override
  public void add(int index, Cube cube) {
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

    super.add(index, cube);
  }

  @Override
  public Cube remove(int index) {
    Cube cube = get(index);

    for (int i = 0; i < inputLength; i++) {
      int oldState = cube.input(i).valueOf();
      if (oldState < 2)
        bitCount[oldState][i]--;
    }

    cube.setBitCount(null);

    return super.remove(index);
  }

  @Override
  public boolean remove(Object o) {
    Cube cube = (Cube) o;

    for (int i = 0; i < inputLength; i++) {
      int oldState = cube.input(i).valueOf();
      if (oldState < 2)
        bitCount[oldState][i]--;
    }

    cube.setBitCount(null);
    return super.remove(o);
  }

  public class CubeArrayIterator implements Iterator<Cube> {
    private ArrayList<Cube> list = CubeArray.this;
    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < list.size();
    }

    @Override
    public Cube next() {
      return list.get(index++);
    }

    @Override
    public void remove() {
      list.remove(index);
    }
  }
}
