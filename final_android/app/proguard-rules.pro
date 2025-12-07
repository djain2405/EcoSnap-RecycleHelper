# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep all classes for now (workshop app - not optimized)
-keep class com.workshop.recyclehelper.** { *; }

# Uncomment this to preserve the line number information for debugging
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
-renamesourcefileattribute SourceFile
