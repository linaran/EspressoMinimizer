package espresso.minimizers.espressoMinimizer.irredundant;

import espresso.boolFunction.Cover;
import org.junit.Test;

import static org.junit.Assert.*;


public class TagCoverTest {

  @Test
  public void cubeTagsShouldBeTrackedThroughCofactorOperation() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    TagCover tagCover = new TagCover(cover);
    TagCover cofactor = tagCover.cofactor(tagCover.generateVariableCube(0));

    assertEquals(
        "Tag wasn't tracked properly.",
        4,
        cofactor.get(0).getTag()
    );

    assertEquals(
        "Tag wasn't tracked properly.",
        5,
        cofactor.get(1).getTag()
    );
  }
}