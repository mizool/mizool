package com.github.mizool.shiro;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

public abstract class AbstractEnvironmentLoaderListener extends EnvironmentLoaderListener
{
    public abstract List<Realm> getRealms();

    @Override
    protected WebEnvironment createEnvironment(ServletContext pServletContext)
    {
        WebEnvironment environment = super.createEnvironment(pServletContext);
        RealmSecurityManager realmSecurityManager = (RealmSecurityManager) environment.getSecurityManager();

        realmSecurityManager.setRealms(getRealms());
        return environment;
    }
}