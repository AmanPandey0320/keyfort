package com.kabutar.keyfort.util.url;

import com.kabutar.keyfort.data.entity.Client;
import com.kabutar.keyfort.data.repository.ClientRepo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TRIE based url matched for 
 * validating redirect url
 * 
 * HOW IT WORKS: A spring component intializes has a 
 * post construct method which reads all redirect uris 
 * of all clients from db and populates them in trie
 * at the end of every trie node where the url ends it
 * has clientid and if after matching url, it returns the 
 * clientid of the redirect uri 
 * 
 */
@Component
public class Matcher {
	private static Logger logger = LoggerFactory.getLogger(Matcher.class);
	private TrieNode root;


	@Autowired
    private ClientRepo clientRepo;

	@PostConstruct
	public void init() {
		System.out.println("Matcher initialized....");

		this.root = new TrieNode();
        this.clientRepo.getAll().subscribe(
                client -> {
                    logger.debug("inserting client {}",client.getName());
                    this.insert(client);
                },
                err -> {
                    logger.error("Error while inserting client {}",err);
                }
        );
	}


	/**
	 *
	 * @param client
	 */
	public void insert(Client client) {
		 String[] uriParts = client.getRedirectUri().split("/");
         TrieNode node = root;

         for (String part : uriParts) {
             if (part.isEmpty()) continue;
             node = node.setChildNode(part, new TrieNode());
         }
         node.setIsEnd();
         node.setClientId(client.getId().toString());

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
		String urlWithOutQuery = url;

		if(url.contains("?")) {
			urlWithOutQuery = url.split("\\?")[0];
		}

		String[] urlParts = urlWithOutQuery.split("/");

		String matchedClientId = this.matchRecursive(urlParts, 0, curr);

		return clientId.equals(matchedClientId);
	}


	/**
	 *
	 * @param parts
	 * @param index
	 * @param node
	 * @return
	 */
	private String matchRecursive(String[] parts, int index, TrieNode node) {
		// wildcard match: * (matches any one segment)
        if (node.hasChild("*")) {
            String result = matchRecursive(parts, index + 1, node.getChildNode("*"));
            if (result != null) return result;
        }

        if (index >= parts.length) {
            if (node.isEnd()) return node.getClientId();
            return null;
        }

        String segment = parts[index];

        // encounters a blank segment, skip it
        if(segment.isEmpty()) {
        	return this.matchRecursive(parts, index+1, node);
        }

        // Exact match
        if (node.hasChild(segment)) {
            String result = this.matchRecursive(parts, index + 1, node.getChildNode(segment));
            if (result != null) return result;
        }

        // deep wildcard match: ** (matches remaining segments)
        if (node.hasChild("**")) {
            for (int i = index; i <= parts.length; i++) {
                String result = matchRecursive(parts, i, node.getChildNode("**"));
                if (result != null) return result;
            }
        }

        return null;
    }
}
