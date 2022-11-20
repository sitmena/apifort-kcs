package com.sitech.oidc.keycloak;

import com.sitech.util.ServiceConstants;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class ServerConnection {


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
            throw new UnauthorizedException("old Password ");
        }
        return instance;
    }

}
