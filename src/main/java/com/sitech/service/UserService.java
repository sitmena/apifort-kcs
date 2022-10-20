package com.sitech.service;

import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.users.AddUserRequest;
import com.sitech.users.StatusReplay;
import com.sitech.users.UpdateUserPasswordRequest;
import com.sitech.users.UpdateUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    ServerConnection connection;
    @Inject
    RealmService realmService;
    private static final Logger log = LoggerFactory.getLogger(RealmService.class);

//    void onStart(@Observes StartupEvent ev) {
//        log.info("The application is starting...");
//    }


    public Response addUser(AddUserRequest request) {

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPass());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUserName());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setCredentials(Arrays.asList(credential));
        user.setEnabled(true);
        Response response = connection.getInstance().realm(request.getRealmName()).users().create(user);
        if(StringUtils.isNoneEmpty(request.getRealmRole())) {
            assignRoleToUser(request.getRealmName(), request.getUserName(), request.getRealmRole());
        }
        return response;
    }

    private void assignRoleToUser(String realmName , String userName ,String realmRole) {
        List<String> roleLst = Arrays.stream(realmRole.split(",")).toList();
        for(String role : roleLst) {
            RoleRepresentation testerRealmRole = connection.getInstance().realm(realmName).roles().get(role).toRepresentation();
            List<UserRepresentation> userRepresentations = connection.getInstance().realm(realmName).users().list();
            for (UserRepresentation user : userRepresentations) {
                if (user.getUsername().equals(userName)) {
                    UserResource userResource1 = connection.getInstance().realm(realmName).users().get(user.getId());
                    userResource1.roles().realmLevel().add(Arrays.asList(testerRealmRole));
                }
            }
        }
    }

    public List<GroupRepresentation> getUserGroups(String realmName, String userId) {
        return realmService.getRealmByName(realmName).users().get(userId).groups();
    }

    public List<RoleRepresentation> getUserRoleEffective(String realmName, String userId) {
        return realmService.getRealmByName(realmName).users().get(userId).roles().realmLevel().listEffective();
    }

    public List<RoleRepresentation> getUserRoleAvailable(String realmName, String userId) {
        return realmService.getRealmByName(realmName).users().get(userId).roles().realmLevel().listAvailable();
    }

    public UserRepresentation getUserByAttributes(String realmName, String attribute) {
        List<UserRepresentation> userRepresentations = realmService.getRealmByName(realmName).users().searchByAttributes(attribute);
        if (!userRepresentations.isEmpty()) {
            for (UserRepresentation usr : userRepresentations){
                if (usr.getUsername().equals(attribute)){
                    return usr;
                }
            }
        }
        return null;
    }

    public UserRepresentation getUserById(String realmName, String userId) {
        UserResource userRepresentations = realmService.getRealmByName(realmName).users().get(userId);
        return userRepresentations.toRepresentation();
    }

    public List<UserRepresentation> findAllUsersInGroup(String realmName, String groupName) {
       GroupsResource groupsResource = realmService.getRealmByName(realmName).groups();
       log.info("__________________ {} ",groupsResource.groups().size());
       GroupResource groupResource =  groupsResource.group(groupName);
        List<UserRepresentation> members = groupResource.members();
        log.info("______+++++++++____________ {} ",members.size());
//       for(GroupsResource group : groupRepresentations){
////           group.
//       }
        return realmService.getRealmByName(realmName).groups().group(groupName).members();
    }

    public Collection<UserRepresentation> findUserByRole(String realmName, String roleName) {
        RoleResource roleResource = realmService.getRealmByName(realmName).roles().get(roleName);
        return roleResource.getRoleUserMembers();
    }

    public String addUserRole(String realmName, String userName, String userRole) {
        RealmResource realmResource = realmService.getRealmByName(realmName);
        UsersResource userResource = realmResource.users();
        UserRepresentation usr = getUserByAttributes(realmName, userName);
        List<RoleRepresentation> roleRepresentations = getUserRoleAvailable(realmName, usr.getId());
        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            if (roleRepresentation.getName().equals(userRole)) {
                userResource.get(usr.getId()).roles().realmLevel().add(Arrays.asList(roleRepresentation));
                log.info(">> Role Added ... ");
            }
        }
        return "success";
    }

    public String addUserToGroup(String realmName, String userName, String groupName) {
        UserRepresentation usr = getUserByAttributes(realmName, userName);
        List<GroupRepresentation> grouplst = realmService.getRealmGroups(realmName);
        for (GroupRepresentation gr : grouplst) {
            if (gr.getName().equals(groupName)) {
                realmService.getRealmByName(realmName).users().get(usr.getId()).joinGroup(gr.getId());
                return "success";
            }
        }
        return "";
    }


    public UserRepresentation updateUser(UpdateUserRequest updateUserRequest) {
        log.info("^^^^^^^^^^^^ Start Update User Id = {} **** realm = {} ", updateUserRequest.getUserId() , updateUserRequest.getRealmName());
        UserResource userResource = connection.getInstance().realm(updateUserRequest.getRealmName()).users().get(updateUserRequest.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setUsername(updateUserRequest.getUserName());
        userRepresentation.setEmail(updateUserRequest.getEmail());
        userRepresentation.setFirstName(updateUserRequest.getFirstName());
        userRepresentation.setLastName(updateUserRequest.getLastName());
        userRepresentation.setEnabled(updateUserRequest.getEnabled());
        userResource.update(userRepresentation);
        return realmService.getRealmByName(updateUserRequest.getRealmName()).users().get(updateUserRequest.getUserId()).toRepresentation();
    }

    public String updateUserService(UpdateUserPasswordRequest request) {
        UserResource userResource = connection.getInstance().realm(request.getRealmName()).users().get(request.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        userRepresentation.setCredentials(Arrays.asList(credential));
        userResource.update(userRepresentation);
        return "success";
    }
}