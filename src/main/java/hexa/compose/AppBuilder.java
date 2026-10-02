package hexa.compose;

import hexa.adapters.console.ConsoleNotifier;
import hexa.adapters.file.AuditFileNotifier;
import hexa.adapters.file.FileAccountRepository;
import hexa.adapters.memory.InMemoryAccountRepository;
import hexa.application.AccountService;
import hexa.ports.in.BankOperations;

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

    /**
     * Un core câblé avec stockage EN MÉMOIRE et notification CONSOLE.
     */
    public static BankOperations withMemoryStorage() {
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        AccountService service = new AccountService(repository,
                List.of(new ConsoleNotifier(System.out)));
        return service;
    }

    /**
     * Un core câblé avec stockage dans UN FICHIER et notifications CONSOLE + AUDIT.
     */
    public static BankOperations withFileStorage(java.nio.file.Path accountsFile, java.nio.file.Path auditFile) {
        FileAccountRepository repository = new FileAccountRepository(accountsFile);
        AccountService service = new AccountService(repository,
                List.of(
                        new ConsoleNotifier(System.out),
                        new AuditFileNotifier(auditFile)
                ));
        return service;
    }
}
