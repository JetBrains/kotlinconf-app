# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,Exceptions,Annotation

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class io.ktor.** { *; }
-keep class android.arch.** { *; }
-keep class kotlin.reflect.** { *; }
-keep class androidx.media.** { *; }
-keep class org.jetbrains.anko.** { *; }
-keep class android.support.v7.widget.** { *; }
-keep class kotlinx.coroutines.** { *; }

-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.jetbrains.anko.**
-dontwarn org.slf4j.**
-dontwarn kotlin.reflect.jvm.**
-dontwarn androidx.media.**
