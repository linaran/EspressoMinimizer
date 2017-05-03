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

  private Pair<Integer, Cube> maxCubeIndex(Cover cover) {
    Integer maxCubeIndex = 0;
    Cube maxCube = cover.get(0);

    for (int i = 0; i < cover.size(); i++) {
      Cube cube = cover.get(i);

      if (maxCube.dontcareCount() < cube.dontcareCount()) {
        maxCube = cube;
        maxCubeIndex = i;
      }
    }

    return new Pair<>(maxCubeIndex, maxCube);
  }

  private void setMaxCubeAsFirst(Cover cover, int index) {
    Cube token = cover.get(index);
    cover.set(index, cover.get(0).copy());
    cover.set(0, token);
  }

  /**
   * Warning: In place organization.
   *
   * @param cover {@link Cover}
   */
  public void organize(Cover cover) {
    Pair<Integer, Cube> pair = maxCubeIndex(cover);
    Integer maxCubeIndex = pair.first;
    Cube maxCube = pair.second;

    setMaxCubeAsFirst(cover, maxCubeIndex);

    cover.sort(new MaxCubeDistanceComparator(maxCube));
  }
}
