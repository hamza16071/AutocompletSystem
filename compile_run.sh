#!/bin/bash
echo "============================================"
echo "  Roman Urdu Autocomplete - Build & Run"
echo "============================================"

mkdir -p out

echo ""
echo "[1/2] Compiling..."
javac -cp "lib/sqlite-jdbc-3.53.1.0.jar" -d out \
    src/models/Word.java \
    src/trie/TrieNode.java \
    src/trie/Trie.java \
    src/algorithms/RomanUrduNormalizer.java \
    src/algorithms/RankingEngine.java \
    src/database/DBConnection.java \
    src/database/WordRepository.java \
    src/services/AutocompleteService.java \
    src/utils/CSVLoader.java \
    src/ui/MainFrame.java \
    src/app/Main.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi

echo "[Compile] SUCCESS"
echo ""
echo "[2/2] Running..."
java -cp "out:lib/sqlite-jdbc-3.53.1.0.jar" app.Main
