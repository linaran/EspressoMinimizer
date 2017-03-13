package espresso.boolFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;

/**
 * By definition cover is a set of cubes.
 * In a sense of boolean algebra, cover is a matrix representation of a boolean function.
 */
public class Cover implements Iterable<Cube> {
  private CubeArray cubes;

  public Cover() {
    cubes = new CubeArray();
  }

  public Cover(Cube... cubes) {
    if (cubes == null) throw new NullPointerException("Parameter can't be null.");

    this.cubes = new CubeArray();

    for (Cube cube : cubes) {
      this.cubes.add(cube);
    }
  }

  /**
   * Copy constructor.
   *
   * @param cover {@link Cover}.
   */
  public Cover(Cover cover) {
    cubes = new CubeArray(cover.cubes);
  }

  /**
   * Reads only single output functions for now.
   *
   * @param filepath  {@link String}
   * @param splitChar {@link String}
   * @throws IOException If file not found or some other IO error occurs.
   */
  public Cover(String filepath, String splitChar) throws IOException {
    cubes = new CubeArray();

    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        String[] split = line.split(splitChar);
        InputState[] inputStates = new InputState[split.length];

        for (int i = 0; i < split.length; ++i) {
          inputStates[i] = InputState.values()[Integer.valueOf(split[i])];
        }

        cubes.add(new Cube(inputStates, new OutputState[]{OutputState.OUTPUT}));
      }
    }
  }

  public static Cover of(Cube... cubes) {
    return new Cover(cubes);
  }

  @Override
  public Iterator<Cube> iterator() {
    return cubes.new CubeArrayIterator();
  }

  @Override
  public String toString() {
    String retValue = "";
    for (Cube cube : cubes) {
      retValue += cube.toString() + "\n";
    }
    if (retValue.equals("")) return "empty\n";
    return retValue;
  }

  public void add(Cube cube) {
    cubes.add(cube);
  }

  public void addAll(Cover cover) {
    cubes.addAll(cover.cubes);
  }

  public void remove(Cube cube) {
    cubes.remove(cube);
  }

  public Cube get(int index) {
    return cubes.get(index);
  }

  /**
   * Number of cubes in the cover.
   *
   * @return primitive int.
   */
  public int size() {
    return cubes.size();
  }

  /**
   * Number of inputs for the {@link Cover}.
   *
   * @return int
   */
  public int inputCount() {
    return cubes.getInputLength();
  }

  /**
   * Number of outputs for the {@link Cover}.
   *
   * @return int
   */
  public int outputCount() {
    return cubes.getOutputLength();
  }

  public int getOneColumnCount(int i) {
    return cubes.getOneColumnCount(i);
  }

  public int getZeroColumnCount(int i) {
    return cubes.getZeroColumnCount(i);
  }

//////////////////////////////////////////////////////////////////////////////
//  Espresso operations
//////////////////////////////////////////////////////////////////////////////

  /**
   * The method tells if this cover is unate.
   * A cover is unate if it contains a column that doesn't have
   * {@link InputState#ONE} and {@link InputState#ZERO} at the same time.
   * For example a column with only {@link InputState#ONE} and
   * {@link InputState#DONTCARE} indicates a unate cover.
   *
   * @return true if the cover is unate.
   */
  public boolean isUnate() {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cover is empty!");

    for (int i = 0; i < cubes.getInputLength(); i++)
      if (getZeroColumnCount(i) != 0 && getOneColumnCount(i) != 0)
        return false;

    return true;
  }

  /**
   * Method chooses the most binate input variable in this cover.
   * The chosen variable will be used for a Shannon expansion.
   * Method will return the index of the input variable. If the
   * returned index is -1 that means no variable was chosen for
   * splitting because the cover is unate.
   *
   * @return primitive int, index of the chosen variable in the cubes of the cover.
   */
  public int binateSelect() {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

    int variableCount = cubes.getInputLength();
    int maxSum = -1;
    int maxIndex = -2;

    if (isUnate())
      return -1;

    for (int i = 0; i < variableCount; i++) {
      int zeroColumnCount = getZeroColumnCount(i);
      int oneColumnCount = getOneColumnCount(i);

      if (zeroColumnCount == 0 || oneColumnCount == 0) {
        continue;
      }

      if (zeroColumnCount + oneColumnCount > maxSum) {
        maxIndex = i;
        maxSum = zeroColumnCount + oneColumnCount;
      }
    }

    return maxIndex;
  }

