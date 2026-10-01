package hexa.adapters.file;

import hexa.domain.Account;
import hexa.domain.Transaction;
import hexa.ports.out.TransactionNotifier;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Adaptateur de sortie : journal d'audit dans un fichier.
 *
 * <p>═══════════════════════════════════════════════════════════
 * EXERCICE 5 — implémente {@code notifyTransaction}.
 * ═════════════════════════════════════════════════════════════
 *
 * <p>Une ligne APPENDUE par transaction, au format :
 * <pre>
 *   A1|DEBIT|300|café
 * </pre>
 * soit {@code id|type|amountCents|description}.
 *
 * <p>Astuce : {@code Files.write(file, lines, CREATE, APPEND)}
 * crée le fichier s'il est absent et ajoute en fin de fichier.
 *
 * <p>Et là est la leçon de l'exercice : ce notifier se branche
 * CÔTÉ À CÔTÉ du notifier console, sur le MÊME port, sans que
 * le core (AccountService) change d'une ligne.
 */
public class AuditFileNotifier implements TransactionNotifier {

    private final Path auditFile;

    public AuditFileNotifier(Path auditFile) {
        this.auditFile = auditFile;
    }

    @Override
    public void notifyTransaction(Account account, Transaction transaction) {
        String line = account.getId() + "|" + transaction.getType()
                + "|" + transaction.getAmountCents() + "|" + transaction.getDescription();
        try {
            Files.write(auditFile, List.of(line),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire le journal " + auditFile, e);
        }
    }
}
