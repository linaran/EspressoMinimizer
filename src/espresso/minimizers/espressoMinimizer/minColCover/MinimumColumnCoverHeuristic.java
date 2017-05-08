package espresso.minimizers.espressoMinimizer.minColCover;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.Set;

public interface MinimumColumnCoverHeuristic {

  Set<Integer> calculateMinimumColumnCover(BooleanMatrix matrix);
}
