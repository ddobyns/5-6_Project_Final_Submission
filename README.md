Project: Machine/PLC Simulator for an AutomationDirect CLICK PLC (Java + SQLite)
Student: Dan Dobyns | Course: SDC330 | Submission: Week 5 (Final)
1) Overview
I built a terminal-based simulator that models a small PLC (CLICK-style) with discrete inputs/outputs, coils, registers, timers, and counters. Users can add tags, run a scan cycle, force values, view a live watch table, and persist configuration/events to SQLite. The purpose was to demonstrate practical Object-Oriented Programming (OOP) and end-to-end CRUD with a realistic industrial theme.

2) Architecture & Key Classes
High-level flow
App → ConsoleUI → PlcSimulator → ScanEngine → Plc (Modules → Tags) → (Repositories → SQLite)
Core
    • App – entry point; launches ConsoleUI.
    • ConsoleUI – shows menus, renders the watch table, collects input, prints confirmations and logs.
    • PlcSimulator – orchestrates scan start/stop/step, forcing, and session logging.
    • ScanEngine – one scan cycle (read → evaluate → write → log), tracks min/max/avg cycle time.
    • Plc – composition root that holds modules and a tag directory.
OOP types
    • DeviceModule (interface) – contract for any I/O/register/timer/counter module.
    • Tag (abstract) – base for all tags; common fields (id, name, type, address, description) and API (evaluate(), valueAsString(), toString()).
    • Concrete tags (polymorphic) – DigitalInputTag, DigitalOutputTag, CoilTag, RegisterTag, TimerTag, CounterTag. Timer/Counter include presets, accumulators, and “done” bits.
Menu highlights (terminal I/O)
    • Add DI/DO/Register/Timer/Counter, List/Watch, Force value, Step/Run scan.
    • DB ops: Ensure schema, Save (upsert) tags, Load tags, Delete tag by name, Show last events.
    • Optional sample reads from plc_samples (demo data table).

3) Database & Persistence (SQLite)
    • Files:
plc_sim.db – configuration + runs + events (created by app).
plc_data.db – optional demo table plc_samples for sample reads.
    • Tables (key fields):
tags(id, name, type, address, description, value_bool, value_int, preset_ms, accum_ms, …)
events(id, run_id, timestamp_ms, level, message, tag_name, old_value, new_value)
runs(id, start_time, end_time, cycles, min_cycle_ms, max_cycle_ms, avg_cycle_ms)
(Scaffolds for scenarios/scenario_steps included for future work.)
    • Repositories:
TagRepository and EventRepository (interfaces + SQLite implementations) perform Create/Read/Update/Delete. A small migrator ensures schema (Ensure schema menu).

4) Typical Use (Run Book)
    1. Start app → press B to migrate/verify the schema.
    2. Add tags: e.g., X1 (DI), Y1 (DO), DS1 (Register), T1 (Timer), C1 (Counter).
    3. Press 6 to view the live watch table; 7 to force values (also writes events).
    4. Press S to save tags; restart; L to load tags and resume.
    5. Press E to review recent events; D to delete a tag by name when needed.

5) What Went Well
    • Clear separation of concerns. ConsoleUI handles the CLI; PlcSimulator/ScanEngine handle logic; repositories handle persistence.
    • Extensibility. The DeviceModule interface and the Tag abstract class made it straightforward to add new tag types or modules without touching UI code.
    • Persistence UX. Having explicit menu items for schema, save, load, delete, and events made validation fast and repeatable.

6) What Was Challenging (and Resolutions)
    • Package path mismatches. A few files initially sat in wrong directories (e.g., PlcDataSource under the sqlite subpackage). Fix: align folder paths to package lines and restart the Java Language Server.
    • SLF4J warning. API/binding versions didn’t match (1.7 vs 2.x). Fix: pin compatible SLF4J artifacts in pom.xml (or add slf4j-nop).
    • GitHub divergent branches. The remote had an initial README; local had history. Fix: git pull --rebase origin main --allow-unrelated-histories, resolve, then push.

7) Requirements Traceability (Design → Requirement)
    • Realistic with terminal I/O → PLC simulation theme; ConsoleUI menus + watch table; force values; scan engine.
    • Proper in-code documentation → Javadoc headers and comments in core classes and repositories.
    • Interface class → DeviceModule (and repository interfaces).
    • Abstract class → Tag is the abstract base for all tag types.
    • Composition → Plc composes modules, which compose tags.
    • Polymorphism → Calls to evaluate() / valueAsString() on Tag dispatch to the correct subclass (TimerTag, CounterTag, etc.).
    • Classes with constructors → All domain classes expose constructors for required fields; immutable or controlled setters used where appropriate.
    • Access specifiers → Fields private; public getters/setters; protected hooks in the abstract base; package-private where helpful.
    • CRUD on SQLite → TagRepository + EventRepository implement Create/Read/Update/Delete; menu options: Save (upsert), Load, Delete by name, Show events.

8) Testing, Results, & Evidence
    • Manual tests followed the run book: create → watch → force → save → restart → load → verify → delete; events confirmed in E and in DB.
    • Scan timing captured min/max/avg; end-of-run summary validates continuous and stepped scanning.
    • Error handling for DB operations surfaces concise messages and guards against nulls where practical.

9) Learning & Next Steps
Key takeaways
    • OOP patterns (interface + abstract base + composition) made the system easy to grow.
    • Keeping persistence behind repositories simplified the UI and simulator code.
    • Tight feedback via the terminal menu accelerated debugging and validation.
Future improvements
    • Scenario playback (timestamped input scripts) and alarms.
    • JSON import/export of tag sets and scenarios.
    • Debounce/edge detection settings per DI tag; richer timer modes.

