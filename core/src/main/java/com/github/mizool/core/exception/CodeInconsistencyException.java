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
package com.github.mizool.core.exception;

/**
 * Represents problems that arise purely from inconsistencies in the source code, as opposed to problems that are caused
 * by user or environment data.<br>
 * <br>
 * For example, this exception may be used to wrap an "impossible" {@link java.io.UnsupportedEncodingException} for the
 * encoding "UTF-8" (which is guaranteed to be supported by the Java specs). If it is thrown, a developer might have
 * accidently changed the encoding (= a code inconsistency).
 */
public class CodeInconsistencyException extends RuntimeException
{
    public CodeInconsistencyException()
    {
    }

    public CodeInconsistencyException(String message)
    {
        super(message);
    }

    public CodeInconsistencyException(Throwable cause)
    {
        super(cause);
    }

    public CodeInconsistencyException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
