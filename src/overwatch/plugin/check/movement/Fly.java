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
import overwatch.getPlugin.utils.UtilPlayer;

public class Fly
        extends Check
{
    public Fly(Overwatch overwatch)
    {
        super("Fly", "Fly", overwatch);

        setAutobanTimer(true);
    }

    private Map<UUID, Long> flyTicks = new HashMap();

    @EventHandler
    public void CheckFly(PlayerMoveEvent event)
    {
        if (!getOverwatch().isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (UtilPlayer.isInWater(player)) {
            return;
        }
        if (UtilCheat.isInWeb(player)) {
            return;
        }
        if (UtilCheat.blocksNear(player))
        {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        if ((event.getTo().getX() == event.getFrom().getX()) &&
                (event.getTo().getZ() == event.getFrom().getZ())) {
            return;
        }
        if (event.getTo().getY() != event.getFrom().getY())
        {
            if (this.flyTicks.containsKey(player.getUniqueId())) {
                this.flyTicks.remove(player.getUniqueId());
            }
            return;
        }
        long Time = System.currentTimeMillis();
        if (this.flyTicks.containsKey(player.getUniqueId())) {
            Time = ((Long)this.flyTicks.get(player.getUniqueId())).longValue();
        }
        long MS = System.currentTimeMillis() - Time;
        if (MS > 500L)
        {
            dumplog(player, "Logged. MS: " + MS);
            this.flyTicks.remove(player.getUniqueId());
            getOverwatch().logCheat(this, player, "Hover", new String[0]);
            return;
        }
        this.flyTicks.put(player.getUniqueId(), Long.valueOf(Time));
    }
}
