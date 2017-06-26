package overwatch.getPlugin.utils;


import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config
{
    private FileConfiguration Config;
    private File File;
    private String Name;

    public Config(JavaPlugin Plugin, String Path, String Name)
    {
        this.File = new File(Plugin.getDataFolder() + Path);
        this.File.mkdirs();
        this.File = new File(Plugin.getDataFolder() + Path, Name + ".yml");
        try
        {
            this.File.createNewFile();
        }
        catch (IOException localIOException) {}
        this.Name = Name;
        this.Config = YamlConfiguration.loadConfiguration(this.File);
    }

    public String getName()
    {
        return this.Name;
    }

    public FileConfiguration getConfig()
    {
        return this.Config;
    }

    public void setDefault(String Path, Object Set)
    {
        if (!getConfig().contains(Path))
        {
            this.Config.set(Path, Set);
            save();
        }
    }

    public void save()
    {
        try
        {
            this.Config.save(this.File);
        }
        catch (IOException localIOException) {}
    }

    public void reload()
    {
        this.Config = YamlConfiguration.loadConfiguration(this.File);
    }
}
