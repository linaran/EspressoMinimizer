package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

public class SingleOutputBlockMatrix extends BooleanMatrix {

  public SingleOutputBlockMatrix(Cover cover, Cube cube) {
    super(new BlockMatrixElementGenerator(cover, cube));
  }
}
