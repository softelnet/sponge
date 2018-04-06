/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.reactivex;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JPlugin;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * ReactiveX plugin.
 */
public class ReactiveXPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "rx";

    private PublishSubject<Event> subject;

    private Observable<Event> observable;

    /**
     * Creates a new ReactiveX plugin.
     */
    public ReactiveXPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new ReactiveX plugin.
     *
     * @param name the plugin name.
     */
    public ReactiveXPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        subject = PublishSubject.create();
        // Ignore system events.
        observable = subject.filter(event -> !SpongeUtils.isSystemEvent(event));
    }

    /**
     * Starts up this plugin.
     */
    @Override
    public void onStartup() {
        // Enable RX correlator.
        getEngineOperations().enableJavaCorrelator(ReactiveXCorrelator.class);
    }

    public Observable<Event> getObservable() {
        return observable;
    }

    public PublishSubject<Event> getSubject() {
        return subject;
    }
}
