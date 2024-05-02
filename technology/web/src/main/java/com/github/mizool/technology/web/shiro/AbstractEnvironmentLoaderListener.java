package com.github.mizool.technology.web.shiro;

import java.util.List;

import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.AuthorizingSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

import jakarta.servlet.ServletContext;

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
        Authorizer authorizer = getAuthorizer();
        if (authorizer != null)
        {
            securityManager.setAuthorizer(authorizer);
        }

        return environment;
    }
}
