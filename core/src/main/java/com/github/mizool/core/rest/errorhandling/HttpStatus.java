/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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