package com.sitech.service;

import com.sitech.exception.DataConflictException;
import com.sitech.exception.ErrorResponse;
import com.sitech.exception.ResourceNotFoundException;
import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.realm.RealmNameRequest;
import io.quarkus.security.UnauthorizedException;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;


@ApplicationScoped
public class RealmService {

    private static final Logger log = LoggerFactory.getLogger(RealmService.class);

    @Inject
    ServerConnection connection;

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
            exceptionHandler(409, "Realm ".concat(realmName).concat(" Already Exist"));
        }
    }

    public RealmResource getRealmByName(String realmName) {
        RealmResource realmResource = null;
        try {
            realmResource = connection.getInstance().realm(realmName);
        } catch (Exception ex) {
            exceptionHandler(404, "Realm ".concat(realmName).concat(" Not Found"));
        }
        return realmResource;
    }

    public int addRealmGroup(com.sitech.realm.AddRealmGroupRequest request) {
        int result=0;
        try {
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setName(request.getGroupName());
            result = getRealmByName(request.getRealmName()).groups().add(groupRepresentation).getStatus();
        } catch (Exception ex) {
            exceptionHandler(500, "Error occurred during Adding Group,please contact your system administrator");
        }
        return result;
    }

    public List<UserRepresentation> getRealmUsers(String realmName) {
        List<UserRepresentation> users = getRealmByName(realmName).users().list();
        if (users.size() == 0) {
            exceptionHandler(404, "Realm ".concat(realmName).concat(" Not have any users"));
        }
        return users;
    }

    public List<GroupRepresentation> getRealmGroups(String realmName) {
        List<GroupRepresentation> group = null;
        try {
            group = connection.getInstance().realm(realmName).groups().groups();
        } catch (Exception ex) {
            exceptionHandler(404, "Realm ".concat(realmName).concat(" Not Found"));
        }
        return group;
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

    private void exceptionHandler(int statusCode, String exceptionMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(statusCode);
        errorResponse.setErrorMessage(exceptionMessage);
        switch (statusCode) {
            case 401:
                throw new UnauthorizedException(errorResponse.toString());
            case 404:
                throw new ResourceNotFoundException(errorResponse.toString());
            case 409:
                throw new DataConflictException(errorResponse.toString());
            default:
                throw new InternalServerErrorException(errorResponse.toString());
        }
    }
}
