# Minesweeper

A Minesweeper game implemented in Scala following functional programming principles.  
In addition to the core gameplay mechanics of the classic Minesweeper, the application includes advanced features such as level creation and transformation.

---

## Features

### Game Features

- New game with difficulty selection (beginner, intermediate, advanced)
- Display all available levels for a selected difficulty
- Choose a specific or random level for a selected difficulty
- Reveal a field with left click
- Add or remove a flag with right click
- Time tracking
- Click counter
- Level reset button
- Automatic reveal of adjacent empty fields
- Display the number of adjacent mines
- Game over with full mine reveal when clicking on a mine
- Hint system that suggests the safest field based on current game state
- Hint counter

---

### File Operations

- Load levels from file
- Save current game state
- Load saved game
- Save results
- Display top 5 best results for each difficulty
- Save custom levels
- Replay a sequence of moves from a file

---

### Basic Operations for Level Creation

- Change a field from mine to empty and vice versa
- Add/remove first or last row/column
- Clear a rectangular area of mines

---

### Isometries for Level Creation

- Applied to rectangular areas
- Can be:
  - **Expandable** (board expands if transformation exceeds boundaries)
  - **Non-expandable**
  - **Transparent** (existing mines are preserved when overlapping)
  - **Non-transparent**
- Support quasi-inverse operations

Available transformations:

- Rotation by 90 degrees (clockwise and counterclockwise)
- Reflection across any row, column, or diagonal
- Translation
- Central symmetry
- Creating and saving new isometries by combining existing ones

---

## Project Structure

- `src/main` – main game logic  
- `src/test` – tests (ScalaTest)

---

## Tech Stack

- Scala  
- Scala Swing  
- SBT  
- ScalaTest  

---

## Architecture

The application is structured using a controller-based architecture with a clear separation between game logic and UI.

### Controllers

The core of the application consists of two main controllers:

- **GameController** – manages the overall game state, including game flow, timing, clicks, and win/lose conditions  
- **LevelController** – handles operations related to level creation and manipulation  

---

### UI Interaction

The UI layer is completely decoupled from the game logic.  
Instead of directly accessing or modifying the state, the UI receives controller methods through constructor parameters.

For example:

- Left click → triggers a controller method for revealing a field  
- Right click → triggers a controller method for toggling a flag  

This ensures that:

- The UI does not contain business logic  
- Game logic remains testable and reusable  
- A clear separation of concerns is maintained  

---

### Functional Programming Principles

The project follows key functional programming principles:

- **Immutability** – game state is not mutated but replaced with new instances  
- **Pure functions** – core logic is implemented through functions without side effects  
- **Traits** – used for abstraction and modular design  
- **Separation of concerns** – logic and UI are strictly separated  

This approach improves code maintainability, predictability, and testability.

---

## Screenshots

<img width="240" height="280" alt="image" src="https://github.com/user-attachments/assets/7e3cc6bb-9ec4-4604-b185-a3cbd8db05de" />
<img width="240" height="280" alt="image" src="https://github.com/user-attachments/assets/3e212d51-0c61-4b58-93d0-f73e4fe19678" />
