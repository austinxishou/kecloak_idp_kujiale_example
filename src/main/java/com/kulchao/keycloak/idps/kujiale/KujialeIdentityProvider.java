/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kulchao.keycloak.idps.kujiale;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

/**
 *
 * @author admin
 */
public class KujialeIdentityProvider extends AbstractOAuth2IdentityProvider implements SocialIdentityProvider {

    public static final String AUTH_URL = "https://oauth.kujiale.com/oauth2/show";
    public static final String TOKEN_URL = "https://oauth.kujiale.com/oauth2/auth/token";
    public static final String OPENID_URL = "https://oauth.kujiale.com/oauth2/auth/user";
    public static final String PROFILE_URL = "https://oauth.kujiale.com/oauth2/openapi/user";
    public static final String DEFAULT_SCOPE = "get_user_info";

    public static final String OAUTH2_PARAMETER_ACCESS_TOKEN = "accessToken";

    public KujialeIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }

    /**
     * 提取酷家乐用户信息，转换为keycloak用户实体
     *
     * @param event
     * @param profile
     * @return
     */
    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {

        if (!profile.has("d") || profile.get("d").isEmpty()) {
            throw new NullPointerException("kujiale idp user info response is null " + profile.toString());
        }

        JsonNode userNode = profile.get("d");

        String openId = getJsonProperty(userNode, "openId");
        if (openId == null || openId.isEmpty()) {
            throw new NullPointerException("kujiale idp user info is null " + userNode.asText());
        }

        BrokeredIdentityContext user = new BrokeredIdentityContext(openId);
        String userName = userNode.get("userName").asText();

        user.setUsername(userName);
        user.setFirstName(userName);
        user.setIdpConfig(getConfig());
        user.setIdp(this);
        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, userNode, getConfig().getAlias());
        return user;
    }

    /**
     * 构建获取酷家乐用户信息的请求 暂不支持
     *
     * @param subjectToken
     * @param userInfoUrl
     * @return
     */
    @Override
    protected SimpleHttp buildUserInfoRequest(String subjectToken, String userInfoUrl) {
        return SimpleHttp.doGet(userInfoUrl, session)
                .header("Authorization", "Bearer " + subjectToken);
    }

    /**
     * 获取酷家乐用户信息
     *
     * @param response
     * @return
     */
    @Override
    public BrokeredIdentityContext getFederatedIdentity(String response) {

        String accessToken;
        try {
            JsonNode resNode = asJsonNode(response);
            accessToken = extractTokenFromResponse(resNode.get("d").toString(), OAUTH2_PARAMETER_ACCESS_TOKEN);
        } catch (Exception ex) {
            throw new IdentityBrokerException("No access token available in OAuth server response: " + response);
        }

        if (accessToken == null) {
            throw new IdentityBrokerException("No access token available in OAuth server response: " + response);
        }

        BrokeredIdentityContext context = doGetFederatedIdentity(accessToken);
        context.getContextData().put(FEDERATED_ACCESS_TOKEN, accessToken);
        return context;
    }

    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        BrokeredIdentityContext context = null;

        try {
            JsonNode openidResponse = generateOpenIdRequest(accessToken).asJson();
            String openId = openidResponse.get("d").asText();
            if (openId == null) {
                throw new Exception("Can not get openId from kujiale IDP");
            }
            JsonNode profile = generateUserInfoRequest(accessToken, openId).asJson();
            context = extractIdentityFromProfile(null, profile);
        } catch (Exception ex) {
            logger.warn("Cannot GetFederatedIdentity from kujiale IDP, error " + ex.getMessage());
        }

        return context;
    }

    public SimpleHttp generateOpenIdRequest(String accessToken) {
        SimpleHttp openIdRequest = SimpleHttp.doGet(OPENID_URL, session)
                .param("access_token", accessToken);
        return openIdRequest;
    }

    public SimpleHttp generateUserInfoRequest(String accessToken, String openId) {
        SimpleHttp openIdRequest = SimpleHttp.doGet(PROFILE_URL, session)
                .param("open_id", openId)
                .param("access_token", accessToken);
        return openIdRequest;
    }

}
