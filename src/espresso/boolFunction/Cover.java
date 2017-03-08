package espresso.boolFunction;

import java.util.Collections;
import java.util.Iterator;

import static espresso.boolFunction.InputState.DONTCARE;

/**
 * By definition cover is a set of cubes.
 * In a sense of boolean algebra, cover is a matrix representation of a boolean function.
 */
public class Cover implements Iterable<Cube> {
  private CubeSet cubes;

  public Cover() {
    cubes = new CubeSet();
  }

  public Cover(Cube... cubes) {
    if (cubes == null) throw new NullPointerException("Parameter can't be null.");

    int inputLength = cubes[0].inputLength();
    int outputLength = cubes[0].outputLength();
    this.cubes = new CubeSet();

    Collections.addAll(this.cubes, cubes);
  }

  /**
   * Copy constructor.
   *
   * @param cover {@link Cover}.
   */
  public Cover(Cover cover) {
    cubes = new CubeSet(cover.cubes);
  }

  public static Cover of(Cube... cubes) {
    return new Cover(cubes);
  }

  @Override
  public Iterator<Cube> iterator() {
    return cubes.iterator();
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

  public boolean add(Cube cube) {
    return cubes.add(cube);
  }

  public boolean addAll(Cover cover) {
    return cubes.addAll(cover.cubes);
  }

  public boolean remove(Cube cube) {
    return cubes.remove(cube);
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
   * Allowed input count for the cubes in the cover.
   * If the method returns 0 then the cover is waiting
   * for the first cube to be added to the set. After
   * the first cube is added, allowed counts are fixed.
   *
   * @return primitive int.
   */
  public int inputCount() {
    return cubes.getInputLength();
  }

  /**
   * Allowed output count for the cubes in the cover.
   * If the method returns 0 then the cover is waiting
   * for the first cube to be added to the set. After
   * the first cube is added, allowed counts are fixed.
   *
   * @return primitive int.
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
   * This method renews the columnCount field.
   *
   * @return true if the cover is unate.
   */
  public boolean isUnate() {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

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

    for (int i = 0; i < variableCount; i++)
      if (getOneColumnCount(i) + getZeroColumnCount(i) > maxSum) {
        maxIndex = i;
        maxSum = getOneColumnCount(i) + getZeroColumnCount(i);
      }

    return maxIndex;
  }

//////////////////////////////////////////////////////////////////////////////
//  Cover operations
//////////////////////////////////////////////////////////////////////////////

//  /**
//   * Method tells how monotone this cover is. Cover can be increasing
//   * monotone, decreasing monotone or not monotone at all. A cover is
//   * increasing/decreasing monotone if it is increasing/decreasing
//   * monotone in all of it's input variables. For a full explanation
//   * see {@link Cover#unateStatus(int)}. <br/>
//   * Note: Unate positive/negative is a synonym for monotone increasing/decreasing.
//   *
//   * @return primitive int 1 is positive unate, -1 is negative unate, 0 isn't unate.
//   */
//  public int unateStatus() {
//    int variableCount = cubes.iterator().next().inputLength();
//    boolean increasing = true;
//    boolean decreasing = true;
//
//    for (int i = 0; i < variableCount; i++) {
//      if (unateStatus(i) == 1) decreasing = false;
//      if (unateStatus(i) == -1) increasing = false;
//      if (!decreasing && !increasing) return 0;
//    }
//
//    if (increasing) return 1;
//    return -1;
//  }

  /**
   * Method returns the Shannon expansion of this cover with
   * regard to the given cube. The Shannon expansion is returned
   * as an array of two covers. Negative part of the Shannon expansion
   * is at index 0 while the positive part of the expansion is at index 1.<br/>
   * Note: Returned array can contain covers that have no {@link Cube}s at all.
   *
   * @param cube {@link Cube}.
   * @return array of two {@link Cover}s.
   */
  public Cover[] shannonCofactors(Cube cube) {
    if (cubes.size() == 0)
      throw new UnsupportedOperationException("Cube is empty!");

    Cover[] retValue = new Cover[2];

    Cube complement = new Cube(cube).inputComplement();

    retValue[0] = cofactor(complement);
    retValue[1] = cofactor(cube);

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
}

