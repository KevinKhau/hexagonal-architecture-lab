package hexa.application;

import hexa.domain.Account;
import hexa.domain.Transaction;
import hexa.ports.in.BankOperations;
import hexa.ports.out.AccountRepository;
import hexa.ports.out.TransactionNotifier;

import java.util.List;

/**
 * Service de cas d'usage : le « cerveau » de l'application.
 *
 * <p>═══════════════════════════════════════════════════════════
 * EXERCICE 1 — implémente les trois méthodes ci-dessous.
 * ═════════════════════════════════════════════════════════════
 *
 * <p>La recette, à chaque opération :
 * <ol>
 *   <li>charger le compte via le repository (port de sortie),</li>
 *   <li>appliquer la mutation à l'objet du domaine ({@code credit} / {@code debit}
 *       — c'est là que les règles métier vivent),</li>
 *   <li>sauvegarder via le repository,</li>
 *   <li>prévenir chacun des notifiers (port de sortie).</li>
 * </ol>
 *
 * <p>Règle d'or : cette classe ne connaît que des INTERFACES (les ports).
 * Jamais d'import d'un adaptateur concret (mémoire, fichier, console…).
 */
public class AccountService implements BankOperations {

    private final AccountRepository repository;
    private final List<TransactionNotifier> notifiers;

    public AccountService(AccountRepository repository, List<TransactionNotifier> notifiers) {
        this.repository = repository;
        this.notifiers = List.copyOf(notifiers);
    }

    @Override
    public void credit(String accountId, long amountCents, String description) {
        // TODO (EXO 1) — la solution est esquissée en commentaire, à comprendre puis à réécrire :
        //
        //   Account account = loadAccount(accountId);
        //   account.credit(amountCents);
        //   repository.save(account);
        //   notify(account, new Transaction(Transaction.Type.CREDIT, amountCents, description));
        throw new UnsupportedOperationException("TODO EXO 1 : credit");
    }

    @Override
    public void debit(String accountId, long amountCents, String description) {
        // TODO (EXO 1) — même enchaînement que credit, avec Type.DEBIT.
        // Attention : si account.debit() lève InsufficientFundsException,
        // rien ne doit être sauvegardé ni notifié (l'exception sort seule).
        throw new UnsupportedOperationException("TODO EXO 1 : debit");
    }

    @Override
    public long getBalanceCents(String accountId) {
        // TODO (EXO 1) — un seul port à appeler ici :
        //
        //   return loadAccount(accountId).getBalanceCents();
        throw new UnsupportedOperationException("TODO EXO 1 : getBalanceCents");
    }

    // ── Private helpers fournis ──

    private Account loadAccount(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Compte inconnu : " + accountId));
    }

    private void notify(Account account, Transaction transaction) {
        for (TransactionNotifier notifier : notifiers) {
            notifier.notifyTransaction(account, transaction);
        }
    }
}
