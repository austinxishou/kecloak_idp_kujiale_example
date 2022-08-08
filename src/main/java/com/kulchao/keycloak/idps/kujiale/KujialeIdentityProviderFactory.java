/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kulchao.keycloak.idps.kujiale;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 *
 * @author admin
 */
public class KujialeIdentityProviderFactory extends AbstractIdentityProviderFactory<KujialeIdentityProvider>
        implements SocialIdentityProviderFactory<KujialeIdentityProvider> {

    public static final String PROVIDER_ID = "kujiale";

    @Override
    public String getName() {
        return "Kujiale";
    }

    @Override
    public KujialeIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new KujialeIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }

    @Override
    public IdentityProviderModel createConfig() {
        return new OAuth2IdentityProviderConfig();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
