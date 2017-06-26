package overwatch.getPlugin.check.movement;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

public class VClip extends Check
{
    public static List<Material> blocked;

    static {
        (VClip.blocked = new ArrayList<Material>()).add(Material.ACTIVATOR_RAIL);
        VClip.blocked.add(Material.AIR);
        VClip.blocked.add(Material.ANVIL);
        VClip.blocked.add(Material.BED_BLOCK);
        VClip.blocked.add(Material.BIRCH_WOOD_STAIRS);
        VClip.blocked.add(Material.BREWING_STAND);
        VClip.blocked.add(Material.BOAT);
        VClip.blocked.add(Material.BRICK_STAIRS);
        VClip.blocked.add(Material.BROWN_MUSHROOM);
        VClip.blocked.add(Material.CAKE_BLOCK);
        VClip.blocked.add(Material.CARPET);
        VClip.blocked.add(Material.CAULDRON);
        VClip.blocked.add(Material.COBBLESTONE_STAIRS);
        VClip.blocked.add(Material.COBBLE_WALL);
        VClip.blocked.add(Material.DARK_OAK_STAIRS);
        VClip.blocked.add(Material.DIODE);
        VClip.blocked.add(Material.DIODE_BLOCK_ON);
        VClip.blocked.add(Material.DIODE_BLOCK_OFF);
        VClip.blocked.add(Material.DEAD_BUSH);
        VClip.blocked.add(Material.DETECTOR_RAIL);
        VClip.blocked.add(Material.DOUBLE_PLANT);
        VClip.blocked.add(Material.DOUBLE_STEP);
        VClip.blocked.add(Material.DRAGON_EGG);
        VClip.blocked.add(Material.FENCE_GATE);
        VClip.blocked.add(Material.FENCE);
        VClip.blocked.add(Material.PAINTING);
        VClip.blocked.add(Material.FLOWER_POT);
        VClip.blocked.add(Material.GOLD_PLATE);
        VClip.blocked.add(Material.HOPPER);
        VClip.blocked.add(Material.STONE_PLATE);
        VClip.blocked.add(Material.IRON_PLATE);
        VClip.blocked.add(Material.HUGE_MUSHROOM_1);
        VClip.blocked.add(Material.HUGE_MUSHROOM_2);
        VClip.blocked.add(Material.IRON_DOOR_BLOCK);
        VClip.blocked.add(Material.IRON_DOOR);
        VClip.blocked.add(Material.IRON_FENCE);
        VClip.blocked.add(Material.IRON_PLATE);
        VClip.blocked.add(Material.ITEM_FRAME);
        VClip.blocked.add(Material.JUKEBOX);
        VClip.blocked.add(Material.JUNGLE_WOOD_STAIRS);
        VClip.blocked.add(Material.LADDER);
        VClip.blocked.add(Material.LEVER);
        VClip.blocked.add(Material.LONG_GRASS);
        VClip.blocked.add(Material.NETHER_FENCE);
        VClip.blocked.add(Material.NETHER_STALK);
        VClip.blocked.add(Material.NETHER_WARTS);
        VClip.blocked.add(Material.MELON_STEM);
        VClip.blocked.add(Material.PUMPKIN_STEM);
        VClip.blocked.add(Material.QUARTZ_STAIRS);
        VClip.blocked.add(Material.RAILS);
        VClip.blocked.add(Material.RED_MUSHROOM);
        VClip.blocked.add(Material.RED_ROSE);
        VClip.blocked.add(Material.SAPLING);
        VClip.blocked.add(Material.SEEDS);
        VClip.blocked.add(Material.SIGN);
        VClip.blocked.add(Material.SIGN_POST);
        VClip.blocked.add(Material.SKULL);
        VClip.blocked.add(Material.SMOOTH_STAIRS);
        VClip.blocked.add(Material.NETHER_BRICK_STAIRS);
        VClip.blocked.add(Material.SPRUCE_WOOD_STAIRS);
        VClip.blocked.add(Material.STAINED_GLASS_PANE);
        VClip.blocked.add(Material.REDSTONE_COMPARATOR);
        VClip.blocked.add(Material.REDSTONE_COMPARATOR_OFF);
        VClip.blocked.add(Material.REDSTONE_COMPARATOR_ON);
        VClip.blocked.add(Material.REDSTONE_LAMP_OFF);
        VClip.blocked.add(Material.REDSTONE_LAMP_ON);
        VClip.blocked.add(Material.REDSTONE_TORCH_OFF);
        VClip.blocked.add(Material.REDSTONE_TORCH_ON);
        VClip.blocked.add(Material.REDSTONE_WIRE);
        VClip.blocked.add(Material.SANDSTONE_STAIRS);
        VClip.blocked.add(Material.STEP);
        VClip.blocked.add(Material.ACACIA_STAIRS);
        VClip.blocked.add(Material.SUGAR_CANE);
        VClip.blocked.add(Material.SUGAR_CANE_BLOCK);
        VClip.blocked.add(Material.ENCHANTMENT_TABLE);
        VClip.blocked.add(Material.SOUL_SAND);
        VClip.blocked.add(Material.TORCH);
        VClip.blocked.add(Material.TRAP_DOOR);
        VClip.blocked.add(Material.TRIPWIRE);
        VClip.blocked.add(Material.TRIPWIRE_HOOK);
        VClip.blocked.add(Material.WALL_SIGN);
        VClip.blocked.add(Material.VINE);
        VClip.blocked.add(Material.WATER_LILY);
        VClip.blocked.add(Material.WEB);
        VClip.blocked.add(Material.WOOD_DOOR);
        VClip.blocked.add(Material.WOOD_DOUBLE_STEP);
        VClip.blocked.add(Material.WOOD_PLATE);
        VClip.blocked.add(Material.WOOD_STAIRS);
        VClip.blocked.add(Material.WOOD_STEP);
        VClip.blocked.add(Material.HOPPER);
        VClip.blocked.add(Material.WOODEN_DOOR);
        VClip.blocked.add(Material.YELLOW_FLOWER);
        VClip.blocked.add(Material.LAVA);
        VClip.blocked.add(Material.WATER);
        VClip.blocked.add(Material.STATIONARY_WATER);
        VClip.blocked.add(Material.STATIONARY_LAVA);
        VClip.blocked.add(Material.CACTUS);
        VClip.blocked.add(Material.CHEST);
        VClip.blocked.add(Material.PISTON_BASE);
        VClip.blocked.add(Material.PISTON_MOVING_PIECE);
        VClip.blocked.add(Material.PISTON_EXTENSION);
        VClip.blocked.add(Material.PISTON_STICKY_BASE);
        VClip.blocked.add(Material.TRAPPED_CHEST);
        VClip.blocked.add(Material.SNOW);
        VClip.blocked.add(Material.ENDER_CHEST);
        VClip.blocked.add(Material.THIN_GLASS);
    }

