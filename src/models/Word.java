package models;

public class Word {
    private int id;
    private String word;
    private String category;   // roman_urdu | english | mixed
    private int frequency;

    public Word() {}

    public Word(int id, String word, String category, int frequency) {
        this.id        = id;
        this.word      = word;
        this.category  = category;
        this.frequency = frequency;
    }

    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }
    public String getWord()         { return word; }
    public void   setWord(String w) { this.word = w; }
    public String getCategory()     { return category; }
    public void   setCategory(String c) { this.category = c; }
    public int    getFrequency()    { return frequency; }
    public void   setFrequency(int f)   { this.frequency = f; }

    @Override public String toString() { return word; }
}
