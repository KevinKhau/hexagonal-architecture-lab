package hexa.test;

import java.util.List;

/**
 * Point d'entrée des tests : {@code java hexa.test.TestRunner}
 *
 * <p>Chaque test est une méthode sans argument de
 * {@link ExerciseTests} ; elles s'exécutent dans l'ordre,
 * un échec affiche le test fautif mais le reste continue.
 */
public final class TestRunner {

    public static void main(String[] args) {
        ExerciseTests tests = new ExerciseTests();

        List<Method> methods = List.of(
                new Method("exo1_creditIncreasesBalanceAndNotifies", tests::exo1_creditIncreasesBalanceAndNotifies),
                new Method("exo1_debitDecreasesBalanceAndNotifies", tests::exo1_debitDecreasesBalanceAndNotifies),
                new Method("exo1_debitOverBalanceIsRefused", tests::exo1_debitOverBalanceIsRefused),
                new Method("exo1_unknownAccountIsAnError", tests::exo1_unknownAccountIsAnError),
                new Method("exo2_noMutableLeakThroughRepository", tests::exo2_noMutableLeakThroughRepository),
                new Method("exo3_fileRoundTrip", tests::exo3_fileRoundTrip),
                new Method("exo3_fileSaveReplacesExisting", tests::exo3_fileSaveReplacesExisting),
                new Method("exo5_auditFileNotifierAppends", tests::exo5_auditFileNotifierAppends)
        );

        int passed = 0;
        int failed = 0;
        for (Method m : methods) {
            try {
                m.body().run();
                passed++;
                System.out.println("  ✔ " + m.name());
            } catch (Throwable t) {
                failed++;
                System.out.println("  ✘ " + m.name());
                System.out.println("      " + t.getMessage());
            }
        }

        System.out.println();
        System.out.println(passed + " réussi(s), " + failed + " échoué(s) sur " + methods.size());
        if (failed > 0) {
            System.exit(1);
        }
    }

    record Method(String name, Throwing body) {
    }

    @FunctionalInterface
    private interface Throwing {
        void run() throws Exception;
    }
}
