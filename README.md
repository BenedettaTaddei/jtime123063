# JTime

A Java application for managing tasks and projects, tracking estimated versus actual time and analysing productivity through reports. Developed as a university project for the *Metodologie di Programmazione* (Programming Methodologies) course at the University of Camerino.

## Features

- Create, edit and delete **tasks**, either standalone or grouped into **projects**
- Track **estimated time** and **actual time** for each activity, with due dates and completion status
- Generate **reports** to analyse performance (e.g. time variance)
- Persistent data: the application state is saved between sessions
- Cascade deletion: removing a project also removes its tasks, keeping data consistent

## Architecture

The application follows a layered design, with each component having a single responsibility:

- **Model**: `Project`, `Task` and their implementations, representing the domain state
- **Repository / Persistence**: repository interfaces mediate access to the data; an XML parser converts objects to and from the storage file
- **Managers**: `ProjectManager`, `TaskManager` and `ReportManager` hold the business rules, while `JTimeManager` coordinates them and keeps tasks and projects consistent after loading
- **Controller / View**: `MainView`, `TasksView`, `ProjectsView` and `ReportsView` handle user interaction and delegate the logic to the managers

Data is stored in a single XML file with two sections (`tasks_store` and `projects_store`).

## Extensibility

- **Different storage:** persistence goes through interfaces, so the XML file can be replaced by a database just by adding new repository implementations
- **New features:** functions such as notifications or data export can be added as dedicated managers
- **Reusable logic:** the business logic is separate from the graphical components

## Tech stack

Java 21, JavaFX, Gradle, XML

## Getting started

Requirements: JDK 21 (Gradle is included through the wrapper).

```bash
git clone https://github.com/BenedettaTaddei/jtime123063.git
cd jtime123063
./gradlew run
```

On Windows, use `gradlew.bat run`.

## Author

Benedetta Taddei
