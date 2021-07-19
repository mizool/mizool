package com.github.mizool.core.function;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Functions
{
    public static <T> Consumer<T> first(Consumer<T> target)
    {
        return new Consumer<T>()
        {
            private boolean wasAccepted;

            @Override
            public void accept(T t)
            {
                if (!wasAccepted)
                {
                    wasAccepted = true;
                    target.accept(t);
                }
            }
        };
    }
}
