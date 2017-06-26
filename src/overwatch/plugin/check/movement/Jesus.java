package overwatch.getPlugin.check.movement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;

public class Jesus extends Check
{
    private Map<UUID, Long> jesusTicks;

    public Jesus(final Overwatch overwatch) {
        super("Jesus", "Jesus", overwatch);
        this.jesusTicks = new HashMap<UUID, Long>();
        this.setAutobanTimer(true);
        this.setMaxViolations(10);
        this.setViolationsToNotify(2);
    }

    @EventHandler
    public void CheckJesus(final PlayerMoveEvent event) {
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }
        long Time = System.currentTimeMillis();
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (!player.getNearbyEntities(1.0, 1.0, 1.0).isEmpty()) {
            return;
        }
        if (this.jesusTicks.containsKey(player.getUniqueId())) {
            Time = this.jesusTicks.get(player.getUniqueId());
        }
        final long MS = System.currentTimeMillis() - Time;
        if (UtilCheat.cantStandAtWater(player.getWorld().getBlockAt(player.getLocation())) && UtilCheat.isHoveringOverWater(player.getLocation()) && !UtilCheat.isFullyInWater(player.getLocation())) {
            this.dumplog(player, "Been hovering over water for: " + MS + "ms.");
            if (MS > 500L) {
                this.dumplog(player, "(LIMIT) Been hovering over water for: " + MS + "ms.");
                this.getOverwatch().logCheat(this, player, null, "Experimental");
                Time = System.currentTimeMillis();
            }
        }
        else {
            Time = System.currentTimeMillis();
        }
        this.jesusTicks.put(player.getUniqueId(), Time);
    }
}