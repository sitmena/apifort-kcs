package com.sitech.service;

import com.sitech.oidc.keycloak.ServerConnection;
import com.sitech.users.*;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.*;

@ApplicationScoped
public class UserService {

    private static final String SUCCESS = "success";
    @Inject
    ServerConnection connection;
    @Inject
    RealmService realmService;

    public Response addUser(AddUserRequest request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getCredentials().getPassword());
        credential.setTemporary(request.getCredentials().getTemporary());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUserName());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setCredentials(Arrays.asList(credential));
        user.setEnabled(true);
        user.setAttributes(prepareUserAttributes(null, request.getAttributesMap()));
        Response response = connection.getInstance().realm(request.getRealmName()).users().create(user);
        if (StringUtils.isNoneEmpty(request.getRole())) {
            assignRoleToUser(request.getRealmName(), request.getUserName(), request.getRole());
        }
        if (StringUtils.isNoneEmpty(request.getGroup())) {
            addUserToGroup(request.getRealmName(), request.getUserName(), request.getGroup());
        }
        return response;
    }

    private Map<String, List<String>> prepareUserAttributes(Map<String, List<String>> userAttributesMap, Map<String, String> requestAttributesMap) {
        Map<String, List<String>> attributes = new HashMap<>();
        if (!Objects.isNull(userAttributesMap) && !userAttributesMap.isEmpty()) {
            for (var entry : userAttributesMap.entrySet()) {
                List<String> value = entry.getValue();
                attributes.put(entry.getKey(), value);
            }
        }
        if (!Objects.isNull(requestAttributesMap) && !requestAttributesMap.isEmpty()) {
            for (var entry : requestAttributesMap.entrySet()) {
                List<String> value = Arrays.asList(entry.getValue());
                attributes.put(entry.getKey(), value);
            }
        }
        return attributes;
    }

    private void assignRoleToUser(String realmName, String userName, String realmRole) {
        List<String> roleLst = Arrays.stream(realmRole.split(",")).toList();
        for (String role : roleLst) {
            RoleRepresentation testerRealmRole = connection.getInstance().realm(realmName).roles().get(role).toRepresentation();
            List<UserRepresentation> userRepresentations = connection.getInstance().realm(realmName).users().list();
            for (UserRepresentation user : userRepresentations) {
                if (user.getUsername().equalsIgnoreCase(userName)) {
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

    private List<String> getUserRoleAsString(List<RoleRepresentation> lst) {
        List<String> roles = new ArrayList<>();
        for (RoleRepresentation role : lst) {
            roles.add(role.getName());
        }
        return roles;
    }

    private List<String> getUserGroupAsString(List<GroupRepresentation> userGroups) {
        List<String> groups = new ArrayList<>();
        for (GroupRepresentation group : userGroups) {
            groups.add(group.getName());
        }
        return groups;
    }

    public UserRepresentation getUserByAttributes(String realmName, String attribute) {
        List<UserRepresentation> userRepresentations = realmService.getRealmByName(realmName).users().searchByAttributes(attribute);
        if (!userRepresentations.isEmpty()) {
            for (UserRepresentation usr : userRepresentations) {
                if (usr.getUsername().equals(attribute)) {
                    usr.setRealmRoles(getUserRoleAsString(getUserRoleAvailable(realmName, usr.getId())));
                    usr.setGroups(getUserGroupAsString(getUserGroups(realmName, usr.getId())));
                    return usr;
                }
            }
        }
        return null;
    }

    public UserRepresentation getUserByUserName(String realmName, String userName) {

        List<UserRepresentation> userRepresentations = realmService.getRealmByName(realmName).users().search(userName);
        if (!userRepresentations.isEmpty()) {
            for (UserRepresentation usr : userRepresentations) {
                if (usr.getUsername().equalsIgnoreCase(userName)) {
                    usr.setRealmRoles(getUserRoleAsString(getUserRoleEffective(realmName, usr.getId())));
                    usr.setGroups(getUserGroupAsString(getUserGroups(realmName, usr.getId())));
                    return usr;
                }
            }
        }
        return null;
    }

    public UserRepresentation getUserById(String realmName, String userId) {
        UserResource userRepresentations = realmService.getRealmByName(realmName).users().get(userId);
        UserRepresentation user = userRepresentations.toRepresentation();
        user.setRealmRoles(getUserRoleAsString(getUserRoleAvailable(realmName, userId)));
        user.setGroups(getUserGroupAsString(getUserGroups(realmName, userId)));
        return user;
    }

    public List<UserRepresentation> findAllUsersInGroup(String realmName, String groupName) {
        List<GroupRepresentation> groupRepresentations = realmService.getRealmByName(realmName).groups().groups();
        for (GroupRepresentation group : groupRepresentations) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return realmService.getRealmByName(realmName).groups().group(group.getId()).members();
            }
        }
        return new ArrayList<>();
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
            }
        }
        return SUCCESS;
    }

    public String addUserToGroup(String realmName, String userName, String groupName) {
        UserRepresentation usr = getUserByUserName(realmName, userName);
        List<GroupRepresentation> groupLst = realmService.getRealmGroups(realmName);
        for (GroupRepresentation gr : groupLst) {
            if (gr.getName().equals(groupName)) {
                realmService.getRealmByName(realmName).users().get(usr.getId()).joinGroup(gr.getId());
                return SUCCESS;
            }
        }
        return "";
    }

    public UserRepresentation updateUser(UpdateUserRequest updateUserRequest) {
        UserResource userResource = connection.getInstance().realm(updateUserRequest.getRealmName()).users().get(updateUserRequest.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        if (StringUtils.isNotEmpty(updateUserRequest.getUserName())) {
            userRepresentation.setUsername(updateUserRequest.getUserName());
        }
        if (StringUtils.isNotEmpty(updateUserRequest.getEmail())) {
            userRepresentation.setEmail(updateUserRequest.getEmail());
        }
        if (StringUtils.isNotEmpty(updateUserRequest.getFirstName())) {
            userRepresentation.setFirstName(updateUserRequest.getFirstName());
        }
        if (StringUtils.isNotEmpty(updateUserRequest.getLastName())) {
            userRepresentation.setLastName(updateUserRequest.getLastName());
        }
        if (StringUtils.isNotEmpty(String.valueOf(updateUserRequest.getEnabled()))) {
            userRepresentation.setEnabled(updateUserRequest.getEnabled());
        }
        if (!updateUserRequest.getAttributesMap().isEmpty()) {
            userRepresentation.setAttributes(prepareUserAttributes(userRepresentation.getAttributes(), updateUserRequest.getAttributesMap()));
        }
        if (StringUtils.isNotEmpty(updateUserRequest.getGroup())) {
            List<GroupRepresentation> groupLst = realmService.getRealmGroups(updateUserRequest.getRealmName());
            for (GroupRepresentation gr : groupLst) {
                if (gr.getName().equals(updateUserRequest.getGroup())) {
                    realmService.getRealmByName(updateUserRequest.getRealmName()).users().get(userRepresentation.getId()).joinGroup(gr.getId());
                }
            }
        }
        if (StringUtils.isNotEmpty(updateUserRequest.getRole())) {
            RoleRepresentation testerRealmRole = connection.getInstance().realm(updateUserRequest.getRealmName()).roles().get(updateUserRequest.getRole()).toRepresentation();
                    userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));

        }
        userResource.update(userRepresentation);
        return realmService.getRealmByName(updateUserRequest.getRealmName()).users().get(updateUserRequest.getUserId()).toRepresentation();
    }

    public String updateUserPassword(UpdateUserPasswordRequest request) {
        UserResource userResource = connection.getInstance().realm(request.getRealmName()).users().get(request.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        userRepresentation.setCredentials(Arrays.asList(credential));
        userResource.update(userRepresentation);
        return SUCCESS;
    }

    public List<UserRepresentation> getAllUsersByRealm(GetUsersRequest request) {
        return connection.getInstance().realm(request.getRealmName()).users().list();
    }

    public UserRepresentation updateUserAttributes(updateUserAttributesRequest request) {
        UserResource userResource = realmService.getRealmByName(request.getRealmName()).users().get(request.getUserId());
        UserRepresentation userRepresentations = userResource.toRepresentation();
        Map<String, List<String>> attributes = userRepresentations.getAttributes();
        if (Objects.isNull(attributes)) {
            userRepresentations.setAttributes(prepareUserAttributes(userRepresentations.getAttributes(), request.getAttributesMap()));
        } else {
            for (var entry : request.getAttributesMap().entrySet()) {
                attributes.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        }
        userResource.update(userRepresentations);
        return realmService.getRealmByName(request.getRealmName()).users().get(request.getUserId()).toRepresentation();
    }

    public UserRepresentation getUserByEmail(String realmName, String userEmail) {
        List<UserRepresentation> userRepresentations = realmService.getRealmByName(realmName).users().searchByAttributes(userEmail);
        if (!userRepresentations.isEmpty()) {
            for (UserRepresentation usr : userRepresentations) {
                if (usr.getEmail().equals(userEmail)) {
                    usr.setRealmRoles(getUserRoleAsString(getUserRoleAvailable(realmName, usr.getId())));
                    usr.setGroups(getUserGroupAsString(getUserGroups(realmName, usr.getId())));
                    return usr;
                }
            }
        }
        return null;
    }

    public void killUserSession(DeleteUserSessionRequest request) {
        RealmResource realmResource = connection.getInstance().realm(request.getRealmName());
        realmResource.deleteSession(request.getSessionState());
    }

    public String sendVerificationLink(SendVerificationLinkRequest request) {
        UsersResource usersResource = connection.getInstance().realm(request.getRealmName()).users();
        usersResource.get(request.getUserId()).sendVerifyEmail();
        return SUCCESS;
    }

    public String sendResetPassword(SendResetPasswordRequest request) {
        UsersResource usersResource = connection.getInstance().realm(request.getRealmName()).users();
        usersResource.get(request.getUserId()).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
        return SUCCESS;
    }


}