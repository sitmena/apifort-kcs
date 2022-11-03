package com.sitech.service;

import com.sitech.oidc.keycloak.PublicAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KeysService {

    private static final Logger log = LoggerFactory.getLogger(RealmService.class);

    @Inject
    PublicAccess publicAccess;

    public String getPublicKey(String realmName) {
        return publicAccess.getRealmPublicKey(realmName);
    }
    public String getRealmCertificate(String realmName) { return publicAccess.getRealmCertificate(realmName); }
}
