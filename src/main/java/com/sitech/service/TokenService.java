package com.sitech.service;

import com.sitech.oidc.keycloak.ServerConnection;
import org.keycloak.representations.AccessTokenResponse;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TokenService {

    @Inject
    ServerConnection connection;

    public AccessTokenResponse getAccessToken(String username, String password) {
        return connection.getInstanceByUser(username, password).tokenManager().getAccessToken();
    }

    public AccessTokenResponse getUserAccessToken(String realmName , String username, String password) {
        return connection.getInstanceByRealmUser(realmName ,username, password).tokenManager().getAccessToken();
    }

}
