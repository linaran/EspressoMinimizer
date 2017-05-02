package espresso.minimizers.espressoMinimizer;


import espresso.boolFunction.Cover;
import espresso.minimizers.espressoMinimizer.expand.Expand;
import espresso.minimizers.espressoMinimizer.irredundant.Irredundant;
import espresso.minimizers.minimizerInterface.BooleanOnSetDontCareMinimizer;
import espresso.minimizers.minimizerInterface.BooleanOnSetMinimizer;

public class EspressoMinimizer implements BooleanOnSetDontCareMinimizer, BooleanOnSetMinimizer {
  private static EspressoMinimizer instance = new EspressoMinimizer();

  public static EspressoMinimizer getInstance() {
    return instance;
  }

  private EspressoMinimizer() {
  }

  @Override
  public Cover minimize(Cover onSet, Cover dontcareSet) {
    Cover offSet = onSet.complement(dontcareSet);

    Cover expandedOnSet = Expand.expandCover(onSet, offSet);

    return Irredundant.irredundantCover(expandedOnSet, dontcareSet);
  }

  @Override
  public Cover minimize(Cover onSet) {
    return minimize(onSet, new Cover(onSet.inputCount(), onSet.outputCount()));
  }
}
