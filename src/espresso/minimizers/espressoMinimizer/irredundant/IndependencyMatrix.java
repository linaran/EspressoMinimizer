package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

public class IndependencyMatrix extends BooleanMatrix {

  public IndependencyMatrix(BooleanMatrix matrix) {
    super(new IndependencyMatrixElementGenerator(matrix));
  }

}
