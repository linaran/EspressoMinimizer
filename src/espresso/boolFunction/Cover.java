package espresso.boolFunction;

import espresso.boolFunction.cube.Cube;
import espresso.boolFunction.cube.CubeArray;
import espresso.urpAlgorithms.Complement;
import espresso.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.OutputState.NOT_OUTPUT;
import static espresso.boolFunction.OutputState.OUTPUT;

/**
 * By definition cover is first set of cubes.
 * In first sense of boolean algebra, cover is first matrix representation of first boolean function.
 */
public class Cover implements Iterable<Cube> {
  private CubeArray cubes;

  /**
   * Initialize first cover that accepts only {@link Cube}s
   * that with first certain number of inputs and outputs.
   * These numbers are defined in parameters.
   *
   * @param inputCount  int
   * @param outputCount int
   */
  public Cover(int inputCount, int outputCount) {
    cubes = new CubeArray(inputCount, outputCount);
  }

  public Cover(Cube... cubes) {
    if (cubes == null) throw new NullPointerException("Parameter can't be null.");
    if (cubes.length == 0) {
      throw new UnsupportedOperationException(
          "This constructor requires at least one cube. See Cover#Cover(int, int) as an alternative."
      );
    }

    this.cubes = new CubeArray(cubes[0].inputLength(), cubes[0].outputLength());

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
   * @param filepath {@link String}
   * @throws IOException If file not found or some other IO error occurs.
   */
  public Cover(String filepath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
      String firstLine = br.readLine();
      String[] inputOutputCounts = firstLine.split("\\s+");
      int inputCount = Integer.valueOf(inputOutputCounts[0]);
      int outputCount = Integer.valueOf(inputOutputCounts[1]);

      cubes = new CubeArray(inputCount, outputCount);

      for (String line = br.readLine(); line != null; line = br.readLine()) {
        String[] inputOutputStrings = line.split("\\s+");
        String inputString = inputOutputStrings[0];
        String outputString = inputOutputStrings[1];

        InputState[] inputStates = new InputState[inputString.length()];
        for (int i = 0; i < inputStates.length; ++i) {
          inputStates[i] = InputState.fromValue(Character.getNumericValue(inputString.charAt(i)));
        }

        OutputState[] outputStates = new OutputState[outputString.length()];
        for (int i = 0; i < outputStates.length; ++i) {
          outputStates[i] = OutputState.fromValue(Character.getNumericValue(outputString.charAt(i)));
        }

        cubes.add(new Cube(inputStates, outputStates));
      }
    }
  }

