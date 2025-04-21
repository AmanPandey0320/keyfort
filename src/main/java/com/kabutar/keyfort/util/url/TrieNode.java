package com.kabutar.keyfort.util.url;

public class TrieNode {
	private TrieNode[] childNodes;
	private boolean isEnd;
	private String clientId;
	
	public TrieNode() {
		this.childNodes = new TrieNode[26];
		this.isEnd = false;
		this.clientId = null;
	}
	
	public TrieNode getChildNode(int idx) {
		return this.childNodes[idx];
	}
	
	public void setChildNode(int idx, TrieNode node) {
		this.childNodes[idx] = node;
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
