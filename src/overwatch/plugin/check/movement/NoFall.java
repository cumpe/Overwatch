package overwatch.getPlugin.check.movement;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilPlayer;
import overwatch.getPlugin.utils.UtilTime;

public class NoFall
        extends Check
{
    private Map<UUID, Map.Entry<Long, Integer>> NoFallTicks = new HashMap();
    private Map<UUID, Double> FallDistance = new HashMap();

    public NoFall(Overwatch overwatch)
    {
        super("NoFall", "NoFall", overwatch);

        setBannable(false);
        setAutobanTimer(true);
        setMaxViolations(10);
    }

    @EventHandler
    public void Move(PlayerMoveEvent e)
    {
        Player player = e.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (player.getHealth() <= 0.0D) {
            return;
        }
        if (UtilPlayer.isOnClimbable(player)) {
            return;
        }
        if (UtilPlayer.isInWater(player)) {
            return;
        }
        double Falling = 0.0D;
        if ((!UtilPlayer.isOnGround(player)) && (e.getFrom().getY() > e.getTo().getY()))
        {
            if (this.FallDistance.containsKey(player.getUniqueId())) {
                Falling = ((Double)this.FallDistance.get(player.getUniqueId())).doubleValue();
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        this.FallDistance.put(player.getUniqueId(), Double.valueOf(Falling));
        if (Falling < 3.0D) {
            return;
        }
        long Time = System.currentTimeMillis();
        int Count = 0;
        if (this.NoFallTicks.containsKey(player.getUniqueId()))
        {
            Time = ((Long)((Map.Entry)this.NoFallTicks.get(player.getUniqueId())).getKey()).longValue();
            Count = Integer.valueOf(((Integer)((Map.Entry)this.NoFallTicks.get(player.getUniqueId())).getValue()).intValue()).intValue();
        }
        if ((player.isOnGround()) || (player.getFallDistance() == 0.0F))
        {
            dumplog(player, "NoFall. Real Fall Distance: " + Falling);
            Count++;
        }
        else
        {
            dumplog(player, "Count Reset");
            Count = 0;
        }
        if ((this.NoFallTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 10000L)))
        {
            dumplog(player, "Count Reset");
            Count = 0;
            Time = System.currentTimeMillis();
        }
        if (Count >= 3)
        {
            dumplog(player, "Logged. Count: " + Count);
            Count = 0;

            this.FallDistance.put(player.getUniqueId(), Double.valueOf(0.0D));
            getOverwatch().logCheat(this, player, null, new String[0]);
        }
        this.NoFallTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Long.valueOf(Time), Integer.valueOf(Count)));
    }
}