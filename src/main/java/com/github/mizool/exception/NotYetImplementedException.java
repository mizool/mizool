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
package com.github.mizool.exception;

/**
 * Thrown to indicate that a method was not yet implemented by the developer. This is intended to be temporary and will
 * cause a deprecation warning during compilation. Thus, if there are no concrete plans to implement the method soon,
 * you should use the JDK {@link UnsupportedOperationException} instead (e.g. as with unmodifiable lists).
 */
public class NotYetImplementedException extends CodeInconsistencyException
{
    /**
     * @deprecated Deprecated to remind you to implement the corresponding code before releasing the software.
     */
    @Deprecated
    public NotYetImplementedException()
    {
    }

    /**
     * @deprecated Deprecated to remind you to implement the corresponding code before releasing the software.
     */
    @Deprecated
    public NotYetImplementedException(String message)
    {
        super(message);
    }
}
