# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

A university course repository (`Program Design & Construction`) containing three independent **NetBeans Java SE projects**. There is no root build, no root manifest, and no CI — each subdirectory is self-contained and built via `nbproject/build-impl.xml` (Ant) or plain `javac`.

| Project | Role |
|---|---|
| `Assignment1/` | The active assignment: a console "calendar pet" (CUI). All code authored by `aria`. |
| `PDC_Tutorials/` | Lecture tutorial exercises: one package per task, each with its own `main`. Mostly lecture-author code, many with deliberate unfinished stubs. |
| `Project2/` | Empty NetBeans scaffold — `Project2.main` is a TODO stub. |

Git remote is `https://github.com/ariaDalgleish/ProgramDesignConstruction.git` (branch `main`).

## Build & Run Commands

Ant is **not** installed on this machine; NetBeans is the intended build tool. The commands below are `javac`/`java` equivalents and are verified to work.

### Assignment1 (Java 25)

```bash
cd Assignment1
javac -d build/classes $(find src -name '*.java')
java -cp build/classes assignment1.Main
```

`main.class` is preconfigured as `assignment1.Main` in `nbproject/project.properties`.

The app reads `calendar.txt` from the **current working directory** (i.e. `Assignment1/calendar.txt`). No such file is committed, so on a fresh run the pet starts unconfigured and prompts for a calendar URL instead. Calendar file format is one event per line:

```
2026-10-26,09:30,Team standup,Room 4B
```

Fields are `date,time,title,location`; `location` is optional. Parsing lives in `AbstractCalendarSource.parseAndStore` — a malformed line is reported as a warning and skipped, but the rest of the file still loads.

### PDC_Tutorials (Java 8 source level)

```bash
cd PDC_Tutorials
javac -cp "lib/derby.jar;lib/derbyclient.jar" -d build/classes $(find src -name '*.java')
java -cp "build/classes;lib/derby.jar;lib/derbyclient.jar" Task04_1.MathQuiz1
```

Key points:

- **Run from the `PDC_Tutorials/` directory.** Resources are opened via relative paths like `"./resources/T11_ml_question.txt"`, not via the classpath. Running from any other directory makes resource-loading tasks fail with `IOException` (and `Task11_1` then dies with `IllegalArgumentException: bound must be positive` because the question list is empty).
- Each task is its own package with its own `main` class — there is no default entry point (`main.class=` is empty in `nbproject/project.properties`), so name the class explicitly.
- The Derby jars are **not** required to compile (the JDBC code only uses `java.sql`); they are required at runtime for the database tasks. Classpath separator is `;` (this is a Windows `javac`, even under Git Bash).
- `nbproject/project.properties` pins `javac.source=1.8` / `javac.target=1.8`. The installed JDK is 25, which still accepts `-source 8` but warns that it is obsolete; prefer `--release 8` when compiling manually.

### Tests

There are none. No `test/` directory, no test sources, and no JUnit configuration, despite `junit-4.12.jar` / `hamcrest-core-1.3.jar` sitting in `PDC_Tutorials/lib/`. `ant test` has nothing to run. Do not assume a failing or passing test suite exists.

## Assignment1 Architecture

A layered console application. The layering is the point of the assignment — the Javadoc on each class names the principle it demonstrates (encapsulation, inheritance, interface/DI, Single Responsibility).

```
Main
 └── Pet                (state machine + dialogue; all I/O via ConsoleView)
      ├── ConsoleView   (owns the Scanner and console output)
      └── CalendarSource (interface)
           └── AbstractCalendarSource (implements CalendarSource; parsing + storage)
                └── LocalFileCalendarSource (implements CalendarSource; supplies raw lines)
```

**`Pet`** is a state machine over `PetState` (`SLEEPING`, `AWAKE`, `ACTIVE`). `run()` is a switch dispatching to `sleepingState()` / `awakeState()`; both return `false` to terminate the program. `enterActive()` is the single place that resets the idle timer — it sets `ACTIVE`, stamps `lastInteractionTime`, and returns to `AWAKE` (the state change is deliberately contained rather than dispatched). `goToSleep()` resets `currentDate` back to today so a later wake always starts from the current day.

**Idle timeout** is implemented as a polling loop in `ConsoleView.readLineWithTimeout(pollMillis, BooleanSupplier timedOut)`: it checks `System.in.available()` and sleeps in short increments instead of calling the blocking `scanner.nextLine()`, returning `null` when the supplier reports a timeout. `Pet.idleTimedOut()` supplies that predicate (15s limit, 500ms poll).

