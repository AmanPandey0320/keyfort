package com.kabutar.keyfort.util.url;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	private Map<Character,TrieNode> childNodes;
	private boolean isEnd;
	private String clientId;
	
	public TrieNode() {
		this.childNodes = new HashMap<>();
		this.isEnd = false;
		this.clientId = null;
	}
	
	public TrieNode getChildNode(Character idx) {
		return this.childNodes.get(idx);
	}
	
	public void setChildNode(Character idx, TrieNode node) {
		this.childNodes.put(idx, node);
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
