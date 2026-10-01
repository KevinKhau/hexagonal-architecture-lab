package hexa.domain;

/**
 * Objet du domaine : une transaction (crédit ou débit).
 * Immutable par nature — c'est un fait, pas un état modifiable.
 */
public final class Transaction {

    public enum Type { CREDIT, DEBIT }

    private final Type type;
    private final long amountCents;
    private final String description;

    public Transaction(Type type, long amountCents, String description) {
        this.type = type;
        this.amountCents = amountCents;
        this.description = description == null ? "" : description;
    }

    public Type getType() {
        return type;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public String getDescription() {
        return description;
    }
}
