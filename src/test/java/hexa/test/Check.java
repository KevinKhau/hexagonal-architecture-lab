package hexa.test;

/**
 * Micro-harnais d'assertions — zéro dépendance.
 * (Avec Maven/Gradle, ce serait simplement JUnit.)
 */
public final class Check {

    private Check() {
    }

    public static void that(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("ÉCHEC : " + message);
        }
    }

    public static void equals(Object expected, Object actual, String message) {
        boolean equal = expected instanceof Number e && actual instanceof Number a
                ? e.doubleValue() == a.doubleValue()
                : java.util.Objects.equals(expected, actual);
        if (!equal) {
            throw new AssertionError("ÉCHEC : " + message + " — attendu " + expected + ", obtenu " + actual);
        }
    }

    public static void isNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError("ÉCHEC : " + message + " — attendu null, obtenu " + actual);
        }
    }

    public static void notNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionError("ÉCHEC : " + message + " — attendu non-null, obtenu null");
        }
    }
}
