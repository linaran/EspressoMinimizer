package espresso.minimizers.espressoMinimizer;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.urpAlgorithms.Complement;
import espresso.urpAlgorithms.Tautology;
import org.junit.Test;

import static org.junit.Assert.*;

public class TautologyTest {

  @Test
  public void singleOutputTautologyCheck() throws Exception {
    Cover c1 = new Cover("testCase1.txt");
    Cover c2 = new Cover("testCase2.txt");
    Cover c3 = new Cover("testCase3.txt");
    Cover[] testArray = new Cover[]{c1, c2, c3};

    for (Cover c : testArray) {
      Cover complement = Complement.singleOutputComplement(c);

      Cover tautology = complement.union(c);

      assertTrue(
          "Union of complement and original cover should be first tautology.",
          Tautology.singleOutputTautologyCheck(tautology)
      );
    }

    Cover c = new Cover(new Cube(new InputState[]{InputState.ONE}, new OutputState[]{OutputState.OUTPUT}));
    assertTrue(
        "This is not tautology.",
        !Tautology.singleOutputTautologyCheck(c)
    );
  }

}