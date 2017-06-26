package overwatch.getPlugin.check.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

public class BedLeave
        extends Check
{
    public BedLeave(Overwatch overwatch)
    {
        super("BedLeave", "BedLeave", overwatch);

        setAutobanTimer(true);
    }

    @EventHandler
    public void CheckBedLeave(PlayerBedLeaveEvent event)
    {
        if (!getOverwatch().isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        Location pLoc = player.getLocation();
        for (int x = pLoc.getBlockX() - 10; x < pLoc.getBlockX() + 10; x++) {
            for (int y = pLoc.getBlockY() - 10; y < pLoc.getBlockY() + 10; y++) {
                for (int z = pLoc.getBlockZ() - 10; z < pLoc.getBlockZ() + 10; z++)
                {
                    Block b = new Location(pLoc.getWorld(), x, y, z).getBlock();
                    if ((b.getType().equals(Material.BED)) || (b.getType().equals(Material.BED_BLOCK))) {
                        return;
                    }
                }
            }
        }
        getOverwatch().logCheat(this, player, null, new String[0]);
    }
}
