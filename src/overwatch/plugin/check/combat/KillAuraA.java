package overwatch.getPlugin.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketUseEntityEvent;
import overwatch.getPlugin.utils.UtilTime;

public class KillAuraA extends Check
{
    private Map<UUID, Long> LastMS;
    private Map<UUID, List<Long>> Clicks;
    private Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public KillAuraA(final Overwatch overwatch) {
        super("KillAuraA", "Kill Aura (Click Pattern)", overwatch);
        this.LastMS = new HashMap<UUID, Long>();
        this.Clicks = new HashMap<UUID, List<Long>>();
        this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.setBannable(false);
    }

    @Override
    public void onEnable() {
    }

    @EventHandler
    public void UseEntity(final PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        if (!(e.getAttacked() instanceof Player)) {
            return;
        }
        final Player damager = e.getAttacker();
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.ClickTicks.containsKey(damager.getUniqueId())) {
            Count = this.ClickTicks.get(damager.getUniqueId()).getKey();
            Time = this.ClickTicks.get(damager.getUniqueId()).getValue();
        }
        if (this.LastMS.containsKey(damager.getUniqueId())) {
            final long MS = UtilTime.nowlong() - this.LastMS.get(damager.getUniqueId());
            if (MS > 500L || MS < 5L) {
                this.LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
                return;
            }
            if (this.Clicks.containsKey(damager.getUniqueId())) {
                final List<Long> Clicks = this.Clicks.get(damager.getUniqueId());
                if (Clicks.size() == 10) {
                    this.Clicks.remove(damager.getUniqueId());
                    Collections.sort(Clicks);
                    final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
                    this.dumplog(damager, "New Range: " + Range);
                    if (Range < 30L) {
                        ++Count;
                        Time = System.currentTimeMillis();
                        this.dumplog(damager, "New Count: " + Count);
                    }
                }
                else {
                    Clicks.add(MS);
                    this.Clicks.put(damager.getUniqueId(), Clicks);
                }
            }
            else {
                final List<Long> Clicks = new ArrayList<Long>();
                Clicks.add(MS);
                this.Clicks.put(damager.getUniqueId(), Clicks);
            }
        }
        if (this.ClickTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count > 0) {
            this.dumplog(damager, "Logged. Count: " + Count);
            Count = 0;
            this.getOverwatch().logCheat(this, damager, "Click Pattern", "Experimental");
        }
        this.LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
        this.ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }
}