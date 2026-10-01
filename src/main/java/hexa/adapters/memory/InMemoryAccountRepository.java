package hexa.adapters.memory;

import hexa.domain.Account;
import hexa.ports.out.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptateur de sortie : persistance en mémoire.
 *
 * <p>═══════════════════════════════════════════════════════════
 * EXERCICE 2 — implémente {@code save} et {@code findById}.
 * ═════════════════════════════════════════════════════════════
 *
 * <p>Le piège classique : {@code Account} est MUTABLE (on peut changer
 * son solde). Si tu stockes l'instance qui t'est passée, l'adaptateur
 * « fuit » un état mutable vers l'extérieur, et tester le core devient
 * aléatoire. Solution : stocker une COPIE (snapshot) à la sauvegarde,
 * et renvoyer une copie à la lecture.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> store = new HashMap<>();

    @Override
    public Account save(Account account) {
        // TODO (EXO 2) — stocke un instantané :
        //
        //   store.put(account.getId(), new Account(account));
        //   return account;
        throw new UnsupportedOperationException("TODO EXO 2 : save");
    }

    @Override
    public Optional<Account> findById(String id) {
        // TODO (EXO 2) — renvoie un instantané (pas l'objet stocké) :
        //
        //   Account account = store.get(id);
        //   return account == null ? Optional.empty() : Optional.of(new Account(account));
        throw new UnsupportedOperationException("TODO EXO 2 : findById");
    }
}
