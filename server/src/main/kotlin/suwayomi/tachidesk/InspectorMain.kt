package suwayomi.tachidesk

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import suwayomi.tachidesk.manga.impl.extension.Extension
import suwayomi.tachidesk.manga.impl.extension.Extension.installAPK
import java.io.File

object InspectorMain {
    private val logger = KotlinLogging.logger {}

    suspend fun inspectorMain(args: Array<String>) {
        if (args.size < 3) {
            throw RuntimeException("Inspector must be given the path of apks directory, output json, and a tmp dir")
        }

        val apksPath = args[0]
        val outputPath = args[1]
        val tmpDirPath = args[2]

        val tmpDir = File(tmpDirPath, "tmp").also { it.mkdir() }
        val extensions = File(apksPath).listFiles().orEmpty().mapNotNull {
            if (it.extension == "apk") {
                logger.info("Installing ${it.absolutePath}")

                val (pkgName, sources) = installAPK(tmpDir) {
                    it
                }
                pkgName to sources.map { source -> SourceJson(source) }
            } else null
        }.toMap()

        File(outputPath).writeText(Json.encodeToString(extensions))
    }

    @Serializable
    data class SourceJson(
        val name: String,
        val lang: String,
        val id: String,
        val baseUrl: String,
        val nsfw: Boolean
    ) {
        constructor(loadedSource: Extension.LoadedSource) :
            this(
                loadedSource.source.name,
                loadedSource.source.lang,
                loadedSource.source.id.toString(),
                loadedSource.source.baseUrl,
                loadedSource.isNsfw
            )
    }
}
