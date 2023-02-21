package com.sitech.service;

import com.sitech.communication.RefreshTokenService;
import com.sitech.exception.ApiFortException;
import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.token.LoginByServiceCredentialsRequest;
import com.sitech.token.LoginByUserCredentialsRequest;
import com.sitech.token.RefreshTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.keycloak.representations.AccessTokenResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Slf4j
@ApplicationScoped
public class TokenService {

    @Inject
    ServerConnection connection;
    @Inject
    @RestClient
    RefreshTokenService refreshTokenService;

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

    public AccessTokenResponse refreshToken(RefreshTokenRequest request) {
        Response response = refreshTokenService.refreshToken(request.getRealmName(),buildForm(request));
        if(response.getStatus() != HttpStatus.SC_OK){
            throw new ApiFortException("Something went wrong please contact system administrator! ");
        }
        AccessTokenResponse accessTokenResponse = response.readEntity(AccessTokenResponse.class);
        return accessTokenResponse;
    }

    private MultivaluedMap<String, String> buildForm(RefreshTokenRequest request) {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle("realm", request.getRealmName());
        form.putSingle("client_id", request.getClientId());
        form.putSingle("client_secret", request.getClientSecret());
        form.putSingle("grant_type", "refresh_token");
        form.putSingle("refresh_token", request.getRefreshedToken());
        return form;
    }
}
