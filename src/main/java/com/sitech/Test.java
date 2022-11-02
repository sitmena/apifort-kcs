package com.sitech;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.event.Observes;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

@Slf4j
public class Test {

    void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting...");
//        addUserRole("ajweh","ajweh","uma_authorization");


//        Keycloak keycloak = KeycloakBuilder.builder()
//                .serverUrl("http://localhost:8180/")
//                .realm("master") // shefa-doner
//                .grantType(OAuth2Constants.PASSWORD) //
//                .clientId("backend-client") // shefa-doner-client
//                .clientSecret("BlTBbHGD2HNIqrWahAXFj7nfF8I5mScw")  // TZ1IELeNqb0FLqBIZHtj87qYZHQkUxJQ
//                .username("ajweh")
//                .password("ajweh")
//                .build();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8180/")
                .realm("master")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("backend-client")
                .clientSecret("T02GkKu1terJ22z6mo8xFHiDx0iQx1cX")
                .build();

        // T02GkKu1terJ22z6mo8xFHiDx0iQx1cX

//                Keycloak keycloak = KeycloakBuilder.builder()
//                .serverUrl("http://localhost:8180/")
//                .realm("shefa-charity")
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId("shefa-charity-client")
//                .clientSecret("1t9y1I1x0SSpqBxfjYyeyw7KsmBpsgM6")
//                .username("ajweh")
//                .password("ajweh")
//                .build();

//        log.info(">> {} ", keycloak.tokenManager().getAccessToken().getToken());
//
//        List<UserRepresentation> users = keycloak.realm("shefa-charity").users().list();
//        for (UserRepresentation u : users) {
//            log.info("******** {} === {} ", u.getId(), u.getUsername());
//        }
//
//        List<UserRepresentation> xx = keycloak.realm("shefa-charity").users().searchByAttributes("ali");
//        for (UserRepresentation u : xx){
//            log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^ {} ",u.getUsername());
//        }


//        UserResource userResource = keycloak.realm("shefa-charity").users().get("0dc4cf70-bfcc-49b4-b433-68604e1e90e4");
//        UserRepresentation userRepresentations = userResource.toRepresentation();
//
//        Map<String, List<String>> v = userRepresentations.getAttributes();
//        String key = "origin";
////        v.put(key, v.getOrDefault(key, Arrays.asList("demo replace")));
//        v.replace(key,Arrays.asList("demo replace"));
//        userResource.update(userRepresentations);
//
//
//        Map<String, List<String>> attributes = userRepresentations.getAttributes();
//        for (var entry : v.entrySet()) {
//            log.info("------------------ {} -------- {} ", entry.getKey(), entry.getValue());
//        }


        //         Map<String, List<String>> v = userRepresentations.getAttributes();
//         String key = "origin";
//         v.put(key, v.getOrDefault(key, Arrays.asList("demo replace")));
//         userRepresentations.setAttributes(v);

//        Map<String, List<String>> requestAttributesMap = userRepresentations.getAttributes();


//        Map<String, List<String>> attributes = userRepresentations.getAttributes();
//        for (var entry : attributes.entrySet()) {
//            log.info("------------------ {} -------- {} ", entry.getKey(), entry.getValue());
//        }


        //.get("xxx");

        /*
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("xxxxxx");
        UserRepresentation user = new UserRepresentation();
        user.setUsername("test2");
        user.setFirstName("test");
        user.setLastName("test");
        user.setEmail("test2@test.com");
        user.setCredentials(Arrays.asList(credential));
        user.setEnabled(true);
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("test")));
        Response response = keycloak.realm("shefa-charity").users().create(user);
        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
*/

//        log.info("*********** {} ", response.getStatusInfo().getReasonPhrase());
//
//
//        RoleRepresentation testerRealmRole = keycloak.realm("shefa-doner").roles().get("doner").toRepresentation();
//
//        List<UserRepresentation> u = keycloak.realm("shefa-doner").users().list();
//        for (UserRepresentation us : u) {
//            if (us.getUsername().equals("test3")) {
//                log.info("________ {} ", user.getId());
//                UserResource userResource1 = keycloak.realm("shefa-doner").users().get(us.getId());
//                userResource1.roles().realmLevel().add(Arrays.asList(testerRealmRole));
//            }
//        }
//
//        log.info(">> {} ", keycloak.tokenManager().getAccessToken().getToken());
    }
}

