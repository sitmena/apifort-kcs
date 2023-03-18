package com.sitech.service;


import com.sitech.exception.ApiFortException;
import com.sitech.exception.DataConflictException;
import com.sitech.users.*;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;
import com.sitech.exception.ErrorResponse;

@ApplicationScoped
@Slf4j
public class UserService {

    private static final String SUCCESS = "success";
    public static final String NOT_FOUND = " Not Found";

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
        Response response = realmService.getRealmByName(request.getRealmName()).users().create(user);
        int responseCode =  response.getStatus();
        if (responseCode == HttpStatus.SC_CREATED) {
            if (StringUtils.isNoneEmpty(request.getRole())) {
                assignRoleToUser(request.getRealmName(), request.getUserName(), request.getRole());
            }
            if (StringUtils.isNoneEmpty(request.getGroup())) {
                addUserToGroup(request.getRealmName(), request.getUserName(), request.getGroup());
            }
        }else{
            exceptionHandler(401 , "UnAuthorize");
        }
        return response;
    }

    public UserRepresentation getUserById(String realmName, String userId) {
        UserRepresentation user = null;
        try {
            UserResource userRepresentations = realmService.getRealmByName(realmName).users().get(userId);
            user = userRepresentations.toRepresentation();
            user.setRealmRoles(getUserRoleAsString(getUserRoleAvailable(realmName, userId)));
            user.setGroups(getUserGroupAsString(getUserGroups(realmName, userId)));
        } catch (Exception ex) {
            exceptionHandler(404, ex.getMessage());
        }
        return user;
    }


    public UserResource getUserResourceById(String realmName, String userId) {
        UserResource userRepresentations = null;
        try {
            userRepresentations = realmService.getRealmByName(realmName).users().get(userId);
        } catch (Exception ex) {
            exceptionHandler(404, ex.getMessage());
        }
        return userRepresentations;
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
        exceptionHandler(404, "User ".concat(userName).concat("  Not Found"));
        return null;
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
        exceptionHandler(404, "Email ".concat(userEmail).concat(NOT_FOUND));
        return null;
    }


    private void assignRoleToUser(String realmName, String userName, String realmRole) {
        List<String> roleLst = Arrays.stream(realmRole.split(",")).toList();
        try{
            for (String role : roleLst) {
                RealmResource realm = realmService.getRealmByName(realmName);
                RoleRepresentation realmRoleRepresentation = realm.roles().get(role).toRepresentation();
                List<UserRepresentation> userRepresentations = realm.users().list();
                for (UserRepresentation user : userRepresentations) {
                    if (user.getUsername().equalsIgnoreCase(userName)) {
                        UserResource userResource1 = realm.users().get(user.getId());
                        userResource1.roles().realmLevel().add(Arrays.asList(realmRoleRepresentation));
                    }
                }
            }
        }catch (NotFoundException ex) {
            exceptionHandler(404, "Role ".concat(realmRole).concat(NOT_FOUND));
        }
    }

    public List<GroupRepresentation> getUserGroups(String realmName, String userId) {
        List<GroupRepresentation> userGroups = null;
        try {
            userGroups = realmService.getRealmByName(realmName).users().get(userId).groups();
        } catch (Exception ex) {
            exceptionHandler(404, "No Group assigned to the User ".concat(userId));
        }
        return userGroups;
    }

    public List<RoleRepresentation> getUserRoleEffective(String realmName, String userId) {
        List<RoleRepresentation> roleEffective = null;
        try {
            roleEffective = realmService.getRealmByName(realmName).users().get(userId).roles().realmLevel().listEffective();
        } catch (Exception ex) {
            exceptionHandler(404, "No Effective Role available for User ".concat(userId));
        }
        return roleEffective;
    }

    public List<RoleRepresentation> getUserRoleAvailable(String realmName, String userId) {
        List<RoleRepresentation> roleAvailable = null;
        try {
            roleAvailable = realmService.getRealmByName(realmName).users().get(userId).roles().realmLevel().listAvailable();
        } catch (Exception ex) {
            exceptionHandler(404, "No Role available for User ".concat(userId));
        }
        return roleAvailable;
    }


    public List<UserRepresentation> findAllUsersInGroup(String realmName, String groupName) {
        List<GroupRepresentation> groupRepresentations = realmService.getRealmByName(realmName).groups().groups();
        for (GroupRepresentation group : groupRepresentations) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return realmService.getRealmByName(realmName).groups().group(group.getId()).members();
            }
        }
        exceptionHandler(404, "Group ".concat(groupName).concat(" doesn't have any member"));
        return new ArrayList<>();
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
        exceptionHandler(404, "Group ".concat(groupName).concat(NOT_FOUND));
        return null;
    }

    public Collection<UserRepresentation> findUserByRole(String realmName, String roleName) {
        RoleResource roleResource = null;
        Set<UserRepresentation> userRepresentations = null;
        try {
            roleResource = realmService.getRealmByName(realmName).roles().get(roleName);
            userRepresentations = roleResource.getRoleUserMembers();
        } catch (Exception ex) {
            exceptionHandler(404, "No User For ".concat(roleName).concat(" Role"));
        }
        return userRepresentations;
    }

    public String addUserRole(String realmName, String userName, String userRole) {
        UsersResource userResource = getUsers(realmName);
        UserRepresentation usr = getUserByUserName(realmName, userName);
        List<RoleRepresentation> roleRepresentations = getUserRoleAvailable(realmName, usr.getId());
        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            if (roleRepresentation.getName().equals(userRole)) {
                userResource.get(usr.getId()).roles().realmLevel().add(Arrays.asList(roleRepresentation));
                return SUCCESS;
            }
        }
        exceptionHandler(404, " Role ".concat(userRole).concat(" Not Available to added to User ").concat(userName));
        return null;
    }


    public UserRepresentation updateUser(UpdateUserRequest updateUserRequest) {

        UserResource userResource = getUserResourceById(updateUserRequest.getRealmName(),updateUserRequest.getUserId());
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
            RoleRepresentation testerRealmRole = realmService.getRealmByName(updateUserRequest.getRealmName()).roles().get(updateUserRequest.getRole()).toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));

        }
        userResource.update(userRepresentation);
        return realmService.getRealmByName(updateUserRequest.getRealmName()).users().get(updateUserRequest.getUserId()).toRepresentation();
    }

    public String updateUserPassword(UpdateUserPasswordRequest request) {
        UserResource userResource = getUserResourceById(request.getRealmName(),request.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        userRepresentation.setCredentials(Arrays.asList(credential));
        userResource.update(userRepresentation);
        return SUCCESS;
    }

    public List<UserRepresentation> getAllUsersByRealm(GetUsersRequest request) {

        if (request == null) {
            exceptionHandler(400, "request cannot be null");
            return Collections.emptyList();
        }

        List<UserRepresentation> userRepresentations;
        if (request.getFrom() >= 0 && request.getSize() > 0 ) {
            userRepresentations = realmService.getRealmByName(request.getRealmName()).users().list(request.getFrom(), request.getSize());
        } else {
            userRepresentations = realmService.getRealmByName(request.getRealmName()).users().list();
        }

        if (userRepresentations.isEmpty()) {
            exceptionHandler(404, "Realm Users is Empty");
            return Collections.emptyList();
        }

        List<UserRepresentation> users = new ArrayList<>();
        for (UserRepresentation usr : userRepresentations) {
            usr.setRealmRoles(getUserRoleAsString(getUserRoleEffective(request.getRealmName(), usr.getId())));
            usr.setGroups(getUserGroupAsString(getUserGroups(request.getRealmName(), usr.getId())));
            users.add(usr);
        }

        return users;
    }

    public UserRepresentation updateUserAttributes(updateUserAttributesRequest request) {
        UserResource userResource = getUserResourceById(request.getRealmName(),request.getUserId());
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
        return getUserById(request.getRealmName() , request.getUserId());
    }

    public void killUserSession(DeleteUserSessionRequest request) {
        realmService.getRealmByName(request.getRealmName()).deleteSession(request.getSessionState());
    }

    public String sendVerificationLink(SendVerificationLinkRequest request) {
        UsersResource usersResource = getUsers(request.getRealmName());
        usersResource.get(request.getUserId()).sendVerifyEmail();
        return SUCCESS;
    }

    public String sendResetPassword(SendResetPasswordRequest request) {
        UsersResource usersResource = getUsers(request.getRealmName());
        usersResource.get(request.getUserId()).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
        return  SUCCESS;
    }

    private void exceptionHandler(int statusCode, String exceptionMessage) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(statusCode);
        errorResponse.setErrorMessage(exceptionMessage);

        switch (statusCode) {
            case 401:
                throw new UnauthorizedException(errorResponse.toString());
            case 404:
                throw new NotFoundException(errorResponse.toString());
            case 409:
                throw new DataConflictException(errorResponse.toString());
            default:
                throw new ApiFortException(errorResponse.toString());
        }
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

    private UsersResource getUsers(String realmName) {
        UsersResource userResource = null;
        try {
            RealmResource realmResource = realmService.getRealmByName(realmName);
            userResource = realmResource.users();
        } catch (Exception ex) {
            exceptionHandler(404, "No Users In ".concat(realmName));
        }
        return userResource;
    }

}