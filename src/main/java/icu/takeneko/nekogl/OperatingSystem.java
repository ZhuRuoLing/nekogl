package icu.takeneko.nekogl;

import java.util.Locale;

public class OperatingSystem {
    public static final String OS_NAME = System.getProperty("os.name", "");
    private static final String normalizedOSName = OS_NAME.toLowerCase(Locale.ROOT).replace(" ", "");
    public static boolean IS_MAC_OS = normalizedOSName.contains("macosx");
}
