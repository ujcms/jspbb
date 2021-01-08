package com.jspbb.util.sensitive;

public class Hit {
    private int begin;
    private int end;
    private String value;


    public Hit() {
    }

    public Hit(int begin, int end, String value) {
        this.begin = begin;
        this.end = end;
        this.value = value;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
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

    @Override
    public String toString() {
        return "[" + begin + "," + end + "]'" + value + "'";
    }
}
