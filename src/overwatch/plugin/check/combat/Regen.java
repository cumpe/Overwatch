package overwatch.getPlugin.check.combat;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilTime;

public class Regen
        extends Check
{
    public Regen(Overwatch overwatch)
    {
        super("Regen", "Regen", overwatch);

        setAutobanTimer(true);
        setViolationsToNotify(3);
        setMaxViolations(12);
        setViolationResetTime(60000L);
    }

    private Map<UUID, Long> LastHeal = new HashMap();
    private Map<UUID, Map.Entry<Integer, Long>> FastHealTicks = new HashMap();

    public boolean checkFastHeal(Player player)
    {
        if (this.LastHeal.containsKey(player.getUniqueId()))
        {
            long l = ((Long)this.LastHeal.get(player.getUniqueId())).longValue();
            this.LastHeal.remove(player.getUniqueId());
            if (System.currentTimeMillis() - l < 3000L) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onHeal(EntityRegainHealthEvent event)
    {
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.FastHealTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.FastHealTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.FastHealTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        if (checkFastHeal(player)) {
            getOverwatch().logCheat(this, player, null, new String[0]);
        }
        if ((this.FastHealTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 60000L)))
        {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        this.LastHeal.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        this.FastHealTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}
