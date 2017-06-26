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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketPlayerEvent;
import overwatch.getPlugin.update.UpdateEvent;
import overwatch.getPlugin.update.UpdateType;
import overwatch.getPlugin.utils.UtilCheat;
import overwatch.getPlugin.utils.UtilTime;

public class VelocityVertical extends Check
{
    private Map<UUID, Map.Entry<Integer, Long>> VelocityTicks;
    private Map<Player, Map.Entry<Double, Long>> velocity;
    private Map<Player, Long> LastUpdate;

    public VelocityVertical(final Overwatch overwatch) {
        super("VelocityVertical", "Knockback Modifier", overwatch);
        this.VelocityTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.velocity = new HashMap<Player, Map.Entry<Double, Long>>();
        this.LastUpdate = new HashMap<Player, Long>();
        this.setBannable(false);
        this.setEnabled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void Knockback(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        final Player player = (Player)event.getEntity();
        if (this.velocity.containsKey(player)) {
            return;
        }
        final Location pL = player.getLocation().clone();
        pL.add(0.0, player.getEyeHeight() + 1.0, 0.0);
        if (UtilCheat.blocksNear(pL)) {
            return;
        }
        this.velocity.put(player, new AbstractMap.SimpleEntry<Double, Long>(player.getLocation().getY(), System.currentTimeMillis()));
    }

    @EventHandler
    public void Death(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (this.velocity.containsKey(player)) {
            this.velocity.remove(player);
        }
    }

    @EventHandler
    public void Move(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final double Y = event.getTo().getY();
        if (this.velocity.containsKey(player)) {
            final Map.Entry<Double, Long> ye = this.velocity.get(player);
            if (Y > ye.getKey()) {
                this.velocity.remove(player);
            }
        }
    }

    @EventHandler
    public void Packet(final PacketPlayerEvent event) {
        this.LastUpdate.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void Update(final UpdateEvent event) {
        if (!event.getType().equals(UpdateType.TICK)) {
            return;
        }
        for (final Player player : this.velocity.keySet()) {
            if (this.LastUpdate.containsKey(player)) {
                if (this.LastUpdate.get(player) == null) {
                    continue;
                }
                int Count = 0;
                long Time = System.currentTimeMillis();
                if (this.VelocityTicks.containsKey(player.getUniqueId())) {
                    Count = this.VelocityTicks.get(player.getUniqueId()).getKey();
                    Time = this.VelocityTicks.get(player.getUniqueId()).getValue();
                    if (UtilTime.elapsed(Time, 5000L)) {
                        Count = 0;
                        Time = System.currentTimeMillis();
                    }
                }
                final Map.Entry<Double, Long> ye = this.velocity.get(player);
                if (System.currentTimeMillis() >= ye.getValue() + 1000L) {
                    this.velocity.remove(player);
                    if (System.currentTimeMillis() - this.LastUpdate.get(player) < 60L) {
                        ++Count;
                        Time = System.currentTimeMillis();
                    }
                    else {
                        Count = 0;
                    }
                }
                if (Count > 3) {
                    Count = 0;
                    this.getOverwatch().logCheat(this, player, "Knockback Modifier", "Experimental");
                }
                this.VelocityTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
            }
        }
    }
}