package hexa.test.fakes;

import hexa.domain.Account;
import hexa.domain.Transaction;
import hexa.ports.out.TransactionNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * FAKE d'adaptateur de sortie : enregistre tout ce qu'on lui envoie
 * pour pouvoir l'inspecter dans les tests.
 */
public class RecordingNotifier implements TransactionNotifier {

    public final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void notifyTransaction(Account account, Transaction transaction) {
        transactions.add(transaction);
    }
}
