package com.kabutar.keyfort.util.url;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.Entity.Client;
import com.kabutar.keyfort.repository.ClientRepository;

import jakarta.annotation.PostConstruct;

/**
 * TRIE based url matched for 
 * validating redirect url
 * 
 * HOW IT WORKS: A spring component intializes has a 
 * post construct method which reads all redirect uris 
 * of all clients from db and populates them isn trie
 * at the end of every trie node where the url ends it
 * has clientid and if after matching url, it returns the 
 * clientid of the redirect uri 
 * 
 */
@Component
public class Matcher {
	
	private TrieNode root;
	
	
	@Autowired
	ClientRepository clientRepo;
	
	@PostConstruct
	public void init() {
		System.out.println("Matcher initialized....");
		
		this.root = new TrieNode();
		ArrayList<Client> clients = this.clientRepo.getAllClients();
		for(Client client: clients) {
			this.insert(client);
		}
	}
	
	
	/**
	 * 
	 * @param client
	 */
	public void insert(Client client) {
		TrieNode curr = this.root;
		String redirectUri = client.getRedirectUri();
		
		for(int i=0;i<redirectUri.length();i++) {
			if(redirectUri.charAt(i) == '*') {
				break;
			}
			curr.setChildNode(redirectUri.charAt(i), new TrieNode());
			curr = curr.getChildNode(redirectUri.charAt(i));
		}
		curr.setIsEnd();
		curr.setClientId(client.getClientId());
		
		return;
	}
	
	/**
	 * 
	 * @param url
	 * @param clientId
	 * @return
	 */
	public boolean match(String url,String clientId) {
		
		TrieNode curr = this.root;
		int len = url.length();
		
		for(int i=0;i<url.length();i++) {
			if(curr == null) {
				return false;
			}
			
			if(this.isMatch(curr, clientId)) {
				return true;
			}
			
			curr = curr.getChildNode(url.charAt(i));
		}
		
		if(this.isMatch(curr, clientId)) {
			return true;
		}
		
		return false;
	}
	
	
	private boolean isMatch(TrieNode node, String clientId) {
		if(node.isEnd() && node.getClientId().equals(clientId)) {
			return true;
		}
		
		TrieNode next = node.getChildNode('/');
		
		if(next != null && next.isEnd() && next.getClientId().equals(clientId)) {
			return true;
		}
		
		return false;
	}
}
