package com.sitech.service;

import com.sitech.exception.ApiFortException;
import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.realm.RealmNameRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class RealmService {

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
        List<GroupRepresentation> groups = connection.getInstance().realm(realmName).groups().groups();
        return groups.stream()
                .filter(x -> groupName.equals(x.getName()))
                .findAny()
                .orElseThrow(() -> new BadRequestException("No Group Found"));


    }

    //    @SneakyThrows
    public List<ClientRepresentation> getRealmClients(String realmName) {
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

    public boolean isRealmExists(String realName ){
        RealmResource realm = connection.getInstance().realm(realName);
        if(Objects.isNull(realm)){
            throw new ApiFortException("No realm Found");
        }
        return true;
    }

    public void logoutAllUsers(RealmNameRequest request){
        RealmResource realmResource = connection.getInstance().realm(request.getRealmName());
        realmResource.logoutAll();
    }


}
