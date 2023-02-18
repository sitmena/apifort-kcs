package com.sitech.service;

import com.sitech.oidc.keycloak.PublicAccess;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KeysService {

    @Inject
    PublicAccess publicAccess;

    public String getPublicKey(String realmName) {
        return publicAccess.getRealmPublicKey(realmName);
    }
    public String getRealmCertificate(String realmName) { return publicAccess.getRealmCertificate(realmName); }
}
