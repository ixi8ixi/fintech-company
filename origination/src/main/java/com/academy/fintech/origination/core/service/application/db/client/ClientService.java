package com.academy.fintech.origination.core.service.application.db.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The service responsible for interacting with the database of bank clients.
 */
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Check if a client with the given email exists, and if not, create a new client in the database.
     *
     * @return the ID of the newly created client.
     * @throws ClientException if a client with the given email already exists
     */
    @Transactional
    public String findOrCreate(Client client) {
        Optional<Client> clientCandidate = clientRepository.findByEmail(client.getEmail());
        if (clientCandidate.isPresent()) {
            Client fromBase = clientCandidate.get();
            if (!fromBase.equalFields(client)) {
                throw new ClientException(client.getEmail());
            }
            return fromBase.getId();
        }
        return clientRepository.save(client).getId();
    }
}
