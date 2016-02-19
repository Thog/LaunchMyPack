package eu.thog92.launcher.download;

import eu.thog92.launcher.view.IDownloadView;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Downloadable
{
    protected final URL url;
    protected final File target;
    protected final boolean forceDownload;
    protected final Proxy proxy;
    protected long startTime;
    protected IDownloadView view;
    protected int numAttempts;
    protected long expectedSize;
    protected long endTime;

    public Downloadable(URL url, File local)
    {
        this.proxy = Proxy.NO_PROXY;
        this.url = url;
        this.target = local;
        this.forceDownload = true;
    }

    public Downloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload)
    {
        this.proxy = proxy;
        this.url = remoteFile;
        this.target = localFile;
        this.forceDownload = forceDownload;
    }

    public static String getDigest(File file, String algorithm, int hashLength)
    {
        DigestInputStream stream = null;
        try
        {
            stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
            byte[] buffer = new byte[65536];
            int read;
            do
            {
                read = stream.read(buffer);
            } while (read > 0);
        } catch (Exception ignored)
        {
            return null;
        } finally
        {
            closeSilently(stream);
        }
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, stream.getMessageDigest().digest()));
    }

    public static void closeSilently(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (IOException ignored)
            {
            }
        }
    }

    public long getExpectedSize()
    {
        return this.expectedSize;
    }

    public void setExpectedSize(long expectedSize)
    {
        this.expectedSize = expectedSize;
    }

    public abstract String download()
            throws IOException;

    protected void updateExpectedSize(HttpURLConnection connection)
    {
        if (this.expectedSize == 0L)
        {
            setExpectedSize(connection.getContentLength());
        } else
        {

        }
    }

    protected HttpURLConnection makeConnection(URL url)
            throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(this.proxy);


        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
        connection.setRequestProperty("Expires", "0");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(30000);

        return connection;
    }

    public URL getUrl()
    {
        return this.url;
    }

    public File getTarget()
    {
        return this.target;
    }

    public boolean shouldIgnoreLocal()
    {
        return this.forceDownload;
    }

    public int getNumAttempts()
    {
        return this.numAttempts;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public String copyAndDigest(InputStream inputStream, OutputStream outputStream, String algorithm, int hashLength)
            throws IOException
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e)
        {
            closeSilently(inputStream);
            closeSilently(outputStream);
            throw new RuntimeException("Missing Digest." + algorithm, e);
        }
        byte[] buffer = new byte[65536];
        try
        {
            int read = inputStream.read(buffer);
            while (read >= 1)
            {
                digest.update(buffer, 0, read);
                outputStream.write(buffer, 0, read);
                if (view != null)
                    view.setInfo("Downloading " + this.target.getName() + " (" + ((read / this.expectedSize) * 100) + " %)");
                read = inputStream.read(buffer);
            }
        } finally
        {
            closeSilently(inputStream);
            closeSilently(outputStream);
        }
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, digest.digest()));
    }

    protected void ensureFileWritable(File target)
    {
        if ((target.getParentFile() != null) && (!target.getParentFile().isDirectory()))
        {
            System.out.println("Making directory " + target.getParentFile());
            if ((!target.getParentFile().mkdirs()) &&
                    (!target.getParentFile().isDirectory()))
            {
                throw new RuntimeException("Could not create directory " + target.getParentFile());
            }
        }
        if ((target.isFile()) && (!target.canWrite()))
        {
            throw new RuntimeException("Do not have write permissions for " + target + " - aborting!");
        }
    }

    public long getStartTime()
    {
        return this.startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public String getStatus()
    {
        return "Downloading " + getTarget().getName();
    }

    public long getEndTime()
    {
        return this.endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public void setView(IDownloadView view)
    {
        this.view = view;
    }
}
