package espresso.minimizers.minimizerInterface;


import espresso.boolFunction.Cover;

public interface BooleanOnSetDontCareMinimizer {

  public Cover minimize(Cover onSet, Cover dontcare);
}
