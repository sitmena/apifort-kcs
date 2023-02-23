package com.sitech.service;

import com.sitech.realm.RealmNameRequest;
import com.sitech.util.ProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.*;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;
import java.util.List;

@ApplicationScoped
@Slf4j
public class RealmService extends ProfileUtil {

    public List<RealmRepresentation> getRealms() {
        List<RealmRepresentation> realm = null;
        try {
            realm = connection.getInstance().realms().findAll();
        } catch (Exception ex) {
            exceptionHandler(404, "Realms Not Found");
        }
        return realm;
    }

    public void createRealm(String realmName, String displayName) {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setId(realmName);
        realmRepresentation.setRealm(realmName);
        realmRepresentation.setDisplayName(displayName);
        realmRepresentation.setEnabled(true);
        try {
            connection.getInstance().realms().create(realmRepresentation);
        } catch (Exception ex) {
            exceptionHandler(409, REALM.concat(realmName).concat(" Already Exist"));
        }
    }

    public int addRealmGroup(com.sitech.realm.AddRealmGroupRequest request) {
        int result=0;
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(request.getGroupName());
        RealmResource realm = getRealmByName(request.getRealmName());
        result = realm.groups().add(groupRepresentation).getStatus();
        if(result == 409){
           exceptionHandler(409, "Group Already Exist.");
        }
        return result;
    }

    public List<UserRepresentation> getRealmUsers(String realmName) {
        List<UserRepresentation> users = getRealmByName(realmName).users().list();
        if (users.isEmpty()) {
            exceptionHandler(404, REALM.concat(realmName).concat(" Not have any users"));
        }
        return users;
    }

    public List<GroupRepresentation> getRealmGroups(String realmName) {
        List<GroupRepresentation> group = null;
        try {
            group = getRealmByName(realmName).groups().groups();
        } catch (Exception ex) {
            exceptionHandler(404, REALM.concat(realmName).concat(" Not Found"));
        }
        return group;
    }

    public GroupRepresentation getRealmGroupByName(String realmName, String groupName) {
        List<GroupRepresentation> groups = getRealmByName(realmName).groups().groups();
        return groups.stream()
                .filter(x -> groupName.equals(x.getName()))
                .findAny()
                .orElseThrow(() -> new BadRequestException("No Group Found"));
    }

    //    @SneakyThrows
    public List<ClientRepresentation> getRealmClients(String realmName) {
        List<ClientRepresentation> lst = null;
        try {
            lst = getRealmByName(realmName).clients().findAll();
            if (lst.isEmpty()) {
                exceptionHandler(404, " No Client Found");
            } else {
                return lst;
            }
        } catch (Exception ex) {
            exceptionHandler(404, " No Client Found");
        }
        return lst;
    }

    public List<RoleRepresentation> getRealmRoles(String realmName) {
        List<RoleRepresentation> role = null;
        try {
            role = getRealmByName(realmName).roles().list();
        } catch (Exception ex) {
            exceptionHandler(404, " No Role Found");
        }
        return role;
    }

    public void logoutAllUsers(RealmNameRequest request) {
        getRealmByName(request.getRealmName()).logoutAll();
    }
}
