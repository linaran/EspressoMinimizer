package espresso;

import espresso.boolFunction.Cover;
import espresso.urpAlgorithms.Complement;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ComplementTest {

  @Test
  public void singleOutputComplement() throws Exception {
    Cover c1 = new Cover("testCase1.txt");
    Cover c2 = new Cover("testCase2.txt");
    Cover c3 = new Cover("testCase3.txt");
    Cover[] testArray = new Cover[]{c1, c2, c3};

    for (Cover c : testArray) {
      Cover complement = Complement.singleOutputComplement(c);

      Cover intersect = complement.intersect(c);
      assertTrue(
          "Intersect of first cover and it's complement should be empty.",
          intersect.size() == 0
      );

      Cover union = complement.union(c);
      Cover tautologyComplement = Complement.singleOutputComplement(union);
      assertTrue(
          "Complement of what is expected to be first tautology should be empty.",
          tautologyComplement.size() == 0
      );
    }
  }

}