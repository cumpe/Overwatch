package overwatch.getPlugin.lag;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import overwatch.getPlugin.Overwatch;

public class LagCore
        implements Listener
{
    public Overwatch overwatch;
    private double tps;

    public LagCore(Overwatch overwatch)
    {
        this.overwatch = overwatch;

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.overwatch,
                new Runnable()
                {
                    long sec;
                    long currentSec;
                    int ticks;

                    public void run()
                    {
                        this.sec = (System.currentTimeMillis() / 1000L);
                        if (this.currentSec == this.sec)
                        {
                            this.ticks += 1;
                        }
                        else
                        {
                            this.currentSec = this.sec;
                            LagCore.this.tps = (LagCore.this.tps == 0.0D ? this.ticks : (LagCore.this.tps + this.ticks) / 2.0D);
                            this.ticks = 0;
                        }
                    }
                }, 0L, 1L);

        this.overwatch.RegisterListener(this);
    }

    public double getTPS()
    {
        return this.tps + 1.0D > 20.0D ? 20.0D : this.tps + 1.0D;
    }

    public int getPing(Player player)
    {
        CraftPlayer cp = (CraftPlayer)player;
        EntityPlayer ep = cp.getHandle();
        return ep.ping;
    }
}