    public VClip(final Overwatch overwatch) {
        super("VClip", "VClip", overwatch);
        this.setBannable(false);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (player.getLocation().getY() < 0.0 || player.getLocation().getY() > player.getWorld().getMaxHeight()) {
            return;
        }
        final Location to = event.getTo().clone();
        final Location from = event.getFrom().clone();
        final double yDist = to.getY() - from.getY();
        int blocks = 0;
        if (yDist < -0.5 || yDist > 0.5) {
            for (int y = (int)Math.round(Math.abs(yDist)), i = 0; i < y; ++i) {
                final Location l = (yDist < -0.5) ? to.clone().add(0.0, (double)i, 0.0) : from.clone().add(0.0, (double)i, 0.0);
                if (l.getBlock() != null && l.getBlock().getType().isSolid() && l.getBlock().getType().isBlock() && l.getBlock().getType() != Material.AIR) {
                    if (VClip.blocked.contains(l.getBlock().getType())) {
                        this.dumplog(player, "Player tried to vclip through blocked material: " + l.getBlock().getType().name());
                    }
                    else {
                        this.dumplog(player, "Player tried to vclip through material: " + l.getBlock().getType().name());
                        ++blocks;
                    }
                }
            }
        }
        if (blocks > 0) {
            this.getOverwatch().logCheat(this, player, null, new StringBuilder(String.valueOf(blocks)).toString());
        }
    }
}