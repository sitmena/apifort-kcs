package com.sitech.mapper;

import com.sitech.dto.Dto;
import com.sitech.dto.Dto.RealmDto;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class DtoMapper {

    public List<RealmDto> toRealmList(List<RealmRepresentation> realmRepresentations) {
        return realmRepresentations.stream().map(
                result -> Dto.RealmDto.newBuilder()
                        .setId(result.getId())
                        .setRealm(result.getRealm())
                        .setDisplayName(ObjectUtils.isEmpty(result.getDisplayName()) ? "" : result.getDisplayName())
                        .setEnabled(result.isEnabled()).build()
        ).collect(Collectors.toList());
    }

    public List<Dto.UserDto> toUserList(List<UserRepresentation> userRepresentations) {
        return userRepresentations.stream().map(
                result -> Dto.UserDto.newBuilder()
                        .setId(result.getId())
                        .setCreatedTimestamp(result.getCreatedTimestamp())
                        .setUsername(ObjectUtils.isEmpty(result.getUsername()) ? "" : result.getUsername())
                        .setEnabled(ObjectUtils.isEmpty(result.isEnabled()) ? false : result.isEnabled())
                        .setFirstName(ObjectUtils.isEmpty(result.getFirstName())  ? "" : result.getFirstName())
                        .setLastName(ObjectUtils.isEmpty(result.getLastName()) ? "" : result.getLastName())
                        .setEmail(ObjectUtils.isEmpty(result.getEmail()) ? "" : result.getEmail())
                        .setRole(ObjectUtils.isEmpty(result.getRealmRoles()) ? "" : String.join(",", result.getRealmRoles()))
                        .setGroup(ObjectUtils.isEmpty(result.getGroups()) ? "" : String.join(",", result.getGroups()))
                        .putAllAttributes(attributeConverter(result.getAttributes()))
                        .build()
        ).collect(Collectors.toList());
    }

    public List<Dto.GroupDto> toGroupList(List<GroupRepresentation> groupRepresentations) {
        return groupRepresentations.stream().map(
                result -> Dto.GroupDto.newBuilder()
                        .setId(result.getId())
                        .setName(result.getName())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<Dto.ClientDto> toClientList(List<ClientRepresentation> clientRepresentations) {
        return clientRepresentations.stream().map(
                result -> Dto.ClientDto.newBuilder()
                        .setId(result.getId())
                        .setName(result.getName())
                        .setEnabled(result.isEnabled())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<Dto.RoleDto> toRoleList(List<RoleRepresentation> roleRepresentations) {
        return roleRepresentations.stream().map(
                result -> Dto.RoleDto.newBuilder()
                        .setId(result.getId())
                        .setName(result.getName())
                        .setDescription(ObjectUtils.isEmpty(result.getDescription()) ? "" : result.getDescription())
                        .build()
        ).collect(Collectors.toList());
    }

    public Dto.GroupDto toGroup(GroupRepresentation group) {
        return Dto.GroupDto.newBuilder()
                .setId(group.getId())
                .setName(group.getName())
                .build();
    }

    public RealmDto toRealmDto(RealmRepresentation realmRepresentation) {
        RealmDto realmDto = RealmDto.newBuilder().setId(realmRepresentation.getId())
                .setRealm(realmRepresentation.getRealm())
                .setDisplayName(ObjectUtils.isEmpty(realmRepresentation.getDisplayName()) ? "" : realmRepresentation.getDisplayName())
                .setEnabled(realmRepresentation.isEnabled()).build();
        return realmDto;
    }

    public Dto.UserDto toUser(UserRepresentation result) {
        return Dto.UserDto.newBuilder()
                .setId(result.getId())
                .setCreatedTimestamp(result.getCreatedTimestamp())
                .setUsername(ObjectUtils.isEmpty(result.getUsername()) ? "" : result.getUsername())
                .setEnabled(ObjectUtils.isEmpty(result.isEnabled().toString()) ? false : result.isEnabled())
                .setFirstName(ObjectUtils.isEmpty(result.getFirstName())  ? "" : result.getFirstName())
                .setLastName(ObjectUtils.isEmpty(result.getLastName()) ? "" : result.getLastName())
                .setEmail(ObjectUtils.isEmpty(result.getEmail()) ? "" : result.getEmail())
                .setRole(ObjectUtils.isEmpty(result.getRealmRoles()) ? "" : String.join(",", result.getRealmRoles()))
                .setGroup(ObjectUtils.isEmpty(result.getGroups()) ? "" : String.join(",", result.getGroups()))
                .putAllAttributes(attributeConverter(result.getAttributes()))
                .build();
    }

    private Map<String, String> attributeConverter(Map<String, List<String>> attributes) {
        Map<String, String> att = new HashMap<String, String>();
        if (!Objects.isNull(attributes) && !attributes.isEmpty()) {
            for (var entry : attributes.entrySet()) {
                String value = String.join(",", entry.getValue());
                att.put(entry.getKey(), value);
            }
        }
        return att;
    }

    public Dto.UserAccessTokenDto toUserAccessTokenDto(AccessTokenResponse accessTokenResponse) {
        return Dto.UserAccessTokenDto.newBuilder()
                .setToken(accessTokenResponse.getToken() != null ? accessTokenResponse.getToken() : "")
                .setExpiresIn(Objects.isNull(accessTokenResponse.getExpiresIn()) ? 0 : accessTokenResponse.getExpiresIn())
                .setRefreshExpiresIn(accessTokenResponse.getRefreshExpiresIn())
                .setRefreshToken(Objects.isNull(accessTokenResponse.getRefreshToken()) ? "" : accessTokenResponse.getRefreshToken())
                .setTokenType(Objects.isNull(accessTokenResponse.getTokenType()) ? "" : accessTokenResponse.getTokenType())
                .setIdToken(Objects.isNull(accessTokenResponse.getIdToken()) ? "" : accessTokenResponse.getIdToken())
                .setNotBeforePolicy(Objects.isNull(accessTokenResponse.getNotBeforePolicy()) ? 0 : accessTokenResponse.getNotBeforePolicy())
                .setSessionState(Objects.isNull(accessTokenResponse.getSessionState()) ? "" : accessTokenResponse.getSessionState())
                .setScope(Objects.isNull(accessTokenResponse.getScope()) ? "" : accessTokenResponse.getScope())
                .setError(Objects.isNull(accessTokenResponse.getError()) ? "" : accessTokenResponse.getError())
                .setErrorDescription(Objects.isNull(accessTokenResponse.getErrorDescription()) ? "" : accessTokenResponse.getErrorDescription())
                .setErrorUri(Objects.isNull(accessTokenResponse.getErrorUri()) ? "" : accessTokenResponse.getErrorUri())
                .build();
    }
}
