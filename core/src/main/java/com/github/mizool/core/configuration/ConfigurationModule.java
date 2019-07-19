package com.github.mizool.core.configuration;

import org.kohsuke.MetaInfServices;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

@MetaInfServices(Module.class)
public class ConfigurationModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        super.configure();
        bind(Configuration.class).to(EnvironmentVariableConfigurationImpl.class);
    }
}
