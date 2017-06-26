package overwatch.getPlugin.check.movement;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilCheat;
import overwatch.getPlugin.utils.UtilPlayer;

public class Step
        extends Check
{
    public Step(Overwatch overwatch)
    {
        super("Step", "Step", overwatch);

        setBannable(false);
    }

    public boolean isOnGround(Player player)
    {
        if (UtilPlayer.isOnClimbable(player)) {
            return false;
        }
        if (player.getVehicle() != null) {
            return false;
        }
        Material type = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER) && (type != Material.VINE)) {
            return true;
        }
        Location a = player.getLocation().clone();a.setY(a.getY() - 0.5D);
        type = a.getBlock().getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER) && (type != Material.VINE)) {
            return true;
        }
        a = player.getLocation().clone();a.setY(a.getY() + 0.5D);
        type = a.getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER) && (type != Material.VINE)) {
            return true;
        }
        if (UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER })) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!this.isOnGround(player)) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        if (UtilCheat.slabsNear(player.getLocation())) {
            return;
        }
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            return;
        }
        if (this.getOverwatch().LastVelocity.containsKey(player.getUniqueId())) {
            return;
        }
        final double yDist = event.getTo().getY() - event.getFrom().getY();
        this.dumplog(player, "Height: " + yDist);
        if (yDist > 0.9) {
            this.dumplog(player, "Height (Logged): " + yDist);
            this.getOverwatch().logCheat(this, player, null, new StringBuilder(String.valueOf(Math.round(yDist))).toString());
        }
    }
}