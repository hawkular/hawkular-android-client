## Icepick

-dontwarn icepick.**

-keepnames class * { @icepick.State *; }

-keep class **$$Icepick { *; }

-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
