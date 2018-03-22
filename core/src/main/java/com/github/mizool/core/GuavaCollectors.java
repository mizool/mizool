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
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuavaCollectors
{
    private static class CollectorImpl<T, A, R> implements Collector<T, A, R>
    {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

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

    public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toImmutableList()
    {
        return new CollectorImpl<>(
            ImmutableList::builder,
            ImmutableList.Builder::add,
            (thisBuilder, otherBuilder) -> thisBuilder.addAll(otherBuilder.build()),
            ImmutableList.Builder::build);
    }

    public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> toImmutableSet()
    {
        return new CollectorImpl<>(
            ImmutableSet::builder,
            ImmutableSet.Builder::add,
            (thisBuilder, otherBuilder) -> thisBuilder.addAll(otherBuilder.build()),
            ImmutableSet.Builder::build);
    }

    public static <T, K, U> Collector<T, ImmutableMap.Builder<K, U>, ImmutableMap<K, U>> toImmutableMap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return new CollectorImpl<>(
            ImmutableMap::builder,
            (builder, element) -> builder.put(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableMap.Builder::build);
    }

    public static <T, K, U> Collector<T, ImmutableMultimap.Builder<K, U>, ImmutableMultimap<K, U>> toImmutableMultimap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends Iterable<U>> valueMapper)
    {
        return new CollectorImpl<>(
            ImmutableMultimap::builder,
            (builder, element) -> builder.putAll(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableMultimap.Builder::build);
    }

    public static <T, K, U> Collector<T, ImmutableListMultimap.Builder<K, U>, ImmutableListMultimap<K, U>> toImmutableListMultimap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends List<U>> valueMapper)
    {
        return new CollectorImpl<>(
            ImmutableListMultimap::builder,
            (builder, element) -> builder.putAll(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableListMultimap.Builder::build);
    }

    public static <T, K, U> Collector<T, ImmutableSetMultimap.Builder<K, U>, ImmutableSetMultimap<K, U>> toImmutableSetMultimap(
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends Set<U>> valueMapper)
    {
        return new CollectorImpl<>(
            ImmutableSetMultimap::builder,
            (builder, element) -> builder.putAll(keyMapper.apply(element), valueMapper.apply(element)),
            (thisBuilder, otherBuilder) -> thisBuilder.putAll(otherBuilder.build()),
            ImmutableSetMultimap.Builder::build);
    }
}