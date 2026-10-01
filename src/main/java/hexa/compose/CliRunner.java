package hexa.compose;

import hexa.domain.InsufficientFundsException;
import hexa.ports.in.BankOperations;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Adaptateur d'ENTRÉE le plus simple : un main() qui pilote l'app.
 *
 * <p>Usage :
 * <pre>
 *   java hexa.compose.CliRunner memory
 *   java hexa.compose.CliRunner file
 * </pre>
 *
 * <p>Regarde comment la seule chose qui « dépend du core » ici
 * est l'INTERFACE {@link BankOperations} — pas une implémentation.
 */
public final class CliRunner {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "memory";

        BankOperations bank;
        if (mode.equals("file")) {
            Path accountsFile = Paths.get("data", "accounts.txt");
            Path auditFile = Paths.get("data", "audit.log");
            bank = AppBuilder.withFileStorage(accountsFile, auditFile);
        } else {
            bank = AppBuilder.withMemoryStorage();
        }

        System.out.println("=== Hexagonal Bank — mode " + mode + " ===");
        run(bank);
        System.out.println("Solde final A1 : " + bank.getBalanceCents("A1") / 100.0 + " EUR");
    }

    private static void run(BankOperations bank) {
        bank.credit("A1", 5_00, "salaire");
        System.out.println("Après crédit 5 € : " + bank.getBalanceCents("A1") / 100.0 + " EUR");

        bank.debit("A1", 2_00, "courses");
        System.out.println("Après débit 2 € : " + bank.getBalanceCents("A1") / 100.0 + " EUR");

        try {
            bank.debit("A1", 100_00, "voiture trop chère");
        } catch (InsufficientFundsException e) {
            // L'adaptateur d'entrée « traduit » l'exception de domaine
            // pour SON canal : ici, une ligne lisible dans le terminal.
            System.out.println("REFUSÉ : " + e.getMessage());
        }
    }
}
