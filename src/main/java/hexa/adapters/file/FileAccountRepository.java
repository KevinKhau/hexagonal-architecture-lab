package hexa.adapters.file;

import hexa.domain.Account;
import hexa.ports.out.AccountRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptateur de sortie : persistance dans un fichier texte.
 *
 * <p>═══════════════════════════════════════════════════════════
 * EXERCICE 3 — implémente {@code save} et {@code findById}.
 * ═════════════════════════════════════════════════════════════
 *
 * <p>Format de fichier, UNE ligne par compte :
 * <pre>
 *   A1|Alice|1000
 *   B2|Bob|250
 * </pre>
 * soit {@code id|owner|balanceCents}.
 *
 * <p>Astuces :
 * <ul>
 *   <li>{@code Files.readAllLines(file)} / {@code Files.write(file, lines)}</li>
 *   <li>{@code line.split("\\|")} — attention au pipe dans une regex, d'où le double backslash</li>
 *   <li>si le fichier n'existe pas, on part d'une liste vide</li>
 * </ul>
 *
 * <p>Le point à digérer : ce fichier, ce format, ce {@code Path}…
 * ce sont des détails qui concernent SEULEMENT cet adaptateur.
 * Le core n'en sait rien, et ne doit rien en savoir.
 */
public class FileAccountRepository implements AccountRepository {

    private final Path file;

    public FileAccountRepository(Path file) {
        this.file = file;
    }

    @Override
    public Account save(Account account) {
        // TODO (EXO 3) :
        //  1. lire les lignes existantes (si le fichier existe),
        //  2. remplacer la ligne du compte (ou l'ajouter),
        //  3. réécrire le fichier complet.
        throw new UnsupportedOperationException("TODO EXO 3 : save");
    }

    @Override
    public Optional<Account> findById(String id) {
        // TODO (EXO 3) :
        //  parcourir les lignes, comparer l'id, reconstruire un Account.
        //  Fichier absent ou aucun match → Optional.empty().
        throw new UnsupportedOperationException("TODO EXO 3 : findById");
    }

    // ── Helpers fournis (IO seule — la logique de format est à toi) ──

    protected List<String> readLines() {
        try {
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Files.readAllLines(file));
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + file, e);
        }
    }

    protected void writeLines(List<String> lines) {
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire " + file, e);
        }
    }

    /** Formate un compte en une ligne {@code id|owner|balance}. */
    protected String toLine(Account account) {
        return account.getId() + "|" + account.getOwner() + "|" + account.getBalanceCents();
    }
}
