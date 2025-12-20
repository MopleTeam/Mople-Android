import org.gradle.api.JavaVersion

object MoimConfig {

    const val APPLICATION_ID = "com.moim.moimtable"

    const val MIN_SDK = 28    // os 9
    const val TARGET_SDK = 36 // os 16
    const val COMPILE_SDK = 36
    val javaCompileTarget = JavaVersion.VERSION_17

    private const val VERSION_MAJOR = 1
    private const val VERSION_MINOR = 3
    private const val VERSION_PATCH = 3

    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"
    const val VERSION_CODE = VERSION_MAJOR.times(1000000) + VERSION_MINOR.times(1000) + VERSION_PATCH
}