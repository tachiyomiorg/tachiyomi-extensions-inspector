package suwayomi.tachidesk

import eu.kanade.tachiyomi.source.CatalogueSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import suwayomi.tachidesk.manga.impl.extension.Extension.installAPK
import java.io.File

object InspectorMain {
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
                println("Installing ${it.absolutePath}")

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
        val id: Long
    ) {
        constructor(source: CatalogueSource) :
            this(source.name, source.lang, source.id)
    }
}
