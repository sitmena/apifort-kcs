package com.sitech.service;

import com.sitech.oidc.keycloak.ServerConnection;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ObjectInputStream;
import java.util.Objects;

@ApplicationScoped
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    @Inject
    ServerConnection connection;

    public AccessTokenResponse getAccessToken(String username, String password) {
        return connection.getInstanceByUser(username, password).tokenManager().getAccessToken();
    }

    public AccessTokenResponse getUserAccessToken(String realmName , String username, String password) {
        return connection.getInstanceByRealmUser(realmName ,username, password).tokenManager().getAccessToken();
    }

}
