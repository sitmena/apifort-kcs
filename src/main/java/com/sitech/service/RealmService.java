package com.sitech.service;

import com.sitech.exception.ApiFortException;
import com.sitech.oidc.keycloak.ServerConnection;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;

@ApplicationScoped
public class RealmService {

    private static final Logger log = LoggerFactory.getLogger(RealmService.class);

    @Inject
    ServerConnection connection;


    public List<RealmRepresentation> getRealms() {
        return connection.getInstance().realms().findAll();
    }

    public void createRealm(String realmName, String displayName) {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setId(realmName);
        realmRepresentation.setRealm(realmName);
        realmRepresentation.setDisplayName(displayName);
        realmRepresentation.setEnabled(true);
        connection.getInstance().realms().create(realmRepresentation);
    }

    public RealmResource getRealmByName(String realmName) {
        return connection.getInstance().realm(realmName);
    }

    public int addRealmGroup(com.sitech.realm.AddRealmGroupRequest request) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(request.getGroupName());
        return connection.getInstance().realm(request.getRealmName()).groups().add(groupRepresentation).getStatus();
    }

    public List<UserRepresentation> getRealmUsers(String realmName) {
        return connection.getInstance().realm(realmName).users().list();
    }

    public List<GroupRepresentation> getRealmGroups(String realmName) {
        return connection.getInstance().realm(realmName).groups().groups();
    }

    public GroupRepresentation getRealmGroupByName(String realmName, String groupName) {
//        return connection.getInstance().realm(realmName).groups().group(groupName).toRepresentation();
        List<GroupRepresentation> groups = connection.getInstance().realm(realmName).groups().groups();
        return groups.stream()
                .filter(x -> groupName.equals(x.getName()))
                .findAny()
                .orElseThrow(() -> new BadRequestException("No Group Found"));


    }


    //    @SneakyThrows
    public List<ClientRepresentation> getRealmClients(String realmName) {
//        return connection.getInstance().realm(realmName).clients().findAll();
        List<ClientRepresentation> lst = connection.getInstance().realm(realmName).clients().findAll();
        if (lst.isEmpty()) {
            throw new ApiFortException("No Client Found");
        } else {
            return lst;
        }
    }

    public List<RoleRepresentation> getRealmRoles(String realmName) {
        return connection.getInstance().realm(realmName).roles().list();
    }
}
