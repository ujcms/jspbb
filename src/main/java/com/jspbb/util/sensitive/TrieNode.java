package com.jspbb.util.sensitive;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private boolean end = false;

    private Map<Character, TrieNode> values = new HashMap<>();

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Map<Character, TrieNode> getValues() {
        return values;
    }

    public void setValues(Map<Character, TrieNode> values) {
        this.values = values;
    }
}
