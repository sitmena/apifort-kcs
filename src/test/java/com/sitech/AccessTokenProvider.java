package com.sitech;


/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public abstract class AccessTokenProvider {
//
//    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
//    String authServerUrl;
//    @ConfigProperty(name = "quarkus.oidc.client-id")
//    String clientId;
//
//    protected String getAccessToken(String username, String password) {
//        return given().contentType("application/x-www-form-urlencoded")
//                .formParams(Map.of(
//                        "username", username,
//                        "password", password,
//                        "grant_type", "password",
//                        "client_id", clientId,
//                        "client_secret", "secret"
//                ))
//                .post(authServerUrl + "/protocol/openid-connect/token")
//                .then().assertThat().statusCode(200)
//                .extract().path("access_token");
//    }
}