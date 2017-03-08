package espresso.boolFunction;

/**
 * Enum for maintaining containment relations.
 * Meant to be used in {@link InputState} and {@link OutputState}.
 */
public enum Containment {
  NOT_CONTAIN,
  CONTAIN,
  STRICT_CONTAIN;

  public static final Containment[][] inputContainment = new Containment[][]{
      {CONTAIN, NOT_CONTAIN, NOT_CONTAIN},
      {NOT_CONTAIN, CONTAIN, NOT_CONTAIN},
      {STRICT_CONTAIN, STRICT_CONTAIN, CONTAIN}
  };

  public static final Containment[][] outputContainment = new Containment[][]{
      {CONTAIN, NOT_CONTAIN},
      {STRICT_CONTAIN, CONTAIN}
  };
}
