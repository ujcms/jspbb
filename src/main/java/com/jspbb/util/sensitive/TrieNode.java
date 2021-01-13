package com.jspbb.util.sensitive;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private boolean end = false;

    private boolean alone = false;

    /**
     * 用于拆字表，标明属于哪个字拆出来的
     */
    private Character splitChar = null;

    private Map<Character, TrieNode> values = new HashMap<>();

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isAlone() {
        return alone;
    }

    public void setAlone(boolean alone) {
        this.alone = alone;
    }

    public Character getSplitChar() {
        return splitChar;
    }

    public void setSplitChar(Character splitChar) {
        this.splitChar = splitChar;
    }

    public Map<Character, TrieNode> getValues() {
        return values;
    }

    public void setValues(Map<Character, TrieNode> values) {
        this.values = values;
    }
}
