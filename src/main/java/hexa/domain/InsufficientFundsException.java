package hexa.domain;

/**
 * Exception de domaine : le solde est insuffisant.
 *
 * <p>C'est le <b>core</b> qui la lève. Les adaptateurs d'entrée
 * (CLI, API REST…) la « traduisent » en message adapté à leur canal :
 * une ligne dans le terminal, un code HTTP 422, etc.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountId, long balanceCents, long requestedCents) {
        super("Solde insuffisant : le compte " + accountId + " porte "
                + balanceCents + " c, demande de " + requestedCents + " c");
    }
}
