package overwatch.getPlugin.check.movement;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

public class Spider
        extends Check
{
    public Spider(Overwatch overwatch)
    {
        super("Spider", "Spider", overwatch);

        setBannable(false);
        setMaxViolations(5);
        setViolationsToNotify(1);
        setViolationResetTime(60000L);
    }

    private Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap();

    @EventHandler
    public void CheckSpider(PlayerMoveEvent event)
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

        boolean ya = false;
        List<Material> Types = new ArrayList();
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
        for (Material Type : Types) {
            if ((Type.isSolid()) && (Type != Material.LADDER) && (Type != Material.VINE))
            {
                ya = true;
                break;
            }
        }
        if (OffsetY > 0.0D) {
            TotalBlocks += OffsetY;
        }
        if ((!ya) || (!UtilCheat.blocksNear(player))) {
            TotalBlocks = 0.0D;
        }
        if ((ya) && (
                (event.getFrom().getY() > event.getTo().getY()) || (UtilPlayer.isOnGround(player)))) {
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
        if ((ya) && (TotalBlocks > Limit))
        {
            if (MS > 500L)
            {
                getOverwatch().logCheat(this, player, null, new String[] { "Experimental" });
                Time = System.currentTimeMillis();
            }
        }
        else {
            Time = System.currentTimeMillis();
        }
        this.AscensionTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Long.valueOf(Time), Double.valueOf(TotalBlocks)));
    }
}
