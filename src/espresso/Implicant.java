package espresso;

public class Implicant {
  public static final char DONT_CARE = '-';

  public static Boolean[] of(final String stringImplicant) {
    return of(stringImplicant, DONT_CARE);
  }

  public static Boolean[] of(final String stringImplicant, final char dontCare) {
    final int stringLength = stringImplicant.length();
    Boolean[] retValue = new Boolean[stringLength];

    for (int i = 0; i < stringLength; i++) {
      final char c = stringImplicant.charAt(i);

      if (c == '1') retValue[i] = true;
      else  if (c == '0') retValue[i] = false;
      else if (c == dontCare) retValue[i] = null;
      else throw new IllegalArgumentException("Given string implicant contains illegal characters.");
    }

    return retValue;
  }
}