//////////////////////////////////////////////////////////////////////////////
//  Cover operations
//////////////////////////////////////////////////////////////////////////////

  /**
   * Method returns the Shannon expansion of this cover with
   * regard to the column which is denoted by the split index.
   * The Shannon expansion is returned
   * as an array of two covers. Negative part of the Shannon expansion
   * is at index 0 while the positive part of the expansion is at index 1.<br/>
   * Note: Returned array can contain covers that have no {@link Cube}s at all.
   *
   * @param splitIndex splitting index.
   * @return array of two {@link Cover}s.
   */
  public Cover[] shannonCofactors(int splitIndex) {
    if (splitIndex < 0 || splitIndex >= inputCount()) {
      throw new IllegalArgumentException("Split index is out range for input size of this cover.");
    }
    if (cubes.size() == 0) {
      throw new UnsupportedOperationException("Cube is empty!");
    }

    Cube splitCube = new Cube(inputCount(), outputCount());
    splitCube.setInput(ONE, splitIndex);
    Cube complement = new Cube(splitCube).inputComplement();

    Cover[] retValue = new Cover[2];
    retValue[0] = cofactor(complement);
    retValue[1] = cofactor(splitCube);

    return retValue;
  }

  /**
   * Method returns a new cover which is a cofactor of this cover
   * with respect to the given cover (given parameter).<br/>
   * Note: a.cofactor(b) and b.cofactor(a) won't yield same results.
   * Note: A cofactor between covers doesn't have to exists. In that case,
   * the method will return an empty cover ({@link Cover#cubes} size will
   * be zero).
   *
   * @param other {@link Cube}.
   * @return {@link Cover}.
   */
  public Cover cofactor(Cube other) {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

    Cover retValue = new Cover();

    for (Cube cube : cubes) {
      Cube cubeCofactor = cube.cofactor(other);
      if (cubeCofactor != null)
        retValue.cubes.add(cubeCofactor);
    }

    return retValue;
  }

  /**
   * Method tells whether the cover has a row full
   * of {@link InputState#DONTCARE} values.
   *
   * @return true if it has a row full of DONTCARE values.
   */
  public boolean hasDONTCARERow() {
    for (Cube cube : this) {
      for (int i = 0; i < cube.inputLength(); i++) {
        if (cube.input(i) != DONTCARE)
          break;
        if (i + 1 == cube.inputLength())
          return true;
      }
    }
    return false;
  }

  /**
   * Complements the cover. In place transformation.<br/>
   * Note: Cover inputComplement is just {@link Cube} output part inputComplement.
   *
   * @deprecated Although it works it is inefficient.
   */
  public void complement() {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

    cubes.forEach(Cube::outputComplement);
  }

  /**
   * Returns a new cover which is a complemented representation
   * of the given parameter.
   *
   * @param cover {@link Cover}.
   * @return {@link Cover}.
   * @deprecated Looks useless and wrong. At least time complexity is too big.
   */
  public Cover complement(Cover cover) {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

    Cover retValue = new Cover(cover);
    retValue.complement();
    return retValue;
  }

  /**
   * Method returns an intersect between this cover and another
   * cover (given parameter). Intersect of covers is a cover and
   * it may or may not exist.
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover intersect(Cover other) {
    Cover retValue = new Cover();

    for (Cube cube1 : cubes) {
      for (Cube cube2 : other.cubes) {
        Cube param = cube1.and(cube2);
        if (param == null) continue;
        retValue.cubes.add(param);
      }
    }

    return retValue;
  }

  /**
   * Method returns a new cover which is a simple union of both
   * covers.
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover union(Cover other) {
//    TODO: It is unclear how this method should be implemented. At least make a union of ON-SETS.
//    TODO: A more compact union implementation is possible.
    Cover retValue = new Cover();

    retValue.cubes.addAll(cubes);
    retValue.cubes.addAll(other.cubes);

    return retValue;
  }

  /**
   * Difference between cover <b>A</b> and cover <b>B</b> is a set of cubes (cover)
   * that covers<br/>
   * <br/>
   * "<b>A</b> INTERSECT COMPLEMENT(<b>B</b>)".<br/>
   * <br/>
   * Method returns the difference between this cover and
   * the other cover (given parameter).<br/>
   * Note: There is a big difference between a.difference(b) and b.difference(a)!
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   * @deprecated Using useless and wrong inputComplement function.
   */
  public Cover difference(Cover other) {
    return intersect(complement(other));
  }

  /**
   * Function to be used in {@link Cover#equals(Object)}.
   * Tells if this cover contains a cube that is LITERALLY
   * identical to the given {@link Cube}.
   *
   * @param cube {@link Cube}
   * @return true if the cube is found
   */
  private boolean containsCube(Cube cube) {
    for (Cube c : this) {
      if (c.equals(cube)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Use for debug purposes only and use it on small covers.
   *
   * @param obj {@link Object}
   * @return true if equal otherwise false.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cover)) {
      return false;
    }

    Cover other = (Cover) obj;

    if (size() != other.size()) {
      return false;
    }

    for (Cube cube : this) {
      if (!other.containsCube(cube)) {
        return false;
      }
    }

    return true;
  }
}

