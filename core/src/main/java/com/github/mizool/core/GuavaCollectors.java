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
package com.github.mizool.core;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuavaCollectors
{
    private static final class CollectorImpl<T, A, R> implements Collector<T, A, R>
    {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final ImmutableSet<Characteristics> characteristics;

        private CollectorImpl(
            Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher)
        {
            this(supplier, accumulator, combiner, finisher, ImmutableSet.of());
        }

        private CollectorImpl(
            Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner,
            Function<A, R> finisher,
            Set<Characteristics> characteristics)
        {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = ImmutableSet.copyOf(characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator()
        {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier()
        {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner()
        {
            return combiner;
        }

        @Override
        public Function<A, R> finisher()
        {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics()
        {
            return characteristics;
        }
    }

    /**
     * @deprecated Use {@link ImmutableList#toImmutableList()} instead.
     */
    @Deprecated
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList()
    {
        return ImmutableList.toImmutableList();
    }

    /**
     * @deprecated Use {@link ImmutableSet#toImmutableSet()} instead.
     */
    @Deprecated
    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet()
    {
        return ImmutableSet.toImmutableSet();
    }

    /**
     * @deprecated Use {@link ImmutableMap#toImmutableMap(Function, Function)} instead.
     */
    @Deprecated
    public static <T, K, U> Collector<T, ?, ImmutableMap<K, U>> toImmutableMap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return ImmutableMap.toImmutableMap(keyMapper, valueMapper);
    }

    /**
     * @deprecated Use {@link ImmutableListMultimap#toImmutableListMultimap(Function, Function)} instead. This will
     * require some refactoring as it expects the value mapper to map one value instead of a list.
     */
    @Deprecated
    public static <T, K, U> Collector<T, ImmutableListMultimap.Builder<K, U>, ImmutableListMultimap<K, U>> toImmutableListMultimap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends List<U>> valueMapper)
    {
        return new CollectorImpl<>(ImmutableListMultimap::builder,
            (builder, element) -> builder.putAll(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableListMultimap.Builder::build);
    }

    /**
     * @deprecated Use {@link ImmutableSetMultimap#toImmutableSetMultimap(Function, Function)} instead. This will
     * require some refactoring as it expects the value mapper to map one value instead of a list.
     */
    @Deprecated
    public static <T, K, U> Collector<T, ImmutableSetMultimap.Builder<K, U>, ImmutableSetMultimap<K, U>> toImmutableSetMultimap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends Set<U>> valueMapper)
    {
        return new CollectorImpl<>(ImmutableSetMultimap::builder,
            (builder, element) -> builder.putAll(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableSetMultimap.Builder::build);
    }
}