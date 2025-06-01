import org.gradle.api.JavaVersion

object MoimConfig {

    const val APPLICATION_ID = "com.moim.moimtable"

    const val MIN_SDK = 29    // os 10
    const val TARGET_SDK = 34 // os 14
    const val COMPILE_SDK = 35
    val javaCompileTarget = JavaVersion.VERSION_17

    private const val VERSION_MAJOR = 1
    private const val VERSION_MINOR = 0
    private const val VERSION_PATCH = 6

    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"
    const val VERSION_CODE = VERSION_MAJOR.times(1000000) + VERSION_MINOR.times(1000) + VERSION_PATCH
}