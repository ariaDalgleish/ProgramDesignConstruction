# Pet Calendar — Project 2

The GUI version of the Project 1 "calendar pet". A pet sits next to the
calendar and the events it shows will be stored in an embedded Apache Derby
database.

This README is written stage by stage, in the same order as the lectures,
so it doubles as a record of what was done in each one.

## What has been done so far

| Stage | Lecture | Result |
|---|---|---|
| 1 | 6 — GUI | The window opens with the right layout |
| 2 | 7 — Version control | `main` is tagged `v1.0.0` and the GUI is built on the `gui` branch |
| 3 | 8 — JDBC | The events are kept in an embedded Derby database |
| 4 | 9 — MVC and patterns | The window, the pet and the events are three separate parts |
| 5 | 10 — V&V and JUnit | 59 unit tests and a manual test log |

## Stage 1: the window (Lecture 6)

The window is `project2.view.PetView`, which extends `JFrame`:

- **Top** — a `GridLayout` panel with the pet's speech and the date.
- **Middle** — the pet's picture (`PetPicturePanel`, drawn with
  `paintComponent`) on the left and the events of the day in a `JTextArea`
  inside a `JScrollPane` on the right.
- **Bottom** — a `FlowLayout` panel with the **Wake**, **Back**, **Today**
  and **Next** buttons. The window's close button replaces the old `quit`
  command, because the frame uses `EXIT_ON_CLOSE`.

The buttons are not connected to anything yet — they only show a message.
The logic is wired up in the later stages.

## Stage 2: version control (Lecture 7)

The project is tracked with Git and GitHub.

| What | Where |
|---|---|
| The working console pet (Project 1) | tag `v1.0.0` on `main` |
| The GUI work | the `gui` branch |
| The finished GUI | `gui` is merged into `main` and tagged `v2.0.0` |

`main` is only changed once the GUI works, so the console version is always
safe to roll back to with `git checkout v1.0.0`.

## Stage 3: the database (Lecture 8)

The events are now kept in an embedded Apache Derby database instead of in
the text file the console version used.

| Class | What it does |
|---|---|
| `model.Event` | One event — a day, a time, a title and a place |
| `data.Database` | Opens the one and only connection (the Singleton) |
| `data.Schema` | Creates the `EVENT` table and adds a few example events |
| `data.EventDao` | The interface the rest of the program uses |
| `data.JdbcEventDao` | The implementation that speaks SQL |
| `data.DataAccessException` | Thrown when the database cannot be read or written |

- The database is `PetCalendarDB`, it lives in the `db` folder next to the
  project and the user name and password are `pdc` / `pdc`.
- The `db` folder is made by the program itself, so there is nothing to set
  up by hand. Derby is told where to keep its files before it starts up.
- Derby has no `CREATE TABLE IF NOT EXISTS`, so `Schema` asks the database
  whether the table is already there before it makes it.
- The first five events are only added when the table is empty, so the
  events that have been saved are never in the way.
- The window reads the events of the day out of the database every time it
  shows a day, so a new event shows up straight away.
- The **Add event** button opens a small window with a `JTextField` for the
  date, the time, the title and the location. This is the one thing the GUI
  can do that the console version could not.
- No SQL is written in the window. The window only knows the `EventDao`
  interface, and the SQL lives in `JdbcEventDao`.
- If the database cannot be opened the program says so in a message instead
  of stopping without a word.

This version still does too much. The window changes the day itself and it
talks to the database, and there is nothing of the console pet yet. Both of
those are put right in the next stage.

## Stage 4: MVC and the patterns (Lecture 9)

The window is split into three parts that do not do each other's jobs.

| Package | What is in it |
|---|---|
| `project2.model` | `Event`, `Pet`, `PetState`, `PetListener` — the data and the pet |
| `project2.service` | `CalendarService`, `CalendarListener`, `ValidationException` — the rules of the calendar |
| `project2.view` | `PetView`, `PetPicturePanel`, `AddEventDialog` — the window |
| `project2.controller` | `PetCalendarController` — the clicks on the buttons |
| `project2.data` | `EventDao`, `JdbcEventDao`, `Database`, `Schema`, `EventDaoFactory` — the database |

- The **window knows nothing** about the database and nothing about the
  rules of the pet. It shows what it is told to show and hands the clicks to
  the controller.
- The **controller** is the only class that knows about the window, the pet
  and the service. It turns a click into a change and a change into an
  update of the window.
- The **service** holds the rules of the calendar. It checks an event before
  it is saved and tells everybody who is interested when the events change.

The patterns that are used, and where:

| Pattern | Where |
|---|---|
| Model-View-Controller | the `model`, `view` and `controller` packages |
| DAO | `EventDao` (the interface) and `JdbcEventDao` (the implementation) |
| Singleton | `Database` — Derby allows only one connection to a database |
| Factory | `EventDaoFactory` — the only place that says which DAO is used |
| Observer | `Pet` → `PetListener` and `CalendarService` → `CalendarListener` |

### The pet

The pet is the same idea as the console pet in Assignment 1. It has three
states — `SLEEPING`, `AWAKE` and `ACTIVE` — and it keeps the day that is
being looked at.

