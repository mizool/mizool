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
package com.github.mizool.core.concurrent;

import java.util.function.BooleanSupplier;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.UncheckedInterruptedException;

@UtilityClass
public class Threads
{
    /**
     * @throws UncheckedInterruptedException If the thread was interrupted.
     */
    public void sleep(int milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (@SuppressWarnings("squid:S2142") InterruptedException e)
        {
            rethrowInterrupt(e);
        }
    }

    /**
     * @throws UncheckedInterruptedException If the thread was interrupted.
     * @throws IllegalMonitorStateException If the current thread is not the owner of the object's monitor
     */
    @SuppressWarnings({ "squid:S2273" })
    public void waitUntil(BooleanSupplier state, Object semaphore)
    {
        try
        {
            while (!state.getAsBoolean())
            {
                semaphore.wait();
            }
        }
        catch (@SuppressWarnings("squid:S2142") InterruptedException e)
        {
            rethrowInterrupt(e);
        }
    }

    /**
     * Wraps the given {@link InterruptedException} in an {@link UncheckedInterruptedException} and re-interrupts the
     * thread.<br>
     * <br>
     * Remember to add {@code @SuppressWarnings("squid:S2142") } to the catch clause. Otherwise, Sonar will complain
     * about not interrupting the thread.
     *
     * @throws UncheckedInterruptedException Wrapping the given {@link InterruptedException}.
     */
    public void rethrowInterrupt(InterruptedException e)
    {
        Thread.currentThread().interrupt();
        throw new UncheckedInterruptedException(e);
    }
}
