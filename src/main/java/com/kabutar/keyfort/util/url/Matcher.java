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
	}
	
	
	
	public void insert(Client client) {
		TrieNode curr = this.root;
		String redirectri = client.getRedirectUri();
		
	}
}
