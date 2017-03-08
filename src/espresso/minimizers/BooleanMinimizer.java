package espresso.minimizers;

import espresso.boolFunction.Cover;

public interface BooleanMinimizer {

  Cover minimize(Cover cover);
}
