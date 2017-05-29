/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.plugin;

import io.crate.http.netty.CrateNettyHttpServerTransport;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.http.HttpServerTransport;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.plugins.NetworkPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.threadpool.ThreadPool;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.elasticsearch.common.network.NetworkModule.HTTP_TYPE_KEY;
import static org.elasticsearch.http.HttpTransportSettings.SETTING_HTTP_COMPRESSION;


public class HttpTransportPlugin extends Plugin implements NetworkPlugin {

    private static final String CRATE_HTTP_TRANSPORT_NAME = "crate";

    public String name() {
        return "http";
    }

    public String description() {
        return "Plugin for extending HTTP transport";
    }

    @Override
    public Collection<Module> createGuiceModules() {
        return Collections.emptyList();
    }

    @Override
    public Settings additionalSettings() {
        return Settings.builder()
            .put(HTTP_TYPE_KEY, CRATE_HTTP_TRANSPORT_NAME)
            .put(SETTING_HTTP_COMPRESSION.getKey(), false)
            .build();
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Supplier<HttpServerTransport>> getHttpTransports(Settings settings,
                                                                        ThreadPool threadPool,
                                                                        BigArrays bigArrays,
                                                                        CircuitBreakerService circuitBreakerService,
                                                                        NamedWriteableRegistry namedWriteableRegistry,
                                                                        NetworkService networkService) {
        return Collections.singletonMap(
            CRATE_HTTP_TRANSPORT_NAME,
            () -> new CrateNettyHttpServerTransport(settings, networkService, bigArrays, threadPool));
    }
}
