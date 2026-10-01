package hexa.test.fakes;

import hexa.domain.Account;
import hexa.ports.out.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * FAKE d'adaptateur de sortie.
 *
 * <p>Pourquoi ça marche sans effort : le core ne dépend que de
 * l'INTERFACE AccountRepository. Pour tester, on branche un faux
 * — ni fichier, ni base de données, pas d'IO, pas d'état partagé.
 * C'est exactement la liberté que l'architecture hexagonale achète.
 */
public class FakeAccountRepository implements AccountRepository {

    private final Map<String, Account> store = new HashMap<>();

    public void seed(Account account) {
        store.put(account.getId(), new Account(account));
    }

    @Override
    public Account save(Account account) {
        store.put(account.getId(), new Account(account));
        return account;
    }

    @Override
    public Optional<Account> findById(String id) {
        Account account = store.get(id);
        return account == null ? Optional.empty() : Optional.of(new Account(account));
    }
}
