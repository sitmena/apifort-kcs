package com.sitech.oidc.keycloak;

import com.sitech.realm.ServiceLoginRequest;
import com.sitech.users.UserLoginRequest;
import com.sitech.util.ServiceConstants;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
@Slf4j
public class ServerConnection {

    private static final String UN_AUTHORIZED = "UnAuthorized!";
    @ConfigProperty(name = ServiceConstants.SERVER_URL)
    String serverUrl;
    @ConfigProperty(name = ServiceConstants.ADMIN_REALM)
    String masterRealm;
    @ConfigProperty(name = ServiceConstants.ADMIN_CLIENT_ID)
    String adminClientId;
    @ConfigProperty(name = ServiceConstants.ADMIN_CLIENT_SECRET)
    String adminClientSecret;

    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(masterRealm)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public Keycloak getInstance2() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(masterRealm)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
//                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).register(new CustomJacksonProvider()).build())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }


    public Keycloak getInstanceByUser(String userName, String userPass) {
        Keycloak instance = null;
        try {
            instance = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(masterRealm)
                    .clientId(adminClientId)
                    .clientSecret(adminClientSecret)
                    .username(userName) //
                    .password(userPass) //
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();
        } catch (Exception ex) {
            throw new UnauthorizedException(UN_AUTHORIZED);
        }
        return instance;
    }


    public Keycloak getInstanceByRealmUser(String realmName, String userName, String userPass) {
        Keycloak instance = null;

        ClientsResource clientsResource = getInstance().realm(realmName).clients();
        List<ClientRepresentation> clientRepresentations = clientsResource.findByClientId(adminClientId);
        for(ClientRepresentation clientRepresentation : clientRepresentations) {
            try {
                instance = KeycloakBuilder.builder()
                        .serverUrl(serverUrl)
                        .realm(realmName)
                        .clientId(adminClientId)
                        .clientSecret(clientRepresentation.getSecret())
                        .username(userName) //
                        .password(userPass) //
                        .grantType(OAuth2Constants.PASSWORD)
                        .build();
            } catch (Exception ex) {
                throw new UnauthorizedException(UN_AUTHORIZED);
            }
        }
        return instance;
    }



    public Keycloak getInstanceByClientCredentials(ServiceLoginRequest request) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(request.getRealmName())
                .clientId(request.getClientId())
                .clientSecret(request.getClientId())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public Keycloak getInstanceByUserPassword(UserLoginRequest request) {
        Keycloak instance = null;
        try {
            instance = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(request.getRealmName())
                    .clientId(request.getClientId())
                    .clientSecret(request.getClientSecret())
                    .username(request.getUserName()) //
                    .password(request.getUserPassword()) //
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();
        } catch (Exception ex) {
            throw new UnauthorizedException(UN_AUTHORIZED);
        }
        return instance;
    }



}
