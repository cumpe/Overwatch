package overwatch.getPlugin.update;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import overwatch.getPlugin.Overwatch;

public class Updater implements Runnable
{
    private Overwatch overwatch;
    private int updater;

    public Updater(final Overwatch janitor) {
        super();
        this.overwatch = janitor;
        this.updater = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.overwatch, (Runnable)this, 0L, 1L);
    }

    public void Disable() {
        Bukkit.getScheduler().cancelTask(this.updater);
    }

    @Override
    public void run() {
        UpdateType[] values;
        for (int length = (values = UpdateType.values()).length, i = 0; i < length; ++i) {
            final UpdateType updateType = values[i];
            if (updateType != null) {
                if (updateType.Elapsed()) {
                    try {
                        final UpdateEvent event = new UpdateEvent(updateType);
                        Bukkit.getPluginManager().callEvent((Event)event);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
