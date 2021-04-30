/**
 * Copyright 2019 incub8 Software Labs GmbH
 * Copyright 2019 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.technology.web.healthcheck;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.CloseableDSLContext;
import org.jooq.exception.DataAccessException;

@Slf4j
@RequiredArgsConstructor
public class JooqConnectivityCheck implements Check
{
    private final Supplier<CloseableDSLContext> dslContextSupplier;
    private final String name;

    @Override
    public CheckResult perform()
    {
        CheckResult.CheckResultBuilder resultBuilder = CheckResult.builder().name(name);

        try (CloseableDSLContext dslContext = dslContextSupplier.get())
        {
            dslContext.selectOne().execute();
            resultBuilder = resultBuilder.success(true).message("OK");
        }
        catch (DataAccessException e)
        {
            log.debug("Error checking the connection to " + name, e);
            resultBuilder = resultBuilder.success(false).message("Connection problem");
        }

        return resultBuilder.build();
    }
}