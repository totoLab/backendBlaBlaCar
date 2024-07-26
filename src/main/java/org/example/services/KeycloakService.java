package org.example.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    private Keycloak keycloak;

    @PostConstruct
    private void initialize() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthServerUrl)
                .realm(keycloakRealm)
                .clientId(keycloakClientId)
                .clientSecret(keycloakClientSecret)
                .grantType("client_credentials")
                .build();
    }

    public List<UserRepresentation> getUsers() {
        return keycloak.realm(keycloakRealm).users().list();
    }

    public UserRepresentation getUserByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(username);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0); // assuming usenames are unique
    }

    public void deleteUser(String id) {
        try {
            keycloak.realm(keycloakRealm).users().delete(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

