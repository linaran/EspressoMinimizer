package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.Comparator;

public final class CubeSizeComparator implements Comparator<Cube> {

  @Override
  public int compare(Cube o1, Cube o2) {
    return Integer.valueOf(dontcareCount(o1)).compareTo(dontcareCount(o2));
  }

  private int dontcareCount(Cube cube) {
    int retValue = 0;

    for (int i = 0; i < cube.inputLength(); i++) {
      if (cube.getInputState(i) == InputState.DONTCARE) {
        retValue++;
      }
    }

    return retValue;
  }
}
