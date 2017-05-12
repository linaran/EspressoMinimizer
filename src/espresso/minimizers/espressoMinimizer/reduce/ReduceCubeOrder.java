package espresso.minimizers.espressoMinimizer.reduce;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.utils.Pair;

public class ReduceCubeOrder {

  private static ReduceCubeOrder instance = new ReduceCubeOrder();

  public ReduceCubeOrder() {
  }

  public static ReduceCubeOrder getInstance() {
    return instance;
  }

  private int maxCubeIndex(Cover cover) {
    int maxCubeIndex = 0;
    boolean isIndexChosen = false;

    for (int i = 0; i < cover.size(); i++) {
      Cube cube = cover.get(i);
      Cube maxCube = cover.get(maxCubeIndex);

      if (maxCube.dontcareCount() < cube.dontcareCount()) {
        maxCubeIndex = i;
        isIndexChosen = true;
      }
    }

    return isIndexChosen ? maxCubeIndex : -1;
  }

  /**
   * Warning: In place organization.
   *
   * @param cover {@link Cover}
   */
  public void organize(Cover cover) {
    int maxCubeIndex = maxCubeIndex(cover);

    if (maxCubeIndex != -1) {
      Cube maxCube = cover.get(maxCubeIndex);
      cover.swapCubes(maxCubeIndex, 0);
      cover.sort(new MaxCubeDistanceComparator(maxCube));
    } else {
      cover.shuffle();
    }
  }
}
