package com.kabutar.keyfort.data.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.entity.client.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client,String> {
    public Client findByClientId(String clientId);
    public Client save(Client client);
    
    @Query(value = "SELECT * FROM client", nativeQuery = true)
    public ArrayList<Client> getAllClients();
    
    public List<Client> findClientByDimension(Dimension d);
}
