package overwatch.getPlugin.check.other;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketBlockPlacementEvent;
import overwatch.getPlugin.packets.events.PacketHeldItemChangeEvent;
import overwatch.getPlugin.packets.events.PacketSwingArmEvent;
import overwatch.getPlugin.utils.UtilTime;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class Crash extends Check
{
    private Map<UUID, Map.Entry<Integer, Long>> faggotTicks;
    private Map<UUID, Map.Entry<Integer, Long>> faggot2Ticks;
    private Map<UUID, Map.Entry<Integer, Long>> faggot3Ticks;
    public List<UUID> faggots;

    public Crash(final Overwatch janitor) {
        super("Crash", "Crash", janitor);
        this.faggotTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.faggot2Ticks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.faggot3Ticks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.faggots = new ArrayList<UUID>();
        this.setMaxViolations(0);
    }

    @EventHandler
    public void Swing(final PacketSwingArmEvent e) {
        final Player faggot = e.getPlayer();
        if (this.faggots.contains(faggot.getUniqueId())) {
            e.getPacketEvent().setCancelled(true);
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.faggotTicks.containsKey(faggot.getUniqueId())) {
            Count = this.faggotTicks.get(faggot.getUniqueId()).getKey();
            Time = this.faggotTicks.get(faggot.getUniqueId()).getValue();
        }
        ++Count;
        if (this.faggotTicks.containsKey(faggot.getUniqueId()) && UtilTime.elapsed(Time, 100L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count > 2000) {
            this.getOverwatch().logCheat(this, faggot, null, new String[0]);
            this.faggots.add(faggot.getUniqueId());
        }
        this.faggotTicks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }

    @EventHandler
    public void Switch(final PacketHeldItemChangeEvent e) {
        final Player faggot = e.getPlayer();
        if (this.faggots.contains(faggot.getUniqueId())) {
            e.getPacketEvent().setCancelled(true);
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.faggot2Ticks.containsKey(faggot.getUniqueId())) {
            Count = this.faggot2Ticks.get(faggot.getUniqueId()).getKey();
            Time = this.faggot2Ticks.get(faggot.getUniqueId()).getValue();
        }
        ++Count;
        if (this.faggot2Ticks.containsKey(faggot.getUniqueId()) && UtilTime.elapsed(Time, 100L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count > 2000) {
            this.getOverwatch().logCheat(this, faggot, null, new String[0]);
            this.faggots.add(faggot.getUniqueId());
        }
        this.faggot2Ticks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }

    @EventHandler
    public void BlockPlace(final PacketBlockPlacementEvent e) {
        final Player faggot = e.getPlayer();
        if (this.faggots.contains(faggot.getUniqueId())) {
            e.getPacketEvent().setCancelled(true);
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.faggot3Ticks.containsKey(faggot.getUniqueId())) {
            Count = this.faggot3Ticks.get(faggot.getUniqueId()).getKey();
            Time = this.faggot3Ticks.get(faggot.getUniqueId()).getValue();
        }
        ++Count;
        if (this.faggot3Ticks.containsKey(faggot.getUniqueId()) && UtilTime.elapsed(Time, 100L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count > 2000) {
            this.getOverwatch().logCheat(this, faggot, null, new String[0]);
            this.faggots.add(faggot.getUniqueId());
        }
        this.faggot3Ticks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }
}