**`CalendarSource`** is the interface the `Pet` depends on, so a URL-backed source can be added without touching `Pet`. `AbstractCalendarSource` holds the `Map<LocalDate, List<CalendarEvent>>` and all parsing; subclasses only fetch raw lines and call `loadLines(...)`. `LocalFileCalendarSource` reads from disk and implements `importFromUrl` with `java.net.http.HttpClient`, writing the response back to the local file and re-loading through the inherited `loadLines`. The URL path expects plain text in the same `date,time,title,location` format — it is not a real `.ics` parser.

**`CalendarEvent`** is immutable (`final` fields, no setters), sorts by time via `Comparable`, and its constructor rejects null `date`/`time`/`title`.

## PDC_Tutorials Architecture

`src/TaskNN_M/<Class>.java` — 17 task packages, nearly all independent. Each is a small console program; a few add Swing. The progression teaches, roughly: classes and getters/setters (01), debugging (02), reference vs. value semantics and static analysis (03), input validation (04), constructors (05), Swing/AWT and image loading (06), JDBC and database design (08), collections, generics, varargs and file I/O (09), array arguments and defensive copies (10), and refactoring with `equals`/`hashCode` (11).

Notable pairs and deliberate details:

- **Task08_2 vs Task08_3** — the same exercise in two stages. `Task08_2` is the *skeleton*: `DBManager` has empty `USER_NAME`/`PASSWORD`/`URL` and `establishConnection()` is blank; `BookStore.connectBookStoreDB()`, `createPromotionTable()` and `getWeekSpecial()` are empty but carry the exact SQL statements in comments as hints. `Task08_3` is the *worked solution*: `SimpleDBManager` connects to embedded Derby at `jdbc:derby:BookStoreDB_Ebd; create=true` with `pdc`/`pdc`.
- **Task09_3** (`MathQuiz`) is a working Swing + Derby app: creates a `UserInfo` table, logs players in, and persists their score. Uses `jdbc:derby:PlayerDB;create=true`, `pdc`/`pdc`.
- **Task10_1** and **Task10_2** contain the same `ArrayTest` class, identical apart from the `package` declaration — the pair exists so students can compare and refactor, not because the code differs.
- **Task08_1** (`NoProgramming`) contains no database code at all — it just `System.out.println`s the CREATE/INSERT/SELECT statements for the student to run by hand against a running Derby server.
- **Task06_2** is the only cross-task import (`import Task06_1.*`); it reuses `Task06_1.BGPanel`'s image on its own `JPanel`.
- **Task01_1** `Students` is a plain bean; **Task01_2** `Shape` is an abstract class with an abstract `calculateArea()`, but the concrete `Circle`/`Rectangle`/`Square` subclasses that once accompanied it are no longer in `src/`.
- **Task02_1** `DebugProgram` is an exercise in *finding* two deliberate bugs (`i <= dogArray.length` overruns the array; one `Dogs` is constructed with a `null` name).

**Exercise stubs are intentional.** `Task09_1/SumAll` has three `//add your code here` placeholders, and the `Task08_2` skeleton methods are empty on purpose. Do not fill these in unless the user asks for that specific task — they are the graded material. The blank DB credentials in `Task08_2/DBManager` are part of that skeleton and are meant to stay blank.

## Resources

`PDC_Tutorials/resources/` holds `T02_input.txt`, `T02_scores.txt`, `T04_users.txt`, `T06_bg.jpg`, `T11_ml_question.txt`. Only three are referenced by the current sources: `T06_bg.jpg` (Tasks 06_1/06_2, via `ImageIcon`), `T11_ml_question.txt` (Task11_1, pipe-delimited `question|options|answer|explanation`), and `T09_log.txt` (written by `Task09_2/Log`, append mode). `T02_input.txt`, `T02_scores.txt` and `T04_users.txt` are currently orphaned — leftovers from an older layout where each task folder held its own resources.

## Gotchas

- **`PDC_Tutorials/build/classes/` is stale and untrustworthy.** It contains `.class` files (and copied `.txt` resources) from a superseded task layout — `Task02_2`, `Task02_3`, `Task02_4`, `Task01_2/Circle`, `ShapeCalculator1/2` — whose sources no longer exist. `git status` therefore reports a large block of deletions in `build/`. Compile from `src/`, never from what's already in `build/classes/`.
- **Java levels differ between the two real projects**: `Assignment1` targets Java 25 (uses `switch` expressions and arrow cases), `PDC_Tutorials` targets Java 8. Don't apply Java 25 syntax to the tutorial project.
- **Assignment1 mutates `System.out` directly in `AbstractCalendarSource.loadLines` and `LocalFileCalendarSource` for warnings**, even though the stated design is that all output goes through `ConsoleView`. Match the surrounding style rather than refactoring this unless asked.
- `Project2/` is an unused scaffold; `Assignment1` is the live work.
