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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Country class and its members

-keep class statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.model.Country { *; }

# Keep Gson related classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Prevent ProGuard from stripping the annotations used by Gson
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# to keep all the classes name unchanged when minifyingEnabled is true
#-keep class ** { *; }