package fr.wherecraft.launcher.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.thog92.launcher.download.Downloadable;
import eu.thog92.launcher.model.AbstractModel;

public class DownloadModel extends AbstractModel
{
    private List<Downloadable> downloadmodpack;
    private static ModPack modPack;
    
    public DownloadModel()
    {
        this.downloadmodpack = new ArrayList<Downloadable>();
    }
    
    public synchronized List<Downloadable> getDownloadListModPack()
    {
        return downloadmodpack;
        
    }
    public synchronized void addDownloadListModPack(Downloadable d)
    {
        downloadmodpack.add(d);
    }
    
    public synchronized void setModPack(Object[] array)
    {
        modPack = new ModPack((RemotePack)array[0], (File)array[1]);
    }
    
    public void setUsername(String ignore) {}

    public synchronized File getModPackDir()
    {
        return modPack.getWorkdir().getAbsoluteFile();
    }
    
    public synchronized String getDownloadURL()
    {
        return modPack.getDownloadURL();
    }
    
    public synchronized File getMinecraftJarPath(){ return new File(modPack.getWorkdir(), "bin/minecraft.jar"); }
}
