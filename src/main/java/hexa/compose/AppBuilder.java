package hexa.compose;

import hexa.application.AccountService;
import hexa.ports.in.BankOperations;
import hexa.ports.out.AccountRepository;
import hexa.ports.out.TransactionNotifier;

import java.util.List;

/**
 * RACINE DE COMPOSITION : le SEUL endroit du code qui connaît
 * des classes concrètes (adaptateurs) ET le core, et qui fait
 * le câblage (wiring) entre eux.
 *
 * <p>C'est ici que tu choisis « mémoire » ou « fichier » pour
 * le stockage, et « console » et/ou « audit file » pour la
 * notification. Le core ne voit jamais ce choix.
 *
 * <p>═══════════════════════════════════════════════════════════
 * EXERCICE 4 — utilise {@code withFileStorage} pour câbler
 * {@link FileAccountRepository} (le helper est déjà écrit,
 * il ne manque que l'implémentation de l'exo 3 pour qu'il marche).
 * ═════════════════════════════════════════════════════════════
 */
public final class AppBuilder {

    /** Un core câblé avec stockage EN MÉMOIRE et notification CONSOLE. */
    public static BankOperations withMemoryStorage() {
        // (Imports à ajouter ici : hexa.adapters.memory.InMemoryAccountRepository,
        //  hexa.adapters.console.ConsoleNotifier)
        //
        //   InMemoryAccountRepository repository = new InMemoryAccountRepository();
        //   AccountService service = new AccountService(repository,
        //           List.of(new ConsoleNotifier(System.out)));
        //   // Le test compte 10 € initiaux :
        //   //   repository.save(new Account("A1", "Alice", 100_00));
        //   return service;
        throw new UnsupportedOperationException("TODO EXO 4 : withMemoryStorage");
    }

    /** Un core câblé avec stockage dans UN FICHIER et notifications CONSOLE + AUDIT. */
    public static BankOperations withFileStorage(java.nio.file.Path accountsFile, java.nio.file.Path auditFile) {
        // TODO (EXO 4) — même câblage que withMemoryStorage, mais :
        //   - repository = new FileAccountRepository(accountsFile)
        //   - notifiers  = List.of(new ConsoleNotifier(System.out), new AuditFileNotifier(auditFile))
        //   - compte de test : repository.save(new Account("A1", "Alice", 100_00));
        throw new UnsupportedOperationException("TODO EXO 4 : withFileStorage");
    }
}
