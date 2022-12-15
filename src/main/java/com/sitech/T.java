package com.sitech;

import io.grpc.Status;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;

import javax.enterprise.event.Observes;
import javax.ws.rs.NotFoundException;
import java.util.Objects;

@Slf4j
public class T {
    void onStart(@Observes StartupEvent ev) {
    log.info("___________________");
//
//
//        Keycloak instance  = KeycloakBuilder.builder()
//                .serverUrl("http://0.0.0.0:8180/")
//                    .realm("master")
//                    .clientId("backend-client")
//                    .clientSecret("UGSNMQIBgEjOXuqQh4zP7SK8Mf94mGZh")
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .build();
//
//        if(instance.isClosed()){
//            log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        }
//
//        if(Objects.isNull(instance.realm("master1"))){
//            throw Status.NOT_FOUND.getCode().toStatus().withDescription("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX").asRuntimeException();
//        }

//
//        RealmResource c = instance.realm("master1");
//        c.users();
//
//        try{
//                c.users().list().size();
//        } catch (Exception error) {
////            log.error(error.getMessage()); // in your case this will print "User exists with same username"
//            throw Status.NOT_FOUND.getCode().toStatus().withDescription(error.getMessage()).asRuntimeException();
//
////            throw new NotFoundException(error.getMessage());
//
////            try {
////                throw new HttpResponseException(404,error.getMessage());
////            } catch (HttpResponseException e) {
////                log.info("))))))))))))))))))))))))))))))))))))");
////                throw new RuntimeException(e);
////            }
//
//        }


//        Keycloak instance = null;
//            log.info("___________________ 1 ");
//            instance = KeycloakBuilder.builder()
//                    .serverUrl("http://0.0.0.0:8180/")
//                    .realm("master1")
//                    .clientId("backend-client")
//                    .clientSecret("UGSNMQIBgEjOXuqQh4zP7SK8Mf94mGZh")
//                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                    .build();
//
//            try {
//                    log.info("___________________ 3");
//                    log.info("------- {}", instance.realm("shefa-admin").toRepresentation().getId());
//        } catch (Exception e) {
//            log.info("___________________ 2 ");
//        }


    }
}
