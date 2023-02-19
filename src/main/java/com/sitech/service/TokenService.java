package com.sitech.service;

import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.token.LoginByServiceCredentialsRequest;
import com.sitech.token.LoginByUserCredentialsRequest;
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

    public AccessTokenResponse loginByUserCredentials(LoginByUserCredentialsRequest request){
        return connection.getInstanceByUserCredentials(request).tokenManager().getAccessToken();
    }

    public AccessTokenResponse loginByServiceCredentials(LoginByServiceCredentialsRequest request){
        return connection.getInstanceByServiceCredentials(request).tokenManager().getAccessToken();
    }

}
