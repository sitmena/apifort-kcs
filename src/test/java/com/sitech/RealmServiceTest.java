package com.sitech;

import com.sitech.realm.GetRealmGroupsResponse;
import com.sitech.realm.RealmNameRequest;
import com.sitech.realm.RealmUserResponse;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@Slf4j
public class RealmServiceTest {

    @GrpcClient
    com.sitech.realm.RealmService client;

    @Test
    void shouldReturnRealmName() {

        log.info(">>> Start Test Get Realm Name ");
        com.sitech.realm.RealmResponse response = client.getRealmByName(com.sitech.realm.RealmNameRequest.newBuilder().setRealmName("sitech").build()).await().atMost(Duration.ofSeconds(5));
        assertEquals("sitech", response.getRealmDto().getRealm());
        assertNotNull(response);
    }

    @Test
    void shouldReturnRealmGroups() {
        log.info(">>> Start Test Get Realm Groups ");
        GetRealmGroupsResponse response = client.getRealmGroups(RealmNameRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }

    @Test
    void shouldReturnRealmClients() {
        log.info(">>> Start Test Get Realm Clients ");
        com.sitech.realm.GetRealmClientsResponse response = client.getRealmClients(RealmNameRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }

    @Test
    void shouldReturnRealmRoles() {
        log.info(">>> Start Test Get Realm Roles ");
        com.sitech.realm.GetRealmRolesResponse response = client.getRealmRoles(RealmNameRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }

    @Test
    void shouldReturnRealmUsers() {
        log.info(">>> Start Test Get Realm Users ");
        RealmUserResponse response = client.getRealmUsers(RealmNameRequest.newBuilder().setRealmName("master").build()).await().atMost(Duration.ofSeconds(5));
        assertNotNull(response);
    }

}
