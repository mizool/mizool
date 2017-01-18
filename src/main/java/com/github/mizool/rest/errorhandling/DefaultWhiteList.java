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