# ProGuard / R8 rules for VolumePilot

# Preserve Line Numbers for stack traces in release builds
-keepattributes SourceFile,LineNumberTable

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt / Dagger
-dontwarn com.google.errorprone.annotations.**

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Keep models and data transfer objects
-keep class com.volumepilot.domain.model.** { *; }
-keep class com.volumepilot.data.local.** { *; }

# Preserve org.json parsing methods and fields
-keepclassmembers class org.json.** {
    public <methods>;
    public <fields>;
}

# Hilt injection
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Jetpack Compose
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**

