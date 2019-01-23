/**
 * Copyright 2017-2019 incub8 Software Labs GmbH
 * Copyright 2017-2019 protel Hotelsoftware GmbH
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
package com.github.mizool.core.rest.errorhandling;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.AuthenticationMissingException;
import com.github.mizool.core.exception.AuthenticationRejectedException;
import com.github.mizool.core.exception.BadRequestException;
import com.github.mizool.core.exception.ConflictingEntityException;
import com.github.mizool.core.exception.InvalidBackendReplyException;
import com.github.mizool.core.exception.LockedEntityException;
import com.github.mizool.core.exception.MethodNotAllowedException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.PermissionDeniedException;
import com.github.mizool.core.exception.UnprocessableEntityException;
import com.github.mizool.core.exception.UnsupportedHttpFeatureException;
import com.google.common.collect.ImmutableSet;

@MetaInfServices
public class DefaultWhiteList implements WhiteList
{
    @Override
    public Set<WhiteListEntry> getEntries()
    {
        return ImmutableSet.<WhiteListEntry>builder().add(new WhiteListEntry(BadRequestException.class,
            HttpServletResponse.SC_BAD_REQUEST,
            true))
            .add(new WhiteListEntry(UnprocessableEntityException.class, HttpStatus.UNPROCESSABLE_ENTITY, true))
            .add(new WhiteListEntry(ConflictingEntityException.class, HttpServletResponse.SC_CONFLICT))
            .add(new WhiteListEntry(ObjectNotFoundException.class, HttpServletResponse.SC_NOT_FOUND))
            .add(new WhiteListEntry(AuthenticationMissingException.class, HttpServletResponse.SC_UNAUTHORIZED))
            .add(new WhiteListEntry(AuthenticationRejectedException.class, HttpServletResponse.SC_UNAUTHORIZED))
            .add(new WhiteListEntry(PermissionDeniedException.class, HttpServletResponse.SC_FORBIDDEN))
            .add(new WhiteListEntry(UnsupportedHttpFeatureException.class, HttpServletResponse.SC_NOT_IMPLEMENTED))
            .add(new WhiteListEntry(LockedEntityException.class, HttpStatus.LOCKED))
            .add(new WhiteListEntry(MethodNotAllowedException.class, HttpServletResponse.SC_METHOD_NOT_ALLOWED))
            .add(new WhiteListEntry(InvalidBackendReplyException.class, HttpServletResponse.SC_BAD_GATEWAY))
            .build();
    }
}