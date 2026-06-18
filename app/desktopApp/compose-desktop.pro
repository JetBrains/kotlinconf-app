# ProGuard rules for the desktop release build (:app:desktopApp:runRelease).
#
# OkHttp (pulled in transitively via ktor-client-okhttp) references several
# optional dependencies that are not on the classpath: GraalVM native-image,
# Conscrypt, BouncyCastle and OpenJSSE. These references are only used when the
# corresponding providers are present at runtime, so they are safe to ignore.
-dontwarn org.graalvm.nativeimage.hosted.**
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn okhttp3.internal.platform.**

# The Metro dependency graph factory generates a synthetic `create$default`
# helper for the default `platformFlags` parameter. ProGuard reports it as an
# inconsistent program class member; suppressing the warning is safe because the
# generated graph is kept and used as the application entry point.
-dontwarn org.jetbrains.kotlinconf.di.JvmAppGraph$Companion
