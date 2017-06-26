package overwatch.getPlugin.check.other;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketPlayerEvent;
import overwatch.getPlugin.utils.UtilTime;

public class MorePackets
        extends Check
{
    public MorePackets(Overwatch overwatch)
    {
        super("MorePackets", "MorePackets", overwatch);

        setBannable(false);
    }

    private Map<UUID, Map.Entry<Integer, Long>> packetTicks = new HashMap();
    private Map<UUID, Long> lastPacket = new HashMap();
    private List<UUID> blacklist = new ArrayList();

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PacketPlayer(PacketPlayerEvent event)
    {
        Player player = event.getPlayer();
        if (!getOverwatch().isEnabled()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (getOverwatch().lag.getTPS() > 21.0D) {
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.packetTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.packetTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.packetTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        if (this.lastPacket.containsKey(player.getUniqueId()))
        {
            long MS = System.currentTimeMillis() - ((Long)this.lastPacket.get(player.getUniqueId())).longValue();
            if (MS >= 100L) {
                this.blacklist.add(player.getUniqueId());
            } else if ((MS > 1L) &&
                    (this.blacklist.contains(player.getUniqueId()))) {
                this.blacklist.remove(player.getUniqueId());
            }
        }
        if (!this.blacklist.contains(player.getUniqueId()))
        {
            Count++;
            if ((this.packetTicks.containsKey(player.getUniqueId())) &&
                    (UtilTime.elapsed(Time, 1000L)))
            {
                int maxPackets = 49;
                if (Count > maxPackets) {
                    getOverwatch().logCheat(this, player, null, new String[] { "Experimental" });
                }
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        this.packetTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
        this.lastPacket.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    }
}
