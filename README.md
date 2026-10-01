# Hexagonal Architecture Lab

Un projet **Java 11+** sans aucune dépendance pour apprendre l'architecture
hexagonale (*Ports & Adapters*, Alec Horst / James Coplien / Neil Ford)
**en la faisant**.

Le domaine : une micro-banque — des comptes qu'on crédite, débite, consulte.
Petite surface, tous les ingrédients : objets du domaine, service de cas
d'usage, ports d'entrée/sortie, adaptateurs interchangeables, tests avec fakes.

---

## 1. La figure à retenir

```
                    ┌─────────────────────────┐
                    │      ADAPTATEURS         │
   (CLI, REST,     │   D'ENTRÉE / DE SORTIE   │
    batch, test)   │  mémoire · fichier       │
        │          │  console · audit · JPA   │
        ▼          └─────────────┬───────────┘
┌───────────────┐                │
│  ADAPTATEURS  │        ┌───────▼────────┐
│   D'ENTRÉE    │◄──────►│       CORE     │
│ (CLI runner)  │  ports │  domain        │
└───────┬───────┘ in/out │  application   │
        │                └────────────────┘
        ▼
   le monde : clavier, fichiers, console
```

- **Core** (centre) : `hexa.domain` (règles métier) + `hexa.application`
  (cas d'usage). **Ne dépend d'aucune technologie.**
- **Ports** (`hexa.ports.in` / `hexa.ports.out`) : des *interfaces*
  qui décrivent les capacités — le contrat entre le core et le monde.
- **Adaptateurs** (`hexa.adapters.*`) : implémentations concrètes
  (fichier, console, mémoire). **Interchangeables sans toucher au core.**
- **Racine de composition** (`hexa.compose`) : le SEUL endroit qui
  fait le câblage entre classes concrètes et core.

Les règles à vérifier dans ce code :

1. Les dépendances pointent **toujours vers le centre**
   (`adapters → ports → core`, jamais l'inverse).
2. Le core ne connaît que des **interfaces** (les ports), jamais
   d'implémentation.
3. Changer le stockage (mémoire → fichier) ou ajouter un notifier
   ne modifie **aucune ligne** de `hexa.domain` / `hexa.application`.

---

## 2. La marche en avant (dans l'ordre)

Chaque étape se valide par les tests (et l'app CLI). Ne regarde pas
les solutions en commentaire avant d'avoir tenté l'exercice.

| Étape | Fichier à compléter | Ce que tu apprends |
|---|---|---|
| **EXO 1** | `hexa/application/AccountService.java` | Le cas d'usage orchestre les ports : charger → muter le domaine → sauvegarder → notifier. Le core ne dépend que d'interfaces. |
| **EXO 2** | `hexa/adapters/memory/InMemoryAccountRepository.java` | L'adaptateur de sortie. Le piège des objets mutables : renvoyer des *snapshots*, pas les instances stockées. |
| **EXO 3** | `hexa/adapters/file/FileAccountRepository.java` | Un DEUXIÈME adaptateur pour le même port. Le format fichier est un détail local — le core n'en sait rien. |
| **EXO 4** | `hexa/compose/AppBuilder.java` | La racine de composition : c'est ICI, et seulement ici, qu'on choisit mémoire ou fichier. |
| **EXO 5** | `hexa/adapters/file/AuditFileNotifier.java` | Deux adaptateurs sur un même port, côte à côte (console + audit). Le core est agnostique du nombre d'implémentations. |

### Bonus (quand les 5 exos passent)

- **EXO 6** — Écris un `RestBankOperations` : même port d'entrée
  (`BankOperations`), mais qui « parle » JSON/HTTP au lieu du texte.
  Le core reste intact.
- **EXO 7** — Écris un `InMemoryNotifier` pour l'audit et ajoute un
  9ᵉ test dans `ExerciseTests` qui vérifie que les DEUX notifiers
  sont appelés.
- **EXO 8** — Retire `InsufficientFundsException` du `domain` et
  remplace-la par un code de retour (`Result` pattern). Où le code
  change, et où il ne change PAS ?

---

## 3. Compile & teste

Prérequis : un JDK (11 ou plus récent). Aucun outil de build requis.

```powershell
# depuis la racine du projet
./build.ps1          # compile tout et lance les tests
```

Ou à la main (PowerShell) :

```powershell
New-Item -ItemType Directory -Force out | Out-Null
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } |
    Set-Content -Path sources.txt -Encoding utf8
javac -encoding UTF-8 -d out @sources.txt
java -cp out hexa.test.TestRunner     # les tests
java -cp out hexa.compose.CliRunner memory   # l'app (stockage mémoire)
java -cp out hexa.compose.CliRunner file     # l'app (stockage fichier)
```

Sous IntelliJ : ouvre le dossier `hexagonal-architecture-lab`,
lance `TestRunner` (tests) puis `CliRunner` avec le paramètre `memory` / `file`.

### Le déroulé d'un run

1. Avant les exos : les tests échouent avec `UnsupportedOperationException:
   TODO EXO …` — c'est le signal que tu sais quel fichier ouvrir.
2. EXO 1 + 2 faits : les 5 tests du core et de la mémoire passent.
3. EXO 3 + 5 faits : les 8 tests passent.
4. EXO 4 fait : `CliRunner file` marche, et après un second run le solde
   est encore là (vérifie `data/accounts.txt` et `data/audit.log`).

---

## 4. Le vocabulaire (pour le restant de ta carrière)

| Terme | Dans ce projet |
|---|---|
| **Domain / entities** | `Account`, `Transaction` |
| **Use-case service** | `AccountService` |
| **Driving port** (entrée) | `BankOperations` — implémenté PAR l'adaptateur d'entrée |
| **Driven port** (sortie) | `AccountRepository`, `TransactionNotifier` — implémentés PAR les adaptateurs de sortie |
| **Driver / adapter in** | `CliRunner` (et demain un controller REST) |
| **Driven / adapter out** | `InMemoryAccountRepository`, `FileAccountRepository`, `ConsoleNotifier`, `AuditFileNotifier` |
| **Composition root** | `AppBuilder` + `main()` |
| **Test double / fake** | `FakeAccountRepository`, `RecordingNotifier` |

Note la subtilité de direction :
- pour un port d'ENTRÉE, l'adaptateur implémente l'interface (le monde
  « pousse » le core) ;
- pour un port de SORTIE, le core *déclare* l'interface et l'injecte en
  construction (le core « tire » sur le monde).

C'est cette inversion sur les ports de sortie qui rend le core
testable sans base de données et interchangeable sans réécriture.
