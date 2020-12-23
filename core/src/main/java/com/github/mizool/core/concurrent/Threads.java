/*
 * Copyright 2018-2020 incub8 Software Labs GmbH
 * Copyright 2018-2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.concurrent;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.UncheckedInterruptedException;

@UtilityClass
public class Threads
{
    /**
     * @throws UncheckedInterruptedException If the thread was interrupted.
     */
    public void sleep(long milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (InterruptedException e)
        {
            rethrowInterrupt(e);
        }
    }

    /**
     * Wraps the given {@link InterruptedException} in an {@link UncheckedInterruptedException} and re-interrupts the
     * thread.
     *
     * @throws UncheckedInterruptedException Wrapping the given {@link InterruptedException}.
     */
    public void rethrowInterrupt(InterruptedException e)
    {
        Thread.currentThread()
            .interrupt();
        throw new UncheckedInterruptedException(e);
    }
}
