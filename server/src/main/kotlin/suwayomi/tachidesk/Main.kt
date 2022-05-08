package suwayomi.tachidesk

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import suwayomi.tachidesk.manga.impl.extension.Extension
import suwayomi.tachidesk.server.applicationSetup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.streams.asSequence

private val logger = KotlinLogging.logger {}

suspend fun main(args: Array<String>) {
    if (args.size < 3) {
        throw RuntimeException("Inspector must be given the path of apks directory, output json, and a tmp dir")
    }

    applicationSetup()

    val (apksPath, outputPath, tmpDirPath) = args

    val tmpDir = File(tmpDirPath, "tmp").also { it.mkdir() }
    val extensions = Files.find(Paths.get(apksPath), 2, { _, fileAttributes -> fileAttributes.isRegularFile })
        .asSequence()
        .filter { it.extension == "apk" }
        .toList()

    logger.info("Found ${extensions.size} extensions")

    val extensionsInfo = extensions.associate {
        logger.debug("Installing $it")
        val (pkgName, sources) = Extension.installAPK(tmpDir) { it.toFile() }
        pkgName to sources.map { source -> SourceJson(source) }
    }

    File(outputPath).writeText(Json.encodeToString(extensionsInfo))
}

@Serializable
data class SourceJson(
    val name: String,
    val lang: String,
    val id: String,
    val baseUrl: String,
    val versionId: Int,
    val hasCloudflare: Short
) {
    constructor(source: HttpSource) :
        this(
            source.name,
            source.lang,
            source.id.toString(),
            source.baseUrl,
            source.versionId,
            source.client.interceptors
                .any { it is CloudflareInterceptor }
                .toShort()
        )
}

private fun Boolean.toShort(): Short = if (this) 1 else 0