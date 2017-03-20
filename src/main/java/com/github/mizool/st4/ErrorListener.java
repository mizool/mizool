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
package com.github.mizool.st4;

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

import com.github.mizool.exception.CodeInconsistencyException;

public class ErrorListener implements STErrorListener
{
    @Override
    public void compileTimeError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void runTimeError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void IOError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void internalError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }
}

