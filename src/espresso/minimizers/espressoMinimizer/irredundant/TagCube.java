package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.boolFunction.cube.Cube;

public class TagCube extends Cube {

  private int tag = -1;

  public TagCube(Cube cube, int tag) {
    super(cube);
    this.tag = tag;
  }

  public TagCube(Cube cube) {
    super(cube);
    if (cube instanceof TagCube) {
      tag = ((TagCube) cube).getTag();
    }
  }

  public int getTag() {
    return tag;
  }

  @Override
  public TagCube cofactor(Cube other) {
    Cube cofactorResult = super.cofactor(other);
    return (cofactorResult != null) ? new TagCube(cofactorResult, tag) : null;
  }
}
