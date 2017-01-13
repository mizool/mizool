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
 * Thrown when the entity cannot be saved due to a conflict with the corresponding object in the database, e.g.
 * because the object that should be created already exists, or the object to be updated has conflicting changes.
 */
public class ConflictingEntityException extends RuntimeException
{
    public ConflictingEntityException()
    {
        super();
    }

    public ConflictingEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConflictingEntityException(String message)
    {
        super(message);
    }

    public ConflictingEntityException(Throwable cause)
    {
        super(cause);
    }
}