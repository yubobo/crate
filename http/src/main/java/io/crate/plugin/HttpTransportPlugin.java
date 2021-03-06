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
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchRequestParsers;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.util.Collection;
import java.util.Collections;

import static org.elasticsearch.common.network.NetworkModule.HTTP_TYPE_KEY;
import static org.elasticsearch.http.HttpTransportSettings.SETTING_HTTP_COMPRESSION;


public class HttpTransportPlugin extends Plugin {

    private static final String CRATE_HTTP_TRANSPORT_NAME = "crate";
    private final PipelineRegistry pipelineRegistry = new PipelineRegistry();

    public String name() {
        return "http";
    }

    public String description() {
        return "Plugin for extending HTTP transport";
    }

    @Override
    public Collection<Object> createComponents(Client client,
                                               ClusterService clusterService,
                                               ThreadPool threadPool,
                                               ResourceWatcherService resourceWatcherService,
                                               ScriptService scriptService,
                                               SearchRequestParsers searchRequestParsers) {
        // pipelineRegistry is returned here so that it's bound in guice and can be injected in other places
        return Collections.singletonList(pipelineRegistry);
    }

    @Override
    public Settings additionalSettings() {
        return Settings.builder()
            .put(HTTP_TYPE_KEY, CRATE_HTTP_TRANSPORT_NAME)
            .put(SETTING_HTTP_COMPRESSION.getKey(), false)
            .build();
    }

    public void onModule(NetworkModule networkModule) {
        if (networkModule.canRegisterHttpExtensions()) {
            networkModule.registerHttpTransport(CRATE_HTTP_TRANSPORT_NAME, CrateNettyHttpServerTransport.class);
        }
    }
}
