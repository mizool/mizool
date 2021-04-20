/**
 * Copyright 2021 incub8 Software Labs GmbH
 * Copyright 2021 protel Hotelsoftware GmbH
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
package com.github.mizool.core.exception;

/**
 * Thrown when there is an error during a {@link java.util.stream.Stream} reduction operation.
 */
public class ReductionException extends RuntimeException
{
    public ReductionException()
    {
        super();
    }

    public ReductionException(String message)
    {
        super(message);
    }

    public ReductionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ReductionException(Throwable cause)
    {
        super(cause);
    }
}