  public static Cover of(Cube... cubes) {
    if (cubes.length == 0) {
      throw new UnsupportedOperationException("At least one Cube must be given.");
    }
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

  private void checkCoverCompatibility(Cover other) {
    if (inputCount() != other.inputCount() || outputCount() != other.outputCount()) {
      throw new UnsupportedOperationException(
          "This operation can't be performed on these two covers. Their input or output counts are different."
      );
    }
  }

  /**
   * Method creates first variable {@link Cube} for this cover.
   * A variable {@link Cube} has all {@link InputState#DONTCARE}
   * input states except for one {@link InputState#ONE} state located
   * at the given index. All output states are {@link OutputState#OUTPUT}.
   *
   * @param inputIndex int
   * @return {@link Cube}
   * @see Cube
   */
  public Cube generateVariableCube(int inputIndex) {
    if (inputIndex < 0 || inputIndex >= inputCount()) {
      throw new IllegalArgumentException("Input index is out of range for this cover.");
    }

    Cube retValue = new Cube(inputCount(), outputCount());
    retValue.setInput(ONE, inputIndex);

    return retValue;
  }

  public Cube generateTautologyForOutput(int index) {
    if (index < 0 || index >= outputCount()) {
      throw new ArrayIndexOutOfBoundsException("Index out of bounds.");
    }

    Cube retValue = new Cube(inputCount(), outputCount());
    retValue.outputComplement();
    retValue.setOutput(OUTPUT, index);

    return retValue;
  }

  /**
   * Param can be null. It just won't be added.
   *
   * @param cube {@link Cube}
   */
  public void add(Cube cube) {
    cubes.add(cube);
  }

  public void addAll(Cover cover) {
    cubes.addAll(cover.cubes);
  }

  public void addAll(Collection<? extends Cube> c) {
    cubes.addAll(c);
  }

  public void remove(Cube cube) {
    cubes.remove(cube);
  }

  public Cube remove(int index) {
    return cubes.remove(index);
  }

  public Cube get(int index) {
    return cubes.get(index);
  }

  private void indexOutOfBoundCheck(int index) {
    if (index < 0 || index >= size()) {
      throw new ArrayIndexOutOfBoundsException("Index out of bounds.");
    }
  }

  public void set(int index, Cube cube) {
    indexOutOfBoundCheck(index);
    cubes.set(index, cube);
  }

  public void swapCubes(int index1, int index2) {
    indexOutOfBoundCheck(index1);
    indexOutOfBoundCheck(index2);

    cubes.swapCubes(index1, index2);
  }

  /**
   * Method will sort {@link Cube}s according to the given
   * {@link Comparator}.
   * <p>
   * Note: This is an in place transformation.
   *
   * @param comparator {@link Comparator}
   */
  public void sort(Comparator<Cube> comparator) {
    cubes.sort(comparator);
  }

  public void shuffle() {
    cubes.shuffle();
  }

  public void shuffle(long seed) {
    cubes.shuffle(seed);
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

  public int literalCount() {
    int retValue = 0;

    for (int i = 0; i < inputCount(); i++) {
      retValue += getOneColumnCount(i) + getZeroColumnCount(i);
    }

    return retValue;
  }

  /**
   * This method returns first new cover which is equivalent
   * to the current cover but the new cover will contain
   * {@link Cube}s that have only one {@link OutputState#OUTPUT}
   * in their output part.
   * <p>
   * Warning: The new cover may be very large.
   *
   * @return {@link Cover}
   */
  public Cover unwrap() {
    Cover retValue = new Cover(inputCount(), outputCount());

    for (Cube cube : this) {
      Cover unwrappedCube = cube.unwrap();
      retValue.addAll(unwrappedCube);
    }

    return retValue;
  }

  /**
   * The method tells if this cover is unate.
   * A cover is unate if it contains first column that doesn't have
   * {@link InputState#ONE} and {@link InputState#ZERO} at the same time.
   * For example first column with only {@link InputState#ONE} and
   * {@link InputState#DONTCARE} indicates first unate cover.
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
   * The chosen variable will be used for first Shannon expansion.
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

    if (isUnate()) {
      return binateSelectUnateCase();
    }

    int maxSum = -1;
    int maxIndex = -2;

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

  private int binateSelectUnateCase() {
    int variableCount = cubes.getInputLength();

    for (int i = 0; i < variableCount; i++) {
      if (getZeroColumnCount(i) != 0 || getOneColumnCount(i) != 0) {
        return i;
      }
    }

    throw new UnsupportedOperationException(
        "Input parts of the given cover contain only dontcare values. Binate selection can't be performed."
    );
  }

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
    if (cubes.size() == 0) {
      throw new UnsupportedOperationException("Cube is empty!");
    }

    Cube splitCube = generateVariableCube(splitIndex);
    Cube complement = new Cube(splitCube).inputComplement();

    Cover[] retValue = new Cover[2];
    retValue[0] = cofactor(complement);
    retValue[1] = cofactor(splitCube);

    return retValue;
  }

  /**
   * Method returns first new cover which is first cofactor of this cover
   * with respect to the given cover (given parameter).<br/>
   * Note: first.cofactor(second) and second.cofactor(first) won't yield same results.
   * Note: A cofactor between covers doesn't have to exists. In that case,
   * the method will return an empty cover ({@link Cover#cubes} size will
   * be zero).
   *
   * @param other {@link Cube}.
   * @return {@link Cover}.
   */
  public Cover cofactor(Cube other) {
    checkCoverCompatibility(Cover.of(other.copy()));
    Cover retValue = new Cover(inputCount(), outputCount());

    for (Cube cube : cubes) {
      Cube cubeCofactor = cube.cofactor(other);
      if (cubeCofactor != null)
        retValue.cubes.add(cubeCofactor);
    }

    return retValue;
  }

  public Pair<Cover, List<Integer>> trackingCofactor(Cube other) {
    return trackingCofactor(other, null);
  }

  public Pair<Cover, List<Integer>> trackingCofactor(Cube other, List<Integer> previousTrack) {
    checkCoverCompatibility(Cover.of(other));
    if (previousTrack != null && previousTrack.size() != size()) {
      throw new IllegalArgumentException(
          "Previous tracker should be the same size as the current cover."
      );
    }

    Cover cofactor = new Cover(inputCount(), outputCount());
    List<Integer> indexTrack = new ArrayList<>();

    for (int i = 0; i < this.size(); ++i) {
      Cube coverCube = this.get(i);
      Cube cubeCofactor = coverCube.cofactor(other);

      if (cubeCofactor != null) {
        cofactor.add(cubeCofactor);
        int index = (previousTrack != null ? previousTrack.get(i) : i);
        indexTrack.add(index);
      }
    }

    return new Pair<>(cofactor, indexTrack);
  }

  /**
   * Method tells whether the cover has first row full
   * of {@link InputState#DONTCARE} values.
   * <p>
   * Note: This is first brute force function that should be
   * used on small {@link Cover}s only.
   *
   * @return true if it has first row full of DONTCARE values.
   */
  public boolean hasDONTCARERow() {
    for (Cube cube : this) {
      if (cube.isInputPartTautology()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns first complement of this cover.
   *
   * @return {@link Cover}
   * @see Complement#singleOutputComplement(Cover)
   * @see Complement#complement(Cover, Cover)
   */
  public Cover complement() {
    if (outputCount() == 1) {
      return Complement.singleOutputComplement(this);
    } else {
      return Complement.complement(this, new Cover(inputCount(), outputCount()));
    }
  }

  public Cover complement(Cover dontcareSet) {
    checkCoverCompatibility(dontcareSet);
    return Complement.complement(this, dontcareSet);
  }

  /**
   * Method returns an intersect between this cover and another
   * cover (given parameter). Intersect of covers is first cover and
   * it may or may not exist.
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover intersect(Cover other) {
    checkCoverCompatibility(other);
    Cover retValue = new Cover(inputCount(), outputCount());

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
   * Method returns first new cover which is first simple smallestCubeContainingBoth of both
   * covers.
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover union(Cover other) {
    checkCoverCompatibility(other);
    Cover retValue = new Cover(inputCount(), outputCount());

    retValue.cubes.addAll(cubes);
    retValue.cubes.addAll(other.cubes);

    return retValue;
  }

  /**
   * Tells if this cover contains first cube that is LITERALLY
   * identical to the given {@link Cube}.
   *
   * @param cube {@link Cube}
   * @return true if the cube is found
   */
  public boolean hasCube(Cube cube) {
    for (Cube c : this) {
      if (c.equals(cube)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Use for debug purposes only and use it on small covers.
   * <p>
   * Warning: This method doesn't check if two covers are
   * logically equivalent. It checks if the covers have
   * same {@link Cube}s.
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
      if (!other.hasCube(cube)) {
        return false;
      }
    }

    return true;
  }
}

