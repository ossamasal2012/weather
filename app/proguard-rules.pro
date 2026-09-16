# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# ---------------------------------------------------------------------------
# Hardening beyond R8's defaults.
#
# Context for whoever reads this later: no ProGuard/R8 configuration makes an
# Android app immune to inspection — a sufficiently motivated person can
# always pull strings and structure out of an installed APK. What these
# rules *do* achieve, for free, is: class/method/field names are scrambled
# (a.b.c instead of WeatherRepository.getWeather), unused code is stripped,
# and — with repackageclasses below — every remaining class is flattened
# into one anonymous top-level package, so the app's real module layout
# (data/, domain/, ui/, update/…) is not visible in a decompiler either.
# That materially raises the effort needed to make sense of a decompile,
# which is the realistic, honest goal here.
# ---------------------------------------------------------------------------
-repackageclasses ''
-allowaccessmodification

# Keep line numbers for readable crash stack traces, but hide real file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------------------
# kotlinx.serialization
# ---------------------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.osama.weather.**$$serializer { *; }
-keepclassmembers class com.osama.weather.** {
    *** Companion;
}
-keepclasseswithmembers class com.osama.weather.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all our own DTO / domain model classes intact (names, fields) since
# they are populated via reflection-free kotlinx.serialization but we still
# want stable field ordering and no accidental stripping of "unused" fields
# that are only read by serialization.
-keep,includedescriptorclasses class com.osama.weather.data.remote.dto.** { *; }
-keep,includedescriptorclasses class com.osama.weather.update.model.** { *; }

# ---------------------------------------------------------------------------
# OkHttp / Okio
# ---------------------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ---------------------------------------------------------------------------
# Play services location
# ---------------------------------------------------------------------------
-keep class com.google.android.gms.location.** { *; }
