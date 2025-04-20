package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client,String> {
    public Client findByClientId(String clientId);
    public Client save(Client client);
}
