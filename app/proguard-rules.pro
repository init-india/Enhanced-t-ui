-keep public class ohi.andre.consolelauncher.commands.**
-keep public abstract class ohi.andre.consolelauncher.CommandAbstraction
-keep public class ohi.andre.consolelauncher.commands.main.**
-keep public class ohi.andre.consolelauncher.managers.**

# Keep libsu API
-keep class com.topjohnwu.superuser.** { *; }
-dontwarn com.topjohnwu.superuser.**



-keep class ohi.andre.consolelauncher.managers.suggestions.**
-keep public class it.andreuzzi.comparestring2.**

-dontwarn ohi.andre.consolelauncher.commands.main.raw.**

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn org.htmlcleaner.**
-dontwarn com.jayway.jsonpath.**
-dontwarn org.slf4j.**
-dontwarn org.jdom2.**
