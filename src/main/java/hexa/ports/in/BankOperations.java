package hexa.ports.in;

/**
 * Port d'ENTRÉE : comment le monde extérieur dialogue avec le core.
 *
 * <p>Le core déclare les cas d'usage (credit, debit, getBalance) ;
 * des adaptateurs d'entrée (CLI ici, et demain un serveur REST, un
 * script batch, un test automatisé…) implémentent cette interface
 * et traduisent leur propre langue (texte, JSON…) en appels de ports.
 */
public interface BankOperations {

    void credit(String accountId, long amountCents, String description);

    void debit(String accountId, long amountCents, String description);

    long getBalanceCents(String accountId);
}
