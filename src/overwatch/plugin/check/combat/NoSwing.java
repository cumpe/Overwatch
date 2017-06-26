package overwatch.getPlugin.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitScheduler;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketSwingArmEvent;
import overwatch.getPlugin.utils.UtilTime;

public class NoSwing
        extends Check
{
    public NoSwing(Overwatch overwatch)
    {
        super("NoSwing", "NoSwing", overwatch);

        setAutobanTimer(true);
    }

    private Map<UUID, Long> LastArmSwing = new HashMap();

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e)
    {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        if (getOverwatch().getLag().getTPS() < 17.0D) {
            return;
        }
        Player player = (Player)e.getDamager();

        final Player fplayer = player;
        if (getOverwatch().isEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getOverwatch(), new Runnable()
            {
                public void run()
                {
                    if (!NoSwing.this.hasSwung(fplayer, Long.valueOf(1500L))) {
                        NoSwing.this.getOverwatch().logCheat(NoSwing.this, fplayer, null, new String[0]);
                    }
                }
            }, 10L);
        }
    }

    public boolean hasSwung(Player player, Long time)
    {
        if (!this.LastArmSwing.containsKey(player.getUniqueId())) {
            return false;
        }
        return UtilTime.nowlong() < ((Long)this.LastArmSwing.get(player.getUniqueId())).longValue() + time.longValue();
    }

    @EventHandler
    public void ArmSwing(PacketSwingArmEvent event)
    {
        this.LastArmSwing.put(event.getPlayer().getUniqueId(), Long.valueOf(UtilTime.nowlong()));
    }
}
