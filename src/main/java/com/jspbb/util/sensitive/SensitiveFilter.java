package com.jspbb.util.sensitive;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 支持全半角、大小写、停止词、跳词。
 *
 * @author PONY
 */
public class SensitiveFilter {
    /**
     * 敏感词字典树
     */
    private Map<Character, TrieNode> sensitiveWordMap = new HashMap<>();
    /**
     * 替换字符串
     */
    private String replacement = "***";

    public SensitiveFilter() {
    }

    public SensitiveFilter(Collection<String> sensitiveWords) {
        setSensitiveWords(sensitiveWords);
    }

    public void setSensitiveWords(Collection<String> sensitiveWords) {
        if (sensitiveWords == null || sensitiveWords.isEmpty()) return;
        Map<Character, TrieNode> tempWordMap = new HashMap<>((int) (sensitiveWords.size() * 1.3));
        for (String word : sensitiveWords) {
            addSensitiveWord(word, tempWordMap);
        }
        this.sensitiveWordMap = tempWordMap;
    }

    public void addSensitiveWord(String word, Map<Character, TrieNode> wordMap) {
        // 敏感词长度必须 2 位以上。敏感词允许带前后空格，过滤英文的时候有时需要空格，否则容易误杀。比如：have av
        if (word == null || word.trim().length() < 2) return;
        Map<Character, TrieNode> currMap = wordMap;
        for (int i = 0, len = word.length(); i < len; i += 1) {
            char c = word.charAt(i);
            TrieNode node = currMap.get(word.charAt(i));
            if (node == null) {
                node = new TrieNode();
                currMap.put(c, node);
            }
            // 敏感词结束，加上结束标志
            if (i == len - 1) node.setEnd(true);
            currMap = node.getValues();
        }
    }

    public boolean matches(String text) {
        return finds(text, false).size() > 0;
    }

    public String filter(String text) {
        List<Hit> hits = finds(text, true);
        if (hits.size() <= 0) return text;
        StringBuilder result = new StringBuilder(text.length());
        int begin = 0;
        for (Hit hit : hits) {
            result.append(text, begin, hit.getBegin()).append(this.replacement);
            begin = hit.getEnd();
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    public List<Hit> finds(String text, boolean maxMatch) {
        if (StringUtils.isBlank(text)) return Collections.emptyList();
        List<Hit> hits = new ArrayList<>();
        for (int i = 0, len = text.length(); i < len; i += 1) {
            int sensitiveLength = find(text, i, maxMatch);
            if (sensitiveLength > 0) {
                hits.add(new Hit(i, i + sensitiveLength, text.substring(i, i + sensitiveLength)));
                if (!maxMatch) break;
                // 循环本身会自动 +1 ，所以要 -1
                i += sensitiveLength - 1;
            }
        }
        return hits;
    }

    private int find(String text, int begin, boolean maxMatch) {
        Map<Character, TrieNode> currMap = sensitiveWordMap;
        // 正在查找的敏感词长度
        int length = 0;
        // 找到的敏感词长度
        int foundLength = 0;
        for (int i = begin, len = text.length(); i < len; i += 1) {
            TrieNode node = currMap.get(text.charAt(i));
            length += 1;
            if (node == null) break;
            if (node.isEnd()) {
                foundLength = length;
                if (!maxMatch) break;
            }
            currMap = node.getValues();
        }
        return foundLength;
    }

    public Map<Character, TrieNode> getSensitiveWordMap() {
        return sensitiveWordMap;
    }

    public void setSensitiveWordMap(Map<Character, TrieNode> sensitiveWordMap) {
        if (sensitiveWordMap == null) this.sensitiveWordMap = Collections.emptyMap();
        else this.sensitiveWordMap = sensitiveWordMap;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

}
