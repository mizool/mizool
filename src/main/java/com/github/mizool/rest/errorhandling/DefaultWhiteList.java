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
package com.github.mizool.rest.errorhandling;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.MetaInfServices;

import com.github.mizool.exception.AuthenticationMissingException;
import com.github.mizool.exception.AuthenticationRejectedException;
import com.github.mizool.exception.BadRequestException;
import com.github.mizool.exception.ConflictingEntityException;
import com.github.mizool.exception.ObjectNotFoundException;
import com.github.mizool.exception.PermissionDeniedException;
import com.github.mizool.exception.UnprocessableEntityException;
import com.google.common.collect.ImmutableMap;

@MetaInfServices
public class DefaultWhiteList implements WhiteList
{
    private static final int SC_UNPROCESSABLE_ENTITY = 422;

    @Override
    public Map<String, Integer> getEntries()
    {
        return ImmutableMap.<String, Integer>builder()
            .put(BadRequestException.class.getName(), HttpServletResponse.SC_BAD_REQUEST)
            .put(ConflictingEntityException.class.getName(), HttpServletResponse.SC_CONFLICT)
            .put(ObjectNotFoundException.class.getName(), HttpServletResponse.SC_NOT_FOUND)
            .put(AuthenticationMissingException.class.getName(), HttpServletResponse.SC_UNAUTHORIZED)
            .put(AuthenticationRejectedException.class.getName(), HttpServletResponse.SC_UNAUTHORIZED)
            .put(PermissionDeniedException.class.getName(), HttpServletResponse.SC_FORBIDDEN)
            .put(UnprocessableEntityException.class.getName(), SC_UNPROCESSABLE_ENTITY)
            .build();
    }
}