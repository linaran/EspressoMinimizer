package espresso.minimizers;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import java.util.ArrayList;
import java.util.Iterator;

final public class CoverUtility {
  private CoverUtility() {
  }

  /**
   * Given two subcovers obtained by the Shannon expansion with respect to the
   * given {@link Cube}, this method computes a new {@link Cover}. A cover obtained
   * by merging the two subcovers.<br/>
   * Note: If the boolean flag in the method is false then this method becomes
   * mergeWithIdentity.
   *
   * @param h1                {@link Cover} subcover obtained by the Shannon expansion.
   * @param h2                {@link Cover} subcover obtained by the Shannon expansion.
   * @param x                 Shannon expansion was done with respect to this {@link Cube}.
   *                          Note that this parameter is copied. Given reference is not used.
   * @param removeContainment if false then this method becomes mergeWithIdentity.
   * @return {@link Cover} before performing Shannon expansion on it.
   */
  public static Cover mergeWithContainment(Cover h1, Cover h2, Cube x, boolean removeContainment) {
    Cover h3 = new Cover();
    x = x.copy();

    for (Iterator<Cube> iter1 = h1.iterator(); iter1.hasNext(); ) {
      Cube c1 = iter1.next();
      for (Iterator<Cube> iter2 = h2.iterator(); iter2.hasNext(); ) {
        Cube c2 = iter2.next();

        if (c1.equals(c2)) {
          iter1.remove();
          iter2.remove();
          h3.add(c1);
        }
      }
    }

    if (!removeContainment)
      return Cover.of(x.copy().inputComplement()).intersect(h1).
          union(Cover.of(x).intersect(h2)).
          union(h3);

    for (Iterator<Cube> iter1 = h1.iterator(); iter1.hasNext(); ) {
      Cube c1 = iter1.next();
      for (Iterator<Cube> iter2 = h2.iterator(); iter2.hasNext(); ) {
        Cube c2 = iter2.next();

        if (c1.generalContain(c2)) {
          iter2.remove();
          h3.add(c2);
        } else if (c2.generalContain(c1)) {
          iter1.remove();
          h3.add(c1);
          break; // If c1 is removed, iter1 needs to go to another example.
        }
      }
    }

    return Cover.of(x.copy().inputComplement()).intersect(h1).
        union(Cover.of(x).intersect(h2)).
        union(h3);
  }

  /**
   * Method removes any {@link Cube}s completely contained in a single another {@link Cube}.
   * Warning: Use this on functions with a small number of cubes or unate functions
   * otherwise wait for a long time for this to finish.
   *
   * @param cover {@link Cover}
   */
  public static void singleCubeContainmentCleanup(Cover cover) {
    ArrayList<Cube> deleteSet = new ArrayList<>();

    for (Cube c1 : cover) {
      for (Cube c2 : cover) {
        if (!c1.equals(c2) && c1.generalContain(c2)) {
          deleteSet.add(c2);
        }
      }
    }

    for (Cube cube : deleteSet) {
      cover.remove(cube);
    }
  }
}
