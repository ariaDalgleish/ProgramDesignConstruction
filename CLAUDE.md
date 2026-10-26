# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

A university course repository (`Program Design & Construction`) containing three independent **NetBeans Java SE projects**. There is no root build, no root manifest, and no CI — each subdirectory is self-contained and built via `nbproject/build-impl.xml` (Ant) or plain `javac`.

| Project | Role |
|---|---|
| `Assignment1/` | Assignment 1: a console "calendar pet" (CUI). All code authored by `aria`. |
| `PDC_Tutorials/` | Lecture tutorial exercises: one package per task, each with its own `main`. Mostly lecture-author code, many with deliberate unfinished stubs. |
| `Project2/` | Assignment 2: the GUI "calendar pet" — a Swing window over an embedded Derby database. |

Git remote is `https://github.com/ariaDalgleish/ProgramDesignConstruction.git` (branch `main`). The GUI work happens on the `gui` branch and is merged into `main` when it works; `v1.0.0` is the console version and `v2.0.0` is the finished GUI.

## Build & Run Commands

Ant is **not** on the PATH. NetBeans 28 lives in `C:\Users\aria\Downloads\netbeans-28-bin\netbeans` and JDK 25 in `C:\Program Files\Java\jdk-25` — between them they are exactly the versions the brief asks for.

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

### Project2 (Java 25)

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\Users\aria\Downloads\netbeans-28-bin\netbeans\extide\ant\bin\ant.bat" -f "C:\ProgramDesignConstruction\Project2\build.xml" clean test
```

The `ant.cmd` wrapper in this NetBeans build misbehaves under Git Bash — use `ant.bat` from PowerShell, or run Ant's launcher jar directly. **Run the program from the `Project2/` directory**, because the database folder is derived from the working directory.

One test class without Ant:

```powershell
& "$env:JAVA_HOME\bin\java" -cp "build\classes;build\test\classes;lib\derby.jar;lib\junit-4.12.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore project2.model.PetTest
```

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
- **Task06_2** is the only cross-task import (`import Task06_1.*`); it reuses `Task06_1/BGPanel`'s image on its own `JPanel`.
- **Task01_1** `Students` is a plain bean; **Task01_2** `Shape` is an abstract class with an abstract `calculateArea()`, but the concrete `Circle`/`Rectangle`/`Square` subclasses that once accompanied it are no longer in `src/`.
- **Task02_1** `DebugProgram` is an exercise in *finding* two deliberate bugs (`i <= dogArray.length` overruns the array; one `Dogs` is constructed with a `null` name).

**Exercise stubs are intentional.** `Task09_1/SumAll` has three `//add your code here` placeholders, and the `Task08_2` skeleton methods are empty on purpose. Do not fill these in unless the user asks for that specific task — they are the graded material. The blank DB credentials in `Task08_2/DBManager` are part of that skeleton and are meant to stay blank.

## Project 2 Architecture

A Swing GUI over an embedded Apache Derby 10.14.2.0 database. It follows the lecture stages in `Study.txt`, and the packages keep the three layers apart:

```
Project2                    entry point - setup only (main.class=project2.Project2)
 ├── project2.model         Event, Pet, PetState, PetListener
 ├── project2.data          EventDao (iface), JdbcEventDao, Database, Schema, EventDaoFactory
 ├── project2.service       CalendarService, CalendarListener, ValidationException
 ├── project2.view          PetView, PetPicturePanel, AddEventDialog
 └── project2.controller    PetCalendarController
```

The layering is strict in both directions: the view never touches the database and never decides what is valid; the service never imports `javax.swing` and never mentions SQL; the data layer knows nothing about Swing. `PetCalendarController` is the only class that talks to both the view and the service — it wires the button clicks to the pet and refreshes the window when the pet or the events change.

**Design patterns, all required by the brief:** MVC (`model`/`view`/`controller` packages), DAO (`EventDao` interface + `JdbcEventDao`), Singleton (`Database` — embedded Derby permits one connection per database per JVM), Factory (`EventDaoFactory` — the only place that picks a concrete DAO), Observer (`CalendarService`→`CalendarListener` for event changes; `Pet`→`PetListener` for state changes).

