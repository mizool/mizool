package com.github.mizool.core.rest.errorhandling;

import javax.servlet.http.HttpServletResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpStatus
{
    public final int OK = HttpServletResponse.SC_OK;
    public final int ACCEPTED = HttpServletResponse.SC_ACCEPTED;
    public final int NO_CONTENT = HttpServletResponse.SC_NO_CONTENT;
    public final int BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;
    public final int UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;
    public final int FORBIDDEN = HttpServletResponse.SC_FORBIDDEN;
    public final int NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
    public final int METHOD_NOT_ALLOWED = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
    public final int CONFLICT = HttpServletResponse.SC_CONFLICT;
    public final int UNPROCESSABLE_ENTITY = 422;
    public final int LOCKED = 423;
    public final int INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    public final int NOT_IMPLEMENTED = HttpServletResponse.SC_NOT_IMPLEMENTED;
    public final int BAD_GATEWAY = HttpServletResponse.SC_BAD_GATEWAY;
    public final int SERVICE_UNAVAILABLE = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
}
