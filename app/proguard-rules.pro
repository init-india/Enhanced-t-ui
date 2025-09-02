# --------------------------
# ConsoleLauncher Core
# --------------------------
-keep public class ohi.andre.consolelauncher.commands.** { *; }
-keep public abstract class ohi.andre.consolelauncher.** { *; }
-keep public class ohi.andre.consolelauncher.commands.main.** { *; }
-keep public class ohi.andre.consolelauncher.managers.** { *; }
-keep class ohi.andre.consolelauncher.tuils.libsuperuser.** { *; }
-keep class ohi.andre.consolelauncher.managers.suggestions.** { *; }
-keep public class it.andreuzzi.comparestring2.** { *; }

-dontwarn ohi.andre.consolelauncher.commands.main.raw.**

# --------------------------
# General Safe Ignores
# --------------------------
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# --------------------------
# Networking
# --------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# --------------------------
# External Libraries
# --------------------------
-dontwarn org.htmlcleaner.**
-dontwarn com.jayway.jsonpath.**
-dontwarn org.slf4j.**
-dontwarn org.jdom2.**

# --------------------------
# Room Database
# --------------------------
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# --------------------------
# JavaMail (Email)
# --------------------------
-keep class javax.mail.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn javax.mail.**
-dontwarn com.sun.mail.**

# --------------------------
# Google Play Services (Maps & Location)
# --------------------------
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
