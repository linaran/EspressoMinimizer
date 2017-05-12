package espresso.minimizers.espressoMinimizer;


import espresso.boolFunction.Cover;
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

    Cover offSet = onSet.complement(dontcareSet);

    Cover latestOnSet = new Cover(onSet);
    int killCount = 5;
    while (true) {
      Cover expandedOnSet = Expand.expandCover(latestOnSet, offSet);
      Cover irredundantSet = Irredundant.irredundantCover(expandedOnSet, dontcareSet);

      int irredundantSize = irredundantSet.size();
      int irredundantLiteralCount = irredundantSet.literalCount();

      int latestSize = latestOnSet.size();
      int latestLiteralCount = latestOnSet.literalCount();

      if (irredundantSize < latestSize || irredundantLiteralCount < latestLiteralCount) {
        latestOnSet = Reduce.reduce(irredundantSet, dontcareSet);
      } else if (killCount > 0) {
        killCount--;
      } else {
        break;
      }
    }

    return latestOnSet;
  }

  @Override
  public Cover minimize(Cover onSet) {
    return minimize(onSet, new Cover(onSet.inputCount(), onSet.outputCount()));
  }
}
