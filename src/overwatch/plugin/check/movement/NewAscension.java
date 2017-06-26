package overwatch.getPlugin.check.movement;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;
import overwatch.getPlugin.utils.UtilMath;

public class NewAscension
        extends Check
{
    public NewAscension(Overwatch overwatch)
    {
        super("NewAscension", "NewAscension", overwatch);

        setEnabled(true);
        setAutobanTimer(true);
        setMaxViolations(10);
        setViolationsToNotify(2);
        setViolationResetTime(60000L);
    }

    private Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap();

    @EventHandler
    public void CheckAscension(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (event.getFrom().getY() >= event.getTo().getY()) {
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
        long Time = System.currentTimeMillis();
        double TotalBlocks = 0.0D;
        if (this.AscensionTicks.containsKey(player.getUniqueId()))
        {
            Time = ((Long)((Map.Entry)this.AscensionTicks.get(player.getUniqueId())).getKey()).longValue();
            TotalBlocks = Double.valueOf(((Double)((Map.Entry)this.AscensionTicks.get(player.getUniqueId())).getValue()).doubleValue()).doubleValue();
        }
        long MS = System.currentTimeMillis() - Time;
        double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()), UtilMath.getVerticalVector(event.getTo().toVector()));
        if (OffsetY > 0.0D) {
            TotalBlocks += OffsetY;
        }
        if (UtilCheat.blocksNear(player)) {
            TotalBlocks = 0.0D;
        }
        Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
        if (UtilCheat.blocksNear(a)) {
            TotalBlocks = 0.0D;
        }
        double Limit = 0.5D;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.JUMP))
                {
                    int level = effect.getAmplifier() + 1;
                    Limit += Math.pow(level + 4.2D, 2.0D) / 16.0D;
                    break;
                }
            }
        }
        if (TotalBlocks > Limit)
        {
            if (MS > 100L)
            {
                getOverwatch().logCheat(this, player, "Flying Upward " + TotalBlocks + " Blocks", new String[0]);
                Time = System.currentTimeMillis();
            }
        }
        else {
            Time = System.currentTimeMillis();
        }
        this.AscensionTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Long.valueOf(Time), Double.valueOf(TotalBlocks)));
    }
}
