package espresso.minimizers.espressoMinimizer;


import espresso.boolFunction.Cover;
import espresso.minimizers.espressoMinimizer.expand.Expand;
import espresso.minimizers.espressoMinimizer.irredundant.Irredundant;
import espresso.minimizers.minimizerInterface.BooleanMinimizer;

public class EspressoMinimizer extends BooleanMinimizer {
  private static EspressoMinimizer instance = new EspressoMinimizer();

  public static EspressoMinimizer getInstance() {
    return instance;
  }

  private EspressoMinimizer() {
  }

  @Override
  public Cover onSetDontcareMinimization(Cover onSet, Cover dontcareSet) {
    Cover offSet = onSet.complement(dontcareSet);

    Cover expandedOnSet = Expand.expandCover(onSet, offSet);

    Cover irredundantSet = Irredundant.irredundantCover(expandedOnSet, dontcareSet);

    return irredundantSet;
  }
}
