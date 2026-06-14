# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Room entities
-keep class com.gopuzzle.app.data.local.entity.** { *; }

# Keep SGF model classes
-keep class com.gopuzzle.app.domain.model.** { *; }
