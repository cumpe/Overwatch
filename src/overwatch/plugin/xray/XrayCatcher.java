package overwatch.getPlugin.xray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.utils.C;
import overwatch.getPlugin.utils.UtilTime;

public class XrayCatcher
        implements Listener, CommandExecutor
{
    public Overwatch overwatch;
    public List<Player> xrayCatcher = new ArrayList();
    public Map<UUID, Long> lastPatch = new HashMap();

    public XrayCatcher(Overwatch overwatch)
    {
        this.overwatch = overwatch;
        this.overwatch.getCommand("xray").setExecutor(this);

        this.overwatch.RegisterListener(this);
    }

    @EventHandler
    public void breakblock(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() != Material.DIAMOND_ORE) {
            return;
        }
        if (event.getBlock().hasMetadata("AlreadyDone")) {
            return;
        }
        Player player = event.getPlayer();
        if (this.lastPatch.containsKey(player.getUniqueId()))
        {
            Long time = (Long)this.lastPatch.get(player.getUniqueId());

            Long maxtime = Long.valueOf(this.overwatch.getMainConfig().getConfig().getInt("xrayduration") * 1000L);
            if (UtilTime.nowlong() - time.longValue() < maxtime.longValue()) {
                for (Player staff : this.xrayCatcher) {
                    staff.sendMessage(Overwatch.PREFIX + C.Reset + player.getName() + C.Gray + " might be using " + C.Blue + "Xray");
                }
            }
        }
        for (int x = -7; x < 7; x++) {
            for (int y = -7; y < 7; y++) {
                for (int z = -7; z < 7; z++)
                {
                    Location location = event.getBlock().getLocation().add(x, y, z);
                    if (location.getBlock().getType() == Material.DIAMOND_ORE) {
                        location.getBlock().setMetadata("AlreadyDone", new FixedMetadataValue(this.overwatch, Boolean.valueOf(true)));
                    }
                }
            }
        }
        this.lastPatch.put(player.getUniqueId(), Long.valueOf(UtilTime.nowlong()));
    }

    @EventHandler
    public void blockplace(BlockPlaceEvent event)
    {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() != Material.DIAMOND_ORE) {
            return;
        }
        event.getBlock().setMetadata("AlreadyDone", new FixedMetadataValue(this.overwatch, Boolean.valueOf(true)));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args)
    {
        if (!sender.hasPermission("janitor.staff"))
        {
            sender.sendMessage(C.Red + "No permission.");
            return true;
        }
        if (!(sender instanceof Player))
        {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        Player player = (Player)sender;
        if (this.xrayCatcher.contains(player))
        {
            sender.sendMessage(Overwatch.PREFIX + C.Gray + "Xray alerts " + C.Red + "OFF");
            this.xrayCatcher.remove(player);
        }
        else
        {
            sender.sendMessage(Overwatch.PREFIX + C.Gray + "Xray alerts " + C.Green + "ON");
            this.xrayCatcher.add(player);
        }
        return true;
    }
}
