package eu.kanade.tachiyomi

class BuildConfig {
    companion object {
        const val VERSION_NAME = inspector.BuildConfig.NAME
        val VERSION_CODE = inspector.BuildConfig.REVISION.trimStart('r').toInt()
    }
}
