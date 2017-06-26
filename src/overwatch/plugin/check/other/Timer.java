package overwatch.getPlugin.check.other;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.entity.Player;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketPlayerEvent;
import overwatch.getPlugin.utils.UtilMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class Timer extends Check
{
    private Map<UUID, Long> lastTimer;
    private Map<UUID, List<Long>> MS;
    private Map<UUID, Integer> timerTicks;

    public Timer(final Overwatch janitor) {
        super("Timer", "Timer", janitor);
        this.lastTimer = new HashMap<UUID, Long>();
        this.MS = new HashMap<UUID, List<Long>>();
        this.timerTicks = new HashMap<UUID, Integer>();
        this.setAutobanTimer(true);
    }

    @EventHandler
    public void PacketPlayer(final PacketPlayerEvent event) {
        final Player player = event.getPlayer();
        if (!this.getOverwatch().isEnabled()) {
            return;
        }
        int Count = 0;
        if (this.timerTicks.containsKey(player.getUniqueId())) {
            Count = this.timerTicks.get(player.getUniqueId());
        }
        if (this.lastTimer.containsKey(player.getUniqueId())) {
            final long MS = System.currentTimeMillis() - this.lastTimer.get(player.getUniqueId());
            List<Long> List = new ArrayList<Long>();
            if (this.MS.containsKey(player.getUniqueId())) {
                List = this.MS.get(player.getUniqueId());
            }
            List.add(MS);
            if (List.size() == 20) {
                boolean doeet = true;
                for (final Long ListMS : List) {
                    if (ListMS < 1L) {
                        doeet = false;
                    }
                }
                final Long average = UtilMath.averageLong(List);
                this.dumplog(player, "Average MS for 20 ticks: " + average);
                if (average < 48L && doeet) {
                    ++Count;
                    this.dumplog(player, "New Count: " + Count);
                }
                else {
                    Count = 0;
                }
                this.MS.remove(player.getUniqueId());
            }
            else {
                this.MS.put(player.getUniqueId(), List);
            }
        }
        if (Count > 4) {
            this.dumplog(player, "Logged for timer. Count: " + Count);
            Count = 0;
            this.getOverwatch().logCheat(this, player, null, new String[0]);
        }
        this.lastTimer.put(player.getUniqueId(), System.currentTimeMillis());
        this.timerTicks.put(player.getUniqueId(), Count);
    }
}