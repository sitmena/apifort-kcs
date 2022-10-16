package com.sitech;

//import dasniko.testcontainers.keycloak.KeycloakContainer;
//import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
//import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class KeycloakResource { //} implements QuarkusTestResourceLifecycleManager {

//    KeycloakContainer keycloak;
//
//    @Override
//    public Map<String, String> start() {
//        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:18.0.2")
//                .withRealmImportFile("/sitech-realm.json");
//        keycloak.start();
////        return Map.of("quarkus.oidc.auth-server-url", StringUtils.chop(keycloak.getAuthServerUrl()) + "/realms/quarkus");
//        return Map.of("keycloak.server.url", keycloak.getAuthServerUrl());
//
//    }
//
//    @Override
//    public void stop() {
//        if (keycloak != null) {
//            keycloak.stop();
//        }
//    }


//    @Override
//    public void inject(TestInjector testInjector) {
//        testInjector.injectIntoFields(keycloak, new TestInjector.AnnotatedAndMatchesType(InjectKeycloakContainer.class, KeycloakContainer.class));
//    }
}
