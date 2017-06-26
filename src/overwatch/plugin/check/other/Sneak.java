package overwatch.getPlugin.check.other;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketEntityActionEvent;
import overwatch.getPlugin.utils.UtilTime;

public class Sneak
        extends Check
{
    private Map<UUID, Map.Entry<Integer, Long>> sneakTicks = new HashMap();

    public Sneak(Overwatch janitor)
    {
        super("Sneak", "Sneak", janitor);

        setAutobanTimer(true);
    }

    @EventHandler
    public void EntityAction(PacketEntityActionEvent event)
    {
        if (event.getAction() != 1) {
            return;
        }
        Player player = event.getPlayer();

        int Count = 0;
        long Time = -1L;
        if (this.sneakTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.sneakTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.sneakTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        Count++;
        if (this.sneakTicks.containsKey(player.getUniqueId())) {
            if (UtilTime.elapsed(Time, 100L))
            {
                Count = 0;
                Time = System.currentTimeMillis();
            }
            else
            {
                Time = System.currentTimeMillis();
            }
        }
        if (Count > 50)
        {
            Count = 0;

            getOverwatch().logCheat(this, player, null, new String[0]);
        }
        this.sneakTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}
