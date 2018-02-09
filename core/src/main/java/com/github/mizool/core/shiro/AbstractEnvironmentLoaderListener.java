/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.core.shiro;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.AuthorizingSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

public abstract class AbstractEnvironmentLoaderListener extends EnvironmentLoaderListener
{
    protected abstract List<Realm> getRealms();

    protected abstract Authorizer getAuthorizer();

    @Override
    protected WebEnvironment createEnvironment(ServletContext pServletContext)
    {
        WebEnvironment environment = super.createEnvironment(pServletContext);
        AuthorizingSecurityManager securityManager = (AuthorizingSecurityManager) environment.getSecurityManager();
        securityManager.setRealms(getRealms());
        securityManager.setAuthorizer(getAuthorizer());

        return environment;
    }
}