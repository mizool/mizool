/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.configuration;

/**
 * An object that allows retrieving {@link PropertyNode} children.
 */
public interface HasChildren
{
    /**
     * Returns the child {@link PropertyNode} with the given name. Until a value is retrieved, no check is made for the
     * existence of any property.<br>
     * <br>
     * <b>Example:</b> For a node representing {@code timeouts.network}, calling {@code child("connect")} will return a
     * node representing {@code timeouts.network.connect}.
     *
     * @param reference one or more segments of the key to append to the key of the current node
     *
     * @return the child or descendant node, never {@code null}.
     */
    PropertyNode child(String reference);
}
