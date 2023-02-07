package eu.kanade.tachiyomi

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import android.app.Application
import eu.kanade.tachiyomi.network.JavaScriptEngine
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.serialization.json.Json
import rx.Observable
import rx.schedulers.Schedulers
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {

        addSingleton(app)

        addSingletonFactory { NetworkHelper(app) }
        addSingletonFactory { JavaScriptEngine(app) }

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        // Asynchronously init expensive components for a faster cold start
        rxAsync { get<NetworkHelper>() }
    }

    private fun rxAsync(block: () -> Unit) {
        Observable.fromCallable { block() }.subscribeOn(Schedulers.computation()).subscribe()
    }
}
