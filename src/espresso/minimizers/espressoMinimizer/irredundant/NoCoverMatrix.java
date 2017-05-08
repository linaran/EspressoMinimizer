package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.Iterator;
import java.util.List;

public class NoCoverMatrix extends BooleanMatrix {

  public NoCoverMatrix(List<List<Integer>> minSets, int columnCount) {
    super(new NoCoverMatrixElementGenerator(minSets, columnCount));
  }

}
