package overwatch.getPlugin.check.combat;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;
import overwatch.getPlugin.utils.UtilTime;

public class Crits extends Check
{
    private Map<UUID, Map.Entry<Integer, Long>> CritTicks;
    private Map<UUID, Double> FallDistance;

    public Crits(final Overwatch overwatch) {
        super("Crits", "Crits", overwatch);
        this.CritTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.FallDistance = new HashMap<UUID, Double>();
        this.setAutobanTimer(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!e.getCause().equals((Object)EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        final Player player = (Player)e.getDamager();
        if (player.getAllowFlight()) {
            return;
        }
        if (this.getOverwatch().LastVelocity.containsKey(player.getUniqueId())) {
            return;
        }
        if (UtilCheat.slabsNear(player.getLocation())) {
            return;
        }
        final Location pL = player.getLocation().clone();
        pL.add(0.0, player.getEyeHeight() + 1.0, 0.0);
        if (UtilCheat.blocksNear(pL)) {
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.CritTicks.containsKey(player.getUniqueId())) {
            Count = this.CritTicks.get(player.getUniqueId()).getKey();
            Time = this.CritTicks.get(player.getUniqueId()).getValue();
        }
        if (!this.FallDistance.containsKey(player.getUniqueId())) {
            return;
        }
        final double realFallDistance = this.FallDistance.get(player.getUniqueId());
        if (player.getFallDistance() > 0.0 && !player.isOnGround() && realFallDistance == 0.0) {
            ++Count;
        }
        else {
            Count = 0;
        }
        if (this.CritTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(Time, 10000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count >= 2) {
            Count = 0;
            this.getOverwatch().logCheat(this, player, null, new String[0]);
        }
        this.CritTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    }

    @EventHandler
    public void Move(final PlayerMoveEvent e) {
        final Player Player = e.getPlayer();
        double Falling = 0.0;
        if (!Player.isOnGround() && e.getFrom().getY() > e.getTo().getY()) {
            if (this.FallDistance.containsKey(Player.getUniqueId())) {
                Falling = this.FallDistance.get(Player.getUniqueId());
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        this.FallDistance.put(Player.getUniqueId(), Falling);
    }
}