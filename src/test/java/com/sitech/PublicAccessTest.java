package com.sitech;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@Slf4j
public class PublicAccessTest {

    @GrpcClient
    com.sitech.access.PublicAccessService client;

    @Test
    void shouldReturnPublicKey() {
        log.info(">>> Start Test Get Public Key ");
        com.sitech.access.PublicKeyReplay response = client.getPublicKey(com.sitech.access.PublicKeyRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }

    @Test
    void shouldReturnCertificate() {
        log.info(">>> Start Test Get Public Key ");
        com.sitech.access.PublicKeyReplay response = client.getCertificate(com.sitech.access.PublicKeyRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }
}
