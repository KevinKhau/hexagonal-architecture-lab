package hexa.domain;

/**
 * Objet du domaine : un compte bancaire.
 *
 * <p>Appartient au <b>core</b> : ne dépend que du domaine,
 * jamais d'une technologie (fichier, HTTP, console, base de données…).
 *
 * <p>Les montants sont exprimés en <b>centimes</b> (long) — on n'utilise
 * jamais de double pour de l'argent.
 */
public final class Account {

    private final String id;
    private final String owner;
    private long balanceCents;

    public Account(String id, String owner, long balanceCents) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Un id de compte est requis");
        }
        this.id = id;
        this.owner = owner;
        this.balanceCents = balanceCents;
    }

    /** Constructeur de copie — les adaptateurs l'utilisent pour stocker un instantané. */
    public Account(Account other) {
        this(other.id, other.owner, other.balanceCents);
    }

    public void credit(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
        balanceCents += amountCents;
    }

    public void debit(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
        if (balanceCents < amountCents) {
            throw new InsufficientFundsException(id, balanceCents, amountCents);
        }
        balanceCents -= amountCents;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public long getBalanceCents() {
        return balanceCents;
    }

    /**
     * Renvoie un instantané (copie) du compte : on peut le passer à un port
     * sans laisser fuiter un objet mutable vers l'extérieur.
     */
    public Account snapshot() {
        return new Account(this);
    }
}
