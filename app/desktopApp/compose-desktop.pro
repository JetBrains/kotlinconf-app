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

# Ktor client engines
-keep class io.ktor.client.engine.okhttp.OkHttpEngineContainer { *; }
-keep class io.ktor.client.HttpClientEngineContainer { *; }
-keep class * implements io.ktor.client.HttpClientEngineContainer { *; }

# Ktor serialization providers
-keep class io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider { *; }
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }
-keep class * implements io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider { *; }

# SLF4J providers
-keep class org.slf4j.nop.NOPServiceProvider { *; }
-keep class * implements org.slf4j.spi.SLF4JServiceProvider { *; }

# Kotlin serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    *** Companion;
}
-keep class *$.serializer { *; }
