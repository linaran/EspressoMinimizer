package espresso.minimizers.espressoMinimizer.irredundant;

import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class NoCoverMatrixTest {

  @Test
  public void construction() throws Exception {
    List<List<Integer>> minSets = new ArrayList<>();
    minSets.add(Arrays.asList(1, 2, 3));
    minSets.add(Arrays.asList(0, 4, 5));

    int columnCount = 6;
    BooleanMatrix matrix = new NoCoverMatrix(minSets, columnCount);

    for (int i = 0; i < minSets.size(); i++) {
      for (int j = 0; j < columnCount; j++) {
        if (minSets.get(i).contains(j)) {
          assertTrue(
              "Auxiliary matrix construction is incorrect.",
              matrix.getElement(i, j)
          );
        }
      }
    }
  }
}