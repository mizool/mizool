package com.github.mizool.technology.sql.jooq;

import lombok.experimental.UtilityClass;

import org.jooq.CloseableDSLContext;
import org.jooq.impl.DefaultCloseableDSLContext;

@UtilityClass
public class DslContexts
{
    public CloseableDSLContext obtain(MizoolConnectionProvider mizoolConnectionProvider)
    {
        return new DefaultCloseableDSLContext(mizoolConnectionProvider, mizoolConnectionProvider.getSqlDialect());
    }
}
