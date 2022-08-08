/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kulchao.keycloak.idps.kujiale;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

/**
 *
 * @author admin
 */
public class KujialeUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[]{KujialeIdentityProviderFactory.PROVIDER_ID};

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "kujiale-user-attribute-mapper";
    }

}
