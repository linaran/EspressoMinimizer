package espresso;

import java.util.HashSet;
import java.util.Set;

/**
 * By definition cover is a set of cubes.
 * In a sense of boolean algebra, cover is a matrix representation
 * of a boolean function.
 * TODO: unate recursive paradigm.
 */
public class Cover {
  public Set<Cube> cubes;

  public Cover() {
    cubes = new HashSet<>();
  }

  public Cover(Cube... cubes) {
    if (cubes == null) throw new NullPointerException("Parameter can't be null.");

    int inputLength = cubes[0].input.length;
    int outputLength = cubes[0].output.length;
    this.cubes = new HashSet<>();

    for (Cube cube : cubes) {
      if (inputLength != cube.input.length || outputLength != cube.output.length)
        throw new IllegalArgumentException("All cubes must have same input and output length.");

      this.cubes.add(cube);
    }
  }

  /**
   * Copy constructor.
   *
   * @param cover {@link Cover}.
   */
  public Cover(Cover cover) {
    cubes = new HashSet<>(cover.cubes);
  }

  public static Cover of(Cube... cubes) {
    return new Cover(cubes);
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

//////////////////////////////////////////////////////////////////////////////
//  Cover operations
//////////////////////////////////////////////////////////////////////////////

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
    Cover retValue = new Cover();

    for (Cube cube : cubes) {
      Cube cubeCofactor = cube.cofactor(other);
      if (cubeCofactor != null)
        retValue.cubes.add(cubeCofactor);
    }

    return retValue;
  }

  /**
   * Method returns the shannon expansion of this cover with
   * regard to the given cube. The shannon expansion is returned
   * as an array of two covers. Negative part of the shannon expansion
   * is at index 0 while the positive part of the expansion is at index 1.<br/>
   * Note: Returned array can contain covers that have no {@link Cube}s at all.
   *
   * @param cube {@link Cube}.
   * @return array of two {@link Cover}s.
   */
  public Cover[] shannon(Cube cube) {
    Cover[] retValue = new Cover[2];

    Cube complement = new Cube(cube).complement();

    retValue[0] = Cover.of(complement).intersect(cofactor(complement));
    retValue[1] = Cover.of(cube).intersect(cofactor(cube));

    return retValue;
  }

  /**
   * Complements the cover. In place transformation.<br/>
   * Note: Cover complement is just {@link Cube} output part complement.
   */
  public void complement() {
    cubes.forEach(Cube::outputComplement);
  }

  /**
   * Returns a new cover which is a complemented representation
   * of the given parameter.
   *
   * @param cover {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover complement(Cover cover) {
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
//    TODO: It is unclear how this method should be implemented.
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
   */
  public Cover difference(Cover other) {
    return intersect(complement(other));
  }

  public boolean isEmpty() {
    return cubes.size() == 0;
  }
}

