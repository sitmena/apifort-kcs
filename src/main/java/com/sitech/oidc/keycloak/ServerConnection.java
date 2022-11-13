package com.sitech.oidc.keycloak;

import com.sitech.util.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class ServerConnection {

//    private static final Logger log = LoggerFactory.getLogger(ServerConnection.class);

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
//                    .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();

    }

    public Keycloak getInstanceByUser(String userName, String userPass) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(masterRealm)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .username(userName) //
                .password(userPass) //
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }


}
