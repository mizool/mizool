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
package com.github.mizool.core.rest.errorhandling;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.AuthenticationMissingException;
import com.github.mizool.core.exception.AuthenticationRejectedException;
import com.github.mizool.core.exception.BadRequestException;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.LockedException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.PermissionDeniedException;
import com.github.mizool.core.exception.UnprocessableEntityException;
import com.github.mizool.core.exception.UnsupportedHttpFeatureException;
import com.google.common.collect.ImmutableSet;

@MetaInfServices
public class DefaultWhiteList implements WhiteList
{
    private static final int SC_UNPROCESSABLE_ENTITY = 422;
    private static final int LOCKED = 423;

    @Override
    public Set<WhiteListEntry> getEntries()
    {
        return ImmutableSet.<WhiteListEntry>builder()
            .add(new WhiteListEntry(BadRequestException.class.getName(), HttpServletResponse.SC_BAD_REQUEST, true))
            .add(
                new WhiteListEntry(UnprocessableEntityException.class.getName(), SC_UNPROCESSABLE_ENTITY, true))
            .add(
                new WhiteListEntry(
                    ConflictingEntityException.class.getName(), HttpServletResponse.SC_CONFLICT))
            .add(new WhiteListEntry(ObjectNotFoundException.class.getName(), HttpServletResponse.SC_NOT_FOUND))
            .add(
                new WhiteListEntry(
                    AuthenticationMissingException.class.getName(), HttpServletResponse.SC_UNAUTHORIZED))
            .add(
                new WhiteListEntry(
                    AuthenticationRejectedException.class.getName(), HttpServletResponse.SC_UNAUTHORIZED))
            .add(new WhiteListEntry(PermissionDeniedException.class.getName(), HttpServletResponse.SC_FORBIDDEN))
            .add(
                new WhiteListEntry(
                    UnsupportedHttpFeatureException.class.getName(), HttpServletResponse.SC_NOT_IMPLEMENTED))
            .add(new WhiteListEntry(LockedException.class.getName(), LOCKED))
            .build();
    }
}