package espresso.minimizers.espressoMinimizer;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.expand.Expand;
import espresso.minimizers.espressoMinimizer.irredundant.Irredundant;
import espresso.minimizers.espressoMinimizer.reduce.Reduce;
import espresso.minimizers.minimizerInterface.BooleanOnSetDontCareMinimizer;
import espresso.minimizers.minimizerInterface.BooleanOnSetMinimizer;

public class SingleOutputEspressoMinimizer implements BooleanOnSetDontCareMinimizer, BooleanOnSetMinimizer {
  private static SingleOutputEspressoMinimizer instance = new SingleOutputEspressoMinimizer();

  public static SingleOutputEspressoMinimizer getInstance() {
    return instance;
  }

  private SingleOutputEspressoMinimizer() {
  }

  @Override
  public Cover minimize(Cover onSet, Cover dontcareSet) {
    if (onSet.outputCount() != 1 || dontcareSet.outputCount() != 1) {
      throw new UnsupportedOperationException(
          "Multiple output minimizer hasn't been implemented yet."
      );
    }

    if (onSet.size() == 0) {
      return new Cover(onSet);
    }

    Cover offSet = onSet.complement(dontcareSet);
    if (offSet.size() == 0) {
//      Given cover is tautology.
      return new Cover(new Cube(onSet.inputCount(), onSet.outputCount()));
    }

    Cover currentOnSet = new Cover(onSet);
    Cover latestMinSet = new Cover(onSet);
    int killCount = 50;
    while (killCount > 0) {
      Cover expandedOnSet = Expand.expandCover(currentOnSet, offSet);
      Cover irredundantSet = Irredundant.irredundantCover(expandedOnSet, dontcareSet);

      int irredundantSize = irredundantSet.size();
      int irredundantLiteralCount = irredundantSet.literalCount();

      int latestSize = latestMinSet.size();
      int latestLiteralCount = latestMinSet.literalCount();

      currentOnSet = Reduce.reduce(irredundantSet, dontcareSet);

      if (irredundantSize < latestSize || irredundantLiteralCount < latestLiteralCount) {
        latestMinSet = irredundantSet;
      } else if (killCount > 0) {
        killCount--;
      }
    }

    return latestMinSet;
  }

  @Override
  public Cover minimize(Cover onSet) {
    return minimize(onSet, new Cover(onSet.inputCount(), onSet.outputCount()));
  }
}
