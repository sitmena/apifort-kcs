package com.sitech.grpc.service;

import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import com.sitech.service.KeysService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@GrpcService
@Slf4j
public class PublicAccessGrpcService implements com.sitech.access.PublicAccessService {

    private static final Logger log = LoggerFactory.getLogger(PublicAccessGrpcService.class);
    @Inject
    KeysService keysService;

    @Override
    public Uni<PublicKeyReplay> getPublicKey(PublicKeyRequest request) {
      log.info("************************ {} " , request.getRealmName());
        return Uni.createFrom().item( () -> PublicKeyReplay.newBuilder().setValue(keysService.getPublicKey(request.getRealmName())).build());
    }

    @Override
    public Uni<PublicKeyReplay> getCertificate(PublicKeyRequest request) {
        return Uni.createFrom().item( () -> PublicKeyReplay.newBuilder().setValue(keysService.getRealmCertificate(request.getRealmName())).build());
    }

}
