package espresso;

import java.util.HashSet;
import java.util.List;
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

  public Cover(Set<Cube> cubes) {
    this.cubes = cubes;
  }

  public Cover(List<Cube> cubes) {
    this.cubes = new HashSet<>(cubes);
  }

  /**
   * Copy constructor.
   *
   * @param cover {@link Cover}.
   */
  public Cover(Cover cover) {
    cubes = new HashSet<>(cover.cubes);
  }

  /**
   * Complements the cover. In place transformation.
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
   * it may or may not exist. In case there is no intersection between
   * covers, method will return null.
   *
   * @param other {@link Cover}.
   * @return {@link Cover}.
   */
  public Cover intersect(Cover other) {
    Cover retValue = new Cover();

    for (Cube cube1 : cubes) {
      for (Cube cube2 : other.cubes) {
        Cube param = cube1.and(cube2);
        if (param == null) return null;
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
}

