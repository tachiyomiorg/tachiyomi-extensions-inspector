package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import android.content.Context
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class NetworkHelper(context: Context) {

    val cookieManager = MemoryCookieJar()

    val client by lazy {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
        builder.build()
    }

    val cloudflareClient by lazy {
        client.newBuilder()
            .addInterceptor(CloudflareInterceptor())
            .build()
    }
}
