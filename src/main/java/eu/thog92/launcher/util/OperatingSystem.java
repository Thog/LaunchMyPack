package eu.thog92.launcher.util;

import java.io.File;
import java.net.URI;

public enum OperatingSystem
{
    LINUX("linux", new String[]{"linux", "unix"}), WINDOWS("windows", new String[]{"win"}), OSX("osx", new String[]{"mac"}), UNKNOWN("unknown", new String[0]);

    private final String name;
    private final String[] aliases;

    OperatingSystem(final String name, final String[] aliases)
    {
        this.name = name;
        this.aliases = aliases == null ? new String[0] : aliases;
    }

    public static OperatingSystem getCurrentPlatform()
    {
        final String osName = System.getProperty("os.name").toLowerCase();

        for (final OperatingSystem os : values())
            for (final String alias : os.getAliases())
                if (osName.contains(alias))
                    return os;

        return UNKNOWN;
    }

    public static void openLink(final URI link)
    {
        try
        {
            final Class<?> desktopClass = Class.forName("java.awt.Desktop");
            final Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null);
            desktopClass.getMethod("browse", new Class[]{URI.class}).invoke(o, link);
        } catch (final Throwable e)
        {
            e.printStackTrace();
        }
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public String getJavaDir()
    {
        final String separator = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + separator + "bin" + separator;

        if (getCurrentPlatform() == WINDOWS && new File(path + "javaw.exe").isFile())
            return path + "javaw.exe";

        return path + "java";
    }

    public String getName()
    {
        return name;
    }

    public boolean isSupported()
    {
        return this != UNKNOWN;
    }
}