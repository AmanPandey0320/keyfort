package com.kabutar.keyfort.data.entity;

import jakarta.persistence.*;

import java.util.List;

import com.kabutar.keyfort.data.entity.client.Client;

@Entity
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    private String displayName;

    @OneToMany(mappedBy = "dimension")
    private List<Client> clients;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

	@Override
	public String toString() {
		return "Dimension [id=" + id + ", name=" + name + ", displayName=" + displayName + ", clients=" + clients
				+ ", isActive=" + isActive + "]";
	}
    
    
}
