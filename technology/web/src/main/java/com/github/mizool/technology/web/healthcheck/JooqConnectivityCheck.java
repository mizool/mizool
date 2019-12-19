package com.github.mizool.technology.web.healthcheck;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

@Slf4j
@RequiredArgsConstructor
public class JooqConnectivityCheck implements Check
{
    private final Supplier<DSLContext> dslContextSupplier;
    private final String name;

    @Override
    public CheckResult perform()
    {
        CheckResult.CheckResultBuilder resultBuilder = CheckResult.builder().name(name);

        try (DSLContext dslContext = dslContextSupplier.get())
        {
            dslContext.selectOne().execute();
            resultBuilder = resultBuilder.success(true).message("OK");
        }
        catch (DataAccessException e)
        {
            log.debug("Error checking the connecting to " + name, e);
            resultBuilder = resultBuilder.success(false).message("Connection problem");
        }

        return resultBuilder.build();
    }
}