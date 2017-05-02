package espresso.minimizers;

import espresso.boolFunction.Cover;
import espresso.minimizers.minimizerInterface.BooleanOnSetMinimizer;

final public class Simplify implements BooleanOnSetMinimizer {
  private static Simplify instance = new Simplify();

  public static Simplify getInstance() {
    return instance;
  }

  private Simplify() {
  }

  @Override
  public Cover minimize(Cover cover) {
    if (cover.size() == 0) {
      return cover;
    }

    if (cover.isUnate()) {
      CoverUtility.singleCubeContainmentCleanup(cover);
      return cover;
    }

    int splitIndex = cover.binateSelect();
    Cover[] cofactors = cover.shannonCofactors(splitIndex);

    Cover newCover = CoverUtility.mergeWithContainment(
        minimize(cofactors[0]),
        minimize(cofactors[1]),
        splitIndex,
        true
    );

    //region Debug
//    System.out.println("Splitting variable:");
//    System.out.println(Cover.of(splitCube));
//    System.out.println("Splitting variable inputComplement:");
//    System.out.println(Cover.of(splitCube.copy().inputComplement()));
//    System.out.println("Cofactor[0]:");
//    System.out.println(cofactors[0].toString());
//    System.out.println("Cofactor[1]");
//    System.out.println(cofactors[1].toString());
//    System.out.println("Intersect1:");
//    System.out.println(intersect1.toString());
//    System.out.println("Intersect2:");
//    System.out.println(intersect2.toString());
//    System.out.println("New cover:");
//    System.out.println(newCover.toString());
    //    endregion

//    New cover must not be empty.
    if (newCover.size() != 0 && newCover.size() < cover.size()) {
      return newCover;
    } else {
      return cover;
    }
  }
}