- The pet starts asleep. The **Wake** button and clicking the picture wake
  it up.
- While the pet is asleep the calendar does not move — the screen says
  "Wake the pet up first."
- The pet goes back to sleep after a minute of not being used. The day goes
  back to today as well, so waking it up later always starts from the
  current day.
- When the pet changes state it tells its listeners, and the controller
  shows the new state in the window (the Observer pattern).

## Stage 5: tests (Lectures 10A and 10B)

The model has no user interface any more, so it can be tested on its own.

| Test class | What it checks | Tests |
|---|---|---|
| `model.EventTest` | The events cannot be changed, the sorting is right | 17 |
| `model.PetTest` | The states of the pet, the idle timer, the days | 24 |
| `service.CalendarServiceTest` | The rules of an event and the telling of the listeners | 12 |
| `data.JdbcEventDaoTest` | The events really go into the database and come back | 6 |

59 tests, all passing.

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\Users\aria\Downloads\netbeans-28-bin\netbeans\extide\ant\bin\ant.bat" -f "C:\ProgramDesignConstruction\Project2\build.xml" test
```

In NetBeans: right-click the project and choose **Run Tests**.

Two things were needed to make the tests work:

- `service.InMemoryEventDao` is a stand-in for the database that keeps the
  events in a list in the memory of the computer. The rules of the calendar
  can then be tested without a database at all.
- `data.JdbcEventDaoTest` uses a database that lives in the memory of the
  computer too, so the real SQL is tested but nothing is left behind. The
  table is emptied before every test, because Derby keeps an in-memory
  database after the connection has been closed.

### The manual test log

The window was walked through with the buttons clicked and the screen read
back after every step. The verdicts are **pass**, **fail** and **error**,
and the problems are put in the categories of the lectures: a *fault* is a
mistake in the program, an *error* is a mistake in the test.

| # | What was done | What was expected | What happened | Verdict |
|---|---|---|---|---|
| 1 | The program was started | The window opens with today's date and today's events | `MON, 26 OCT` and the three events of today | pass |
| 2 | The window was looked at | The pet is asleep and says so | "zzz... I am asleep. Click me to wake me up." | pass |
| 3 | **Next** was pressed while the pet was asleep | The day does not move and the user is told why | "Wake the pet up first." and the day stayed on today | pass |
| 4 | **Wake** was pressed | The pet wakes up | "Mmmrm..." What is on today? | pass |
| 5 | The picture was clicked | The pet wakes up and the idle timer starts again | the pet woke up | pass |
| 6 | **Next** was pressed | Tomorrow is shown with the events of tomorrow | `TUE, 27 OCT` with *Lecture* and *Football practice* | pass |
| 7 | **Back** was pressed | The day before is shown | `MON, 26 OCT` | pass |
| 8 | **Today** was pressed | Today is shown | `MON, 26 OCT` | pass |
| 9 | Three days forward were pressed | A day with nothing on it is shown clearly | "Oooo, there's um... nothing to do on this day!" | pass |
| 10 | **Add event** was pressed, the boxes were filled in and **Add** was pressed | The event is saved and the window closes | the window opened, the event was saved (5 events became 6) and the window closed | pass |
| 11 | **Add event** was pressed and **Add** was pressed with no title | The window stays open and says what is wrong | "Please give the event a title." | pass |
| 12 | **Add event** was pressed and a date like "26/10/2026" was typed in | The window stays open and says how the date must look | "The date must look like yyyy-MM-dd and the time like HH:mm." | pass |
| 13 | The window was used and then left alone | The window keeps answering and never freezes | the Swing thread answered within two seconds | pass |
| 14 | The pet was left alone for a minute | The pet falls asleep and the day goes back to today | the pet went back to sleep and the day went back to today | pass |

### The problems that were found

| # | What was found | Category | What was done |
|---|---|---|---|
| 1 | The rule "the calendar does not move while the pet is asleep" was written in the controller, so it could not be tested without a window | design fault | The rule was moved into the model as `next()`, `back()` and `reset()`, which say whether they did anything. Six unit tests now cover it. |
| 2 | The events of one test were still there in the next test, so the test for "a day with nothing on it" failed when the tests were run twice | error in the test | The table is emptied at the start of every test now. The program was not at fault. |

One thing was noticed but left as it is: the buttons are not greyed out while
the pet is asleep, the window says "Wake the pet up first." instead. That is
what the console version does as well, so it is not a fault.

## Running it

In NetBeans: open the `Project2` project and press **Run**.

Or from a terminal, with the project folder as the working folder (the
database folder is made next to the program, so it has to be run from here):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\Users\aria\Downloads\netbeans-28-bin\netbeans\extide\ant\bin\ant.bat" -f "C:\ProgramDesignConstruction\Project2\build.xml" compile
& "$env:JAVA_HOME\bin\java" -cp "build\classes;lib\derby.jar" project2.Project2
```

The pet sleeps, it wakes up when the **Wake** button is pressed or when it
is clicked, the **Back**, **Today** and **Next** buttons move through the
days, and the **Add event** button saves a new event in the database.
