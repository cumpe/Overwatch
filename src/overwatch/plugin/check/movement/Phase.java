package overwatch.getPlugin.check.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

public class Phase
        extends Check
{
    public static List<Material> blocked = new ArrayList();

    static
    {
        blocked.add(Material.ACTIVATOR_RAIL);
        blocked.add(Material.AIR);
        blocked.add(Material.ANVIL);
        blocked.add(Material.BED_BLOCK);
        blocked.add(Material.BIRCH_WOOD_STAIRS);
        blocked.add(Material.BREWING_STAND);
        blocked.add(Material.BOAT);
        blocked.add(Material.BRICK_STAIRS);
        blocked.add(Material.BROWN_MUSHROOM);
        blocked.add(Material.CAKE_BLOCK);
        blocked.add(Material.CARPET);
        blocked.add(Material.CAULDRON);
        blocked.add(Material.COBBLESTONE_STAIRS);
        blocked.add(Material.COBBLE_WALL);
        blocked.add(Material.DARK_OAK_STAIRS);
        blocked.add(Material.DIODE);
        blocked.add(Material.DIODE_BLOCK_ON);
        blocked.add(Material.DIODE_BLOCK_OFF);
        blocked.add(Material.DEAD_BUSH);
        blocked.add(Material.DETECTOR_RAIL);
        blocked.add(Material.DOUBLE_PLANT);
        blocked.add(Material.DOUBLE_STEP);
        blocked.add(Material.DRAGON_EGG);
        blocked.add(Material.FENCE_GATE);
        blocked.add(Material.FENCE);
        blocked.add(Material.PAINTING);
        blocked.add(Material.FLOWER_POT);
        blocked.add(Material.GOLD_PLATE);
        blocked.add(Material.HOPPER);
        blocked.add(Material.STONE_PLATE);
        blocked.add(Material.IRON_PLATE);
        blocked.add(Material.HUGE_MUSHROOM_1);
        blocked.add(Material.HUGE_MUSHROOM_2);
        blocked.add(Material.IRON_DOOR_BLOCK);
        blocked.add(Material.IRON_DOOR);
        blocked.add(Material.IRON_FENCE);
        blocked.add(Material.IRON_PLATE);
        blocked.add(Material.ITEM_FRAME);
        blocked.add(Material.JUKEBOX);
        blocked.add(Material.JUNGLE_WOOD_STAIRS);
        blocked.add(Material.LADDER);
        blocked.add(Material.LEVER);
        blocked.add(Material.LONG_GRASS);
        blocked.add(Material.NETHER_FENCE);
        blocked.add(Material.NETHER_STALK);
        blocked.add(Material.NETHER_WARTS);
        blocked.add(Material.MELON_STEM);
        blocked.add(Material.PUMPKIN_STEM);
        blocked.add(Material.QUARTZ_STAIRS);
        blocked.add(Material.RAILS);
        blocked.add(Material.RED_MUSHROOM);
        blocked.add(Material.RED_ROSE);
        blocked.add(Material.SAPLING);
        blocked.add(Material.SEEDS);
        blocked.add(Material.SIGN);
        blocked.add(Material.SIGN_POST);
        blocked.add(Material.SKULL);
        blocked.add(Material.SMOOTH_STAIRS);
        blocked.add(Material.NETHER_BRICK_STAIRS);
        blocked.add(Material.SPRUCE_WOOD_STAIRS);
        blocked.add(Material.STAINED_GLASS_PANE);
        blocked.add(Material.REDSTONE_COMPARATOR);
        blocked.add(Material.REDSTONE_COMPARATOR_OFF);
        blocked.add(Material.REDSTONE_COMPARATOR_ON);
        blocked.add(Material.REDSTONE_LAMP_OFF);
        blocked.add(Material.REDSTONE_LAMP_ON);
        blocked.add(Material.REDSTONE_TORCH_OFF);
        blocked.add(Material.REDSTONE_TORCH_ON);
        blocked.add(Material.REDSTONE_WIRE);
        blocked.add(Material.SANDSTONE_STAIRS);
        blocked.add(Material.STEP);
        blocked.add(Material.ACACIA_STAIRS);
        blocked.add(Material.ENCHANTMENT_TABLE);
        blocked.add(Material.SUGAR_CANE);
        blocked.add(Material.SUGAR_CANE_BLOCK);
        blocked.add(Material.SOUL_SAND);
        blocked.add(Material.TORCH);
        blocked.add(Material.TRAP_DOOR);
        blocked.add(Material.TRIPWIRE);
        blocked.add(Material.TRIPWIRE_HOOK);
        blocked.add(Material.WALL_SIGN);
        blocked.add(Material.VINE);
        blocked.add(Material.WATER_LILY);
        blocked.add(Material.WEB);
        blocked.add(Material.WOOD_DOOR);
        blocked.add(Material.WOOD_DOUBLE_STEP);
        blocked.add(Material.WOOD_PLATE);
        blocked.add(Material.WOOD_STAIRS);
        blocked.add(Material.WOOD_STEP);
        blocked.add(Material.HOPPER);
        blocked.add(Material.WOODEN_DOOR);
        blocked.add(Material.YELLOW_FLOWER);
        blocked.add(Material.LAVA);
        blocked.add(Material.WATER);
        blocked.add(Material.STATIONARY_WATER);
        blocked.add(Material.STATIONARY_LAVA);
        blocked.add(Material.CACTUS);
        blocked.add(Material.CHEST);
        blocked.add(Material.PISTON_BASE);
        blocked.add(Material.PISTON_MOVING_PIECE);
        blocked.add(Material.PISTON_EXTENSION);
        blocked.add(Material.PISTON_STICKY_BASE);
        blocked.add(Material.TRAPPED_CHEST);
        blocked.add(Material.SNOW);
        blocked.add(Material.ENDER_CHEST);
        blocked.add(Material.THIN_GLASS);
    }

    public Phase(Overwatch overwatch)
    {
        super("Phase", "Phase", overwatch);
        setBannable(false);
    }

    public Map<Player, Long> lastlog = new HashMap();

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if ((player.getLocation().getY() < 0.0D) || (player.getLocation().getY() > player.getWorld().getMaxHeight())) {
            return;
        }
        if ((this.lastlog.containsKey(player)) &&
                (System.currentTimeMillis() - ((Long)this.lastlog.get(player)).longValue() < 500L)) {
            return;
        }
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();

        double xDist = to.getX() - from.getX();

        int blocks = 0;
        if ((xDist < -0.5D) || (xDist > 0.5D))
        {
            int x = (int)Math.round(Math.abs(xDist));
            for (int i = 0; i < x; i++)
            {
                Location l = xDist < -0.5D ? to.clone().add(i, 0.0D, 0.0D) : from.clone().add(i, 0.0D, 0.0D);
                if ((l.getBlock() != null) && (l.getBlock().getType().isSolid()) && (l.getBlock().getType().isBlock()) && (l.getBlock().getType() != Material.AIR)) {
                    if (blocked.contains(l.getBlock().getType()))
                    {
                        dumplog(player, "(#1) (X) Player tried to phase through blocked material: " + l.getBlock().getType().name());
                    }
                    else
                    {
                        dumplog(player, "(#1) (X) Player tried to phase through material: " + l.getBlock().getType().name());
                        blocks++;
                    }
                }
            }
        }
        double zDist = to.getX() - from.getX();
        if ((zDist < -0.5D) || (zDist > 0.5D))
        {
            int z = (int)Math.round(Math.abs(xDist));
            for (int i = 0; i < z; i++)
            {
                Location l = zDist < -0.5D ? to.clone().add(0.0D, 0.0D, i) : from.clone().add(0.0D, 0.0D, i);
                if ((l.getBlock() != null) && (l.getBlock().getType().isSolid()) && (l.getBlock().getType().isBlock()) && (l.getBlock().getType() != Material.AIR)) {
                    if (blocked.contains(l.getBlock().getType()))
                    {
                        dumplog(player, "(#1) (Z) Player tried to phase through blocked material: " + l.getBlock().getType().name());
                    }
                    else
                    {
                        dumplog(player, "(#1) (Z) Player tried to phase through material: " + l.getBlock().getType().name());
                        blocks++;
                    }
                }
            }
        }
        if (blocks > 0) {
            this.getOverwatch().logCheat(this, player, null, "#1", new StringBuilder(String.valueOf(blocks)).toString());
            this.lastlog.put(player, System.currentTimeMillis());
            return;
        }
        if ((event.getFrom().getBlock() != null) && (event.getFrom().getBlock().getType().isSolid()) && (event.getFrom().getBlock().getType().isBlock()) &&
                (event.getFrom().getBlock().getType() != Material.AIR)) {
            if (blocked.contains(event.getFrom().getBlock().getType()))
            {
                dumplog(player, "(#2) Player tried to phase through blocked material: " + event.getFrom().getBlock().getType().name());
            }
            else if ((event.getTo().getBlock() != event.getFrom().getBlock()) && (event.getTo().getBlock() != null))
            {
                dumplog(player, "(#2) Player tried to phase through. from material: " + event.getFrom().getBlock().getType().name() + "." +
                        " to material: " + event.getTo().getBlock().getType().name());

                getOverwatch().logCheat(this, player, null, new String[] { "#2" });
                this.lastlog.put(player, Long.valueOf(System.currentTimeMillis()));
            }
        }
    }
}
