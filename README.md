# 🚀 Roman Urdu Intelligent Autocomplete System

An AI-inspired **Roman Urdu Intelligent Autocomplete System** built using **Java, Swing, Trie Data Structure, SQLite, and JDBC**.

The system provides real-time autocomplete suggestions, multi-word phrase prediction, search history management, and intelligent auto-correction for Roman Urdu queries.

---

# 📌 Project Overview

Typing long Roman Urdu sentences can be slow and inconvenient.

This project solves that problem by providing intelligent autocomplete suggestions while the user types.

Instead of searching the complete dataset every time, the project uses the **Trie Data Structure** for fast prefix searching, making suggestions almost instantly.

The system also learns from user searches by increasing word frequency and storing search history.

---

# 🎯 Objectives

- Fast autocomplete using Trie
- Roman Urdu word suggestions
- Multi-word phrase prediction
- Search history
- Auto correction
- Frequency-based ranking
- SQLite database integration
- Modern Java Swing GUI

---

# ✨ Features

## ✅ Real-Time Autocomplete

Shows suggestions while the user types.

Example

Input

```
aap
```

Suggestions

```
aap kaise ho
aap kaha ho
aap theek ho
```

---

## ✅ Prefix Searching (Trie)

Uses Trie Data Structure for extremely fast searching.

Instead of scanning every word, Trie searches only the matching branch.

Time Complexity

```
O(length of prefix)
```

---

## ✅ Frequency Based Ranking

Frequently selected words automatically move to the top.

Example

```
hello
frequency = 120
```

appears before

```
help
frequency = 20
```

---

## ✅ Auto Correction

Incorrect spellings automatically suggest the closest valid word.

Example

```
helo
```

↓

```
Did you mean:
hello
```

---

## ✅ Multi-word Prediction

Predicts complete phrases.

Example

Input

```
aap
```

↓

Suggestions

```
aap kaise ho
aap kaha ho
aap theek ho
```

---

## ✅ Search History

Stores previously searched words.

Users can

- View history
- Search again
- Clear history

---

## ✅ SQLite Database

Stores

- Words
- Categories
- Frequencies
- Search History

---

## ✅ CSV Import

The project automatically imports Roman Urdu words from a CSV dataset into SQLite.

---

# 🛠 Technologies Used

| Technology | Purpose |
|------------|----------|
| Java | Programming Language |
| Swing | GUI |
| SQLite | Database |
| JDBC | Database Connectivity |
| Trie | Prefix Searching |
| HashMap | Fast Lookup |
| Priority Queue | Ranking |
| VS Code | Development Environment |

---

# 📂 Project Structure

```
RomanUrduAutoComplete
│
├── src
│   ├── app
│   │      Main.java
│   │
│   ├── ui
│   │      MainFrame.java
│   │
│   ├── services
│   │      AutocompleteService.java
│   │      SearchHistoryService.java
│   │
│   ├── trie
│   │      Trie.java
│   │      TrieNode.java
│   │
│   ├── algorithms
│   │      RankingEngine.java
│   │      AutoCorrector.java
│   │      MultiWordPredictor.java
│   │
│   ├── database
│   │      DBConnection.java
│   │      WordRepository.java
│   │      HistoryRepository.java
│   │
│   ├── dataset
│   │      roman_urdu_words.csv
│   │
│   └── utils
│          CSVLoader.java
│
├── lib
│      sqlite-jdbc.jar
│
└── README.md
```

---

# ⚙ Project Workflow

```
User Types

      │

      ▼

MainFrame (GUI)

      │

      ▼

AutocompleteService

      │

      ▼

Trie Search

      │

      ▼

Ranking Engine

      │

      ▼

Suggestions Displayed

      │

      ▼

User Selects Suggestion

      │

      ▼

Frequency Updated

      │

      ▼

Search History Saved
```

---

# 📊 Data Structures Used

## Trie

Used for

- Prefix searching
- Fast autocomplete

---

## HashMap

Used for

- Frequency lookup
- Fast searching

---

## Priority Queue

Used for

- Ranking suggestions
- Showing top results

---

# 💾 Database Tables

## words

| Column |
|---------|
| id |
| word |
| category |
| frequency |

---

## search_history

| Column |
|---------|
| id |
| word |
| search_time |

---

# 📈 Algorithms Used

- Trie Insertion
- Prefix Search
- Frequency Ranking
- Auto Correction
- Multi-word Prediction
- CSV Loading
- Search History Management

---

# 🚀 Installation

## Clone Repository

```bash
git clone https://github.com/YourUsername/RomanUrduAutoComplete.git
```

---

## Open in VS Code

Open the project folder.

---

## Install Extensions

- Extension Pack for Java
- Language Support for Java
- Debugger for Java

---

## Add SQLite JDBC

Download

```
sqlite-jdbc.jar
```

Place inside

```
lib/
```

---

## Run

Run

```
Main.java
```

The application will automatically

- Create database
- Create tables
- Load CSV dataset
- Build Trie
- Launch GUI

---

# 📷 Screenshots

Add screenshots here.

Example

```
images/home.png

images/suggestions.png

images/history.png
```

---

# 🔮 Future Improvements

- Voice Search
- AI-based Prediction
- User Login
- Cloud Database
- Dark Mode
- Export Search History
- Machine Learning Ranking
- Urdu Keyboard Support

---

# 👩‍💻 Developed By

**Hamza Yousafzai**  
BS Software Engineering



---

# 📜 License

This project is developed for educational purposes as a Data Structures & Algorithms semester project.