**The database is set up with no manual steps.** `Database` sets `derby.system.home` to `<user.dir>/db` in its constructor *before* the first connection (Derby reads that property when its engine boots), then `Schema.createIfNotExists` and `Schema.seedIfEmpty` create the `EVENT` table and add five example events. Derby has no `CREATE TABLE IF NOT EXISTS`, so `Schema` checks `DatabaseMetaData.getTables` first. The database folder is gitignored.

**`DataAccessException` is a checked exception**, like `CalendarImportException` in Assignment 1. `EventDao.findByDate` / `insert` / `countAll` and `Database.getInstance()` all declare it, so every caller has to decide what to do when the database fails.

**`Event` is deliberately not `Comparable`.** Events can share a date and time, so a `compareTo` on the sort key would contradict `equals`. Order is supplied by a named `Comparator<Event> BY_DATE_AND_TIME` instead — this is the "code smells" requirement in the brief, deliberately avoided.

**`Pet` owns the rule "the calendar does not move while the pet is asleep".** `next()`, `back()` and `reset()` each return `false` and change nothing while the state is `SLEEPING`; the controller turns that `false` into the message "Wake the pet up first." `Pet.setCurrentDate` is package-private, so only the pet and its own tests can move the day directly.

**There is a JUnit 4 suite** under `Project2/test/` — 59 tests in `EventTest`, `PetTest`, `CalendarServiceTest` and `JdbcEventDaoTest`. `service.InMemoryEventDao` is a test double that keeps events in a list; the DAO test uses an in-memory Derby database and empties the table before each test, because Derby keeps an in-memory database after the connection closes.

**The style of the code is deliberately plain** — it is written to look like the hand-written code of `Assignment1` (explicit types, no streams, no `var`, no `List.of`). The Swing listeners are written with the lambda shorthand, the same way `Assignment1` uses arrow cases and a method reference; the mouse listener is the one that stays in the long form, because `MouseListener` has several methods in it. Match that style when editing Project 2 rather than introducing newer idioms.

## Resources

`PDC_Tutorials/resources/` holds `T02_input.txt`, `T02_scores.txt`, `T04_users.txt`, `T06_bg.jpg`, `T11_ml_question.txt`. Only three are referenced by the current sources: `T06_bg.jpg` (Tasks 06_1/06_2, via `ImageIcon`), `T11_ml_question.txt` (Task11_1, pipe-delimited `question|options|answer|explanation`), and `T09_log.txt` (written by `Task09_2/Log`, append mode). `T02_input.txt`, `T02_scores.txt` and `T04_users.txt` are currently orphaned — leftovers from an older layout where each task folder held its own resources.

## Gotchas

- **`PDC_Tutorials/build/classes/` is stale and untrustworthy.** It contains `.class` files (and copied `.txt` resources) from a superseded task layout — `Task02_2`, `Task02_3`, `Task02_4`, `Task01_2/Circle`, `ShapeCalculator1/2` — whose sources no longer exist. None of it is tracked by Git. Compile from `src/`, never from what's already in `build/classes/`.
- **`Assignment1/build/classes/` *is* tracked by Git** (11 `.class` files, committed before `.gitignore` existed), so recompiling Assignment 1 makes `git status` show them as modified. The other two projects' build folders are untracked. Don't commit the recompiled binaries; restore them with `git checkout -- Assignment1/build/classes` unless the user asks for the build output to be untracked.
- **Java levels differ between the two real projects**: `Assignment1` and `Project2` target Java 25 (uses `switch` expressions and arrow cases), `PDC_Tutorials` targets Java 8. Don't apply Java 25 syntax to the tutorial project.
- **Assignment1 mutates `System.out` directly in `AbstractCalendarSource.loadLines` and `LocalFileCalendarSource` for warnings**, even though the stated design is that all output goes through `ConsoleView`. Match the surrounding style rather than refactoring this unless asked.
- `Project2/db/` is the live Derby database and is gitignored — deleting the folder resets the calendar to the five example events.
- The old Project 2 work (before the stage-by-stage rebuild) is kept on the branch `backup/pre-stage-history` — look there if something is missing.
