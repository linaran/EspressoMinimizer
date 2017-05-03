package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.cube.Cube;

import java.util.Comparator;

public final class CubeSizeComparator implements Comparator<Cube> {

  @Override
  public int compare(Cube o1, Cube o2) {
    return Integer.valueOf(o1.dontcareCount()).compareTo(o2.dontcareCount());
  }

}
