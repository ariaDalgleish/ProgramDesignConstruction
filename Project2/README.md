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

## Running it

In NetBeans: open the `Project2` project and press **Run**.

Or from a terminal, with the project folder as the working folder (the
database folder is made next to the program, so it has to be run from here):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
& "C:\Users\aria\Downloads\netbeans-28-bin\netbeans\extide\ant\bin\ant.bat" -f "C:\ProgramDesignConstruction\Project2\build.xml" compile
& "$env:JAVA_HOME\bin\java" -cp "build\classes;lib\derby.jar" project2.Project2
```

The **Add event** button and the **Back**, **Today** and **Next** buttons
work now. The pet itself is still only a picture — it does not sleep and it
does not wake up yet.
