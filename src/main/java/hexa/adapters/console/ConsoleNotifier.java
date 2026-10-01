package hexa.adapters.console;

import hexa.domain.Account;
import hexa.domain.Transaction;
import hexa.ports.out.TransactionNotifier;

import java.io.PrintStream;

/**
 * Adaptateur de sortie : notification sur la console.
 *
 * <p>Fourni en exemple — regarde comment il :
 * <ul>
 *   <li>ne contient AUCUNE logique métier (pas de calcul, pas de règle),</li>
 *   <li>ne fait que « traduire » un {@link Transaction} en texte,
 *       dans le style de SON canal (la console).</li>
 * </ul>
 * Le {@link PrintStream} est injecté (et non {@code System.out} en dur)
 * pour pouvoir capter la sortie dans les tests.
 */
public class ConsoleNotifier implements TransactionNotifier {

    private final PrintStream out;

    public ConsoleNotifier(PrintStream out) {
        this.out = out;
    }

    @Override
    public void notifyTransaction(Account account, Transaction transaction) {
        String sign = transaction.getType() == Transaction.Type.CREDIT ? "+" : "-";
        out.println(String.format("[NOTIFY] compte %s : %s%.2f EUR (%s)",
                account.getId(), sign, transaction.getAmountCents() / 100.0,
                transaction.getDescription()));
    }
}
