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

## Running it

In NetBeans: open the `Project2` project and press **Run**. The window is
the only thing that happens at the moment — the pet does not do anything yet.
