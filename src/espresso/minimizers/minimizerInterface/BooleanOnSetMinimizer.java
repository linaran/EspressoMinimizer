package espresso.minimizers.minimizerInterface;


import espresso.boolFunction.Cover;

public interface BooleanOnSetMinimizer {

  public Cover minimize(Cover onSet);
}
