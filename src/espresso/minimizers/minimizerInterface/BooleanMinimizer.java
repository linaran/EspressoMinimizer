package espresso.minimizers.minimizerInterface;

import espresso.boolFunction.Cover;

public abstract class BooleanMinimizer {

  public Cover onSetMinimization(Cover onSet) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public Cover onSetDontcareMinimization(Cover onSet, Cover dontcare) {
    throw new UnsupportedOperationException("Not implemented.");
  }
}
