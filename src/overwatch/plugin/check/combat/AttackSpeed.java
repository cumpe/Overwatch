package overwatch.getPlugin.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

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
import overwatch.getPlugin.packets.events.PacketUseEntityEvent;
import overwatch.getPlugin.utils.UtilTime;

public class AttackSpeed
        extends Check
{
    private Map<UUID, Map.Entry<Integer, Long>> attackTicks = new HashMap();

    public AttackSpeed(Overwatch overwatch)
    {
        super("AttackSpeed", "AttackSpeed", overwatch);

        setAutobanTimer(true);
    }

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e)
    {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        if (!(e.getAttacked() instanceof Player)) {
            return;
        }
        Player player = e.getAttacker();

        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.attackTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.attackTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.attackTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        Count++;
        if ((this.attackTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 1000L)))
        {
            if (Count > 19) {
                this.getOverwatch().logCheat(this, player, null, new String[] { Count + " ap/s" });
            }
            Count = 0;
            Time = UtilTime.nowlong();
        }
        this.attackTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}
