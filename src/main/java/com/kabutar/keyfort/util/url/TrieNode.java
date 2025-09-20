package com.kabutar.keyfort.util.url;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private Map<String, TrieNode> childNodes;
    private boolean isEnd;
    private String clientId;

    public TrieNode() {
        this.childNodes = new HashMap<>();
        this.isEnd = false;
        this.clientId = null;
    }

    public TrieNode getChildNode(String str) {
        return this.childNodes.get(str);
    }

    public TrieNode setChildNode(String str, TrieNode node) {
        return this.childNodes.computeIfAbsent(str, k -> node);
    }

    public boolean hasChild(String str) {
        return this.childNodes.containsKey(str);
    }

    public void setIsEnd() {
        this.isEnd = true;
    }

    public void unSetIsEnd() {
        this.isEnd = false;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void setClientId(String id) {
        this.clientId = id;
    }

    public String getClientId() {
        return this.clientId;
    }
}
