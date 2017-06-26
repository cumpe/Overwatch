package overwatch.getPlugin.check.movement;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;
import overwatch.getPlugin.utils.UtilMath;
import overwatch.getPlugin.utils.UtilPlayer;
import overwatch.getPlugin.utils.UtilTime;

public class Speed
        extends Check
{
    public Speed(Overwatch overwatch)
    {
        super("Speed", "Speed", overwatch);

        setAutobanTimer(true);
    }

    private Map<UUID, Map.Entry<Integer, Long>> speedTicks = new HashMap();
    private Map<UUID, Map.Entry<Integer, Long>> tooFastTicks = new HashMap();

    public boolean isOnIce(final Player player) {
        final Location a = player.getLocation();
        a.setY(a.getY() - 1.0);
        if (a.getBlock().getType().equals((Object)Material.ICE)) {
            return true;
        }
        a.setY(a.getY() - 1.0);
        return a.getBlock().getType().equals((Object)Material.ICE);
    }


    public void onEnable() {}

    @EventHandler
    public void CheckSpeed(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getY() == event.getTo().getY()) &&
                (event.getFrom().getZ() == event.getFrom().getZ())) {
            return;
        }
        if (!getOverwatch().isEnabled()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (getOverwatch().LastVelocity.containsKey(player.getUniqueId())) {
            return;
        }
        int Count = 0;
        long Time = UtilTime.nowlong();
        if (this.speedTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        int TooFastCount = 0;
        if (this.tooFastTicks.containsKey(player.getUniqueId()))
        {
            double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()), UtilMath.getHorizontalVector(event.getTo().toVector()));
            double LimitXZ = 0.0D;
            if ((UtilPlayer.isOnGround(player)) && (player.getVehicle() == null)) {
                LimitXZ = 0.33D;
            } else {
                LimitXZ = 0.4D;
            }
            if (UtilCheat.slabsNear(player.getLocation())) {
                LimitXZ += 0.05D;
            }
            Location b = UtilPlayer.getEyeLocation(player);b.add(0.0D, 1.0D, 0.0D);
            if ((b.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(b.getBlock()))) {
                LimitXZ = 0.69D;
            }
            if (isOnIce(player)) {
                if ((b.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(b.getBlock()))) {
                    LimitXZ = 1.0D;
                } else {
                    LimitXZ = 0.75D;
                }
            }
            float speed = player.getWalkSpeed();LimitXZ += (speed > 0.2F ? speed * 10.0F * 0.33F : 0.0F);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    if (player.isOnGround()) {
                        LimitXZ += 0.06D * (effect.getAmplifier() + 1);
                    } else {
                        LimitXZ += 0.02D * (effect.getAmplifier() + 1);
                    }
                }
            }
            dumplog(player, "Speed XZ: " + OffsetXZ);
            if ((OffsetXZ > LimitXZ) && (!UtilTime.elapsed(((Long)((Map.Entry)this.tooFastTicks.get(player.getUniqueId())).getValue()).longValue(), 150L)))
            {
                TooFastCount = ((Integer)((Map.Entry)this.tooFastTicks.get(player.getUniqueId())).getKey()).intValue() + 1;
                dumplog(player, "New TooFastCount: " + TooFastCount);
            }
            else
            {
                TooFastCount = 0;
                dumplog(player, "TooFastCount Reset");
            }
        }
        if (TooFastCount > 6)
        {
            TooFastCount = 0;
            Count++;
            dumplog(player, "New Count: " + Count);
        }
        if ((this.speedTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 60000L)))
        {
            dumplog(player, "Count Reset");
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if (Count >= 3)
        {
            dumplog(player, "Logged for Speed. Count: " + Count);
            Count = 0;

            getOverwatch().logCheat(this, player, null, new String[0]);
        }
        this.tooFastTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(TooFastCount), Long.valueOf(System.currentTimeMillis())));
        this.speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}