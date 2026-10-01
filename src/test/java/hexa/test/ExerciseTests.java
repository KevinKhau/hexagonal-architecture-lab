package hexa.test;

import hexa.adapters.file.AuditFileNotifier;
import hexa.adapters.file.FileAccountRepository;
import hexa.application.AccountService;
import hexa.domain.Account;
import hexa.domain.InsufficientFundsException;
import hexa.domain.Transaction;
import hexa.ports.in.BankOperations;
import hexa.ports.out.AccountRepository;
import hexa.test.fakes.FakeAccountRepository;
import hexa.test.fakes.RecordingNotifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests du core — avec des FAUX adaptateurs, sans IO.
 *
 * <p>Vois le schéma : on construit un {@code AccountService} (le core)
 * en lui injectant des {@link FakeAccountRepository} /
 * {@link RecordingNotifier}. Aucun fichier, aucun port réseau,
 * aucune base de données : le core se teste seul.
 */
public class ExerciseTests {

    private FakeAccountRepository repo;
    private RecordingNotifier notifier;
    private BankOperations bank;

    private void setup() {
        repo = new FakeAccountRepository();
        repo.seed(new Account("A1", "Alice", 100_00)); // 100 €
        notifier = new RecordingNotifier();
        bank = new AccountService(repo, List.of(notifier));
    }

    // ───────────────────────── EXO 1 ─────────────────────────

    public void exo1_creditIncreasesBalanceAndNotifies() {
        setup();
        bank.credit("A1", 5_00, "salaire");

        Check.equals(105_00, bank.getBalanceCents("A1"), "le crédit doit augmenter le solde");
        Check.equals(1, notifier.transactions.size(), "un notifier doit être prévenu");
        Transaction t = notifier.transactions.get(0);
        Check.equals(Transaction.Type.CREDIT, t.getType(), "la transaction notifiée doit être un crédit");
        Check.equals(5_00, t.getAmountCents(), "le montant notifié doit correspondre");
    }

    public void exo1_debitDecreasesBalanceAndNotifies() {
        setup();
        bank.debit("A1", 3_50, "courses");

        Check.equals(96_50, bank.getBalanceCents("A1"), "le débit doit diminuer le solde");
        Check.equals(1, notifier.transactions.size(), "un notifier doit être prévenu");
        Check.equals(Transaction.Type.DEBIT, notifier.transactions.get(0).getType(), "ce doit être un débit");
    }

    public void exo1_debitOverBalanceIsRefused() {
        setup();
        try {
            bank.debit("A1", 500_00, "trop cher");
            Check.that(false, "le débit de 500 € sur un solde de 100 € aurait dû refuser");
        } catch (InsufficientFundsException expected) {
            // C'est l'exception attendue.
        }
        Check.equals(100_00, bank.getBalanceCents("A1"), "le solde doit rester intact après refus");
        Check.that(notifier.transactions.isEmpty(), "rien ne doit être notifié après un refus");
    }

    public void exo1_unknownAccountIsAnError() {
        setup();
        try {
            bank.credit("INEX", 1_00, "rien");
            Check.that(false, "un compte inconnu aurait dû refuser");
        } catch (IllegalArgumentException expected) {
            // Attendu : le service traduit l'absence de compte en erreur claire.
        }
    }

    // ───────────────────────── EXO 2 ─────────────────────────

    /**
     * Test le vrai adaptateur en mémoire : l'objet renvoyé par
     * {@code findById} ne doit PAS être la même instance stockée,
     * sinon muter « le résultat » muterait silencieusement le stockage.
     */
    public void exo2_noMutableLeakThroughRepository() throws IOException {
        Path dir = Files.createTempDirectory("hexa");
        AccountRepository repo = new FileAccountRepository(dir.resolve("accounts.txt"));
        Account original = new Account("A1", "Alice", 10_00);
        repo.save(original);

        Account loaded = repo.findById("A1").orElseThrow();
        loaded.credit(999_99); // on mute la copie chargée…

        Check.equals(10_00, repo.findById("A1").orElseThrow().getBalanceCents(),
                "muter un compte chargé ne doit pas affecter le stockage (snapshot !)");
        Check.that(loaded != repo.findById("A1").orElseThrow(),
                "findById doit renvoyer une copie, pas l'objet stocké");
    }

    // ───────────────────────── EXO 3 ─────────────────────────

    public void exo3_fileRoundTrip() throws IOException {
        Path dir = Files.createTempDirectory("hexa");
        try {
            FileAccountRepository repo = new FileAccountRepository(dir.resolve("accounts.txt"));
            repo.save(new Account("A1", "Alice", 100_00));
            repo.save(new Account("B2", "Bob", 250));

            Account alice = repo.findById("A1").orElseThrow();
            Check.equals("Alice", alice.getOwner(), "le propriétaire doit survivre au round-trip");
            Check.equals(100_00, alice.getBalanceCents(), "le solde doit survivre au round-trip");

            Check.equals(250, repo.findById("B2").orElseThrow().getBalanceCents(),
                    "deux comptes doivent coexister dans le même fichier");
            Check.that(repo.findById("ZZ").isEmpty(), "un id inconnu doit donner empty");
        } finally {
            Files.walk(dir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // nettoyage best-effort
                        }
                    });
        }
    }

    public void exo3_fileSaveReplacesExisting() throws IOException {
        Path dir = Files.createTempDirectory("hexa");
        try {
            Path file = dir.resolve("accounts.txt");
            FileAccountRepository repo = new FileAccountRepository(file);
            repo.save(new Account("A1", "Alice", 100_00));
            repo.save(new Account("A1", "Alice", 90_00)); // même id, nouveau solde

            Check.equals(90_00, repo.findById("A1").orElseThrow().getBalanceCents(),
                    "save doit REMPLACER l'entrée existante, pas la dupliquer");
            Check.equals(1, Files.readAllLines(file).size(), "une seule ligne pour A1 dans le fichier");
        } finally {
            Files.walk(dir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // nettoyage best-effort
                        }
                    });
        }
    }

    // ───────────────────────── EXO 5 ─────────────────────────

    public void exo5_auditFileNotifierAppends() throws IOException {
        Path dir = Files.createTempDirectory("hexa");
        try {
            Path audit = dir.resolve("audit.log");
            AuditFileNotifier notifier = new AuditFileNotifier(audit);

            Account a1 = new Account("A1", "Alice", 100_00);
            notifier.notifyTransaction(a1, new Transaction(Transaction.Type.DEBIT, 300, "café"));
            notifier.notifyTransaction(a1, new Transaction(Transaction.Type.CREDIT, 5_00, "salaire"));

            List<String> lines = Files.readAllLines(audit);
            Check.equals(2, lines.size(), "deux transactions → deux lignes dans le journal");
            Check.equals("A1|DEBIT|300|café", lines.get(0), "format attendu : id|type|montant|description");
            Check.equals("A1|CREDIT|500|salaire", lines.get(1), "deuxième ligne attendue");
        } finally {
            Files.walk(dir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // nettoyage best-effort
                        }
                    });
        }
    }
}
