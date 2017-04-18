package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.List;

public class AuxiliaryMatrix extends BooleanMatrix {
  public AuxiliaryMatrix(List<List<Integer>> minSets, int columnCount) {
    super(new AuxiliaryMatrixElementGenerator(minSets, columnCount));
  }
}
