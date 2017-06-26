package overwatch.getPlugin.check.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;

public class Glide extends Check
{
    private Map<UUID, Long> flyTicks;

    public Glide(final Overwatch overwatch) {
        super("Glide", "Glide", overwatch);
        this.flyTicks = new HashMap<UUID, Long>();
        this.setAutobanTimer(true);
    }

    @Override
    public void onEnable() {
    }

    @EventHandler
    public void CheckGlide(final PlayerMoveEvent event) {
        if (!this.getOverwatch().isEnabled()) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (UtilCheat.isInWeb(player)) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (UtilCheat.blocksNear(player)) {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        if (event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ()) {
            return;
        }
        final double OffsetY = event.getFrom().getY() - event.getTo().getY();
        if (OffsetY <= 0.0 || OffsetY > 0.16) {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        this.dumplog(player, "OffsetY: " + OffsetY);
        long Time = System.currentTimeMillis();
        if (this.flyTicks.containsKey(player.getUniqueId())) {
            Time = this.flyTicks.get(player.getUniqueId());
        }
        final long MS = System.currentTimeMillis() - Time;
        this.dumplog(player, "MS: " + MS);
        if (MS > 1000L) {
            this.dumplog(player, "Logged. MS: " + MS);
            this.flyTicks.remove(player.getUniqueId());
            this.getOverwatch().logCheat(this, player, "Fall Speed", new String[0]);
            return;
        }
        this.flyTicks.put(player.getUniqueId(), Time);
    }
}