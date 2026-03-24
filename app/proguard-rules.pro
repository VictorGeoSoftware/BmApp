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

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Firebase ──
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ── Kotlin Serialization ──
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.briel.marnisos.brielapp.data.model.**$$serializer { *; }
-keepclassmembers class com.briel.marnisos.brielapp.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.briel.marnisos.brielapp.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Data models (DTOs and domain models) ──
-keep class com.briel.marnisos.brielapp.data.model.** { *; }
-keep class com.briel.marnisos.brielapp.domain.models.** { *; }

# ── Ktor ──
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# ── OkHttp (Ktor engine) ──
-dontwarn okhttp3.**
-dontwarn okio.**

# ── Koin DI ──
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# ── Compose ──
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**