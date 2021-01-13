package com.jspbb.util.sensitive;

public class Hit {
    private int begin;
    private int end;
    private String value;
    private String word;

    public Hit() {
    }

    public Hit(int begin, int end, String value, String word) {
        this.begin = begin;
        this.end = end;
        this.value = value;
        this.word = word;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getLength() {
        return this.end - this.begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "[" + begin + "," + end + "]'" + value + "':'" + word + "'";
    }
}
