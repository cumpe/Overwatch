package overwatch.getPlugin.utils;

import org.bukkit.entity.Damageable;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.entity.Entity;
import java.util.List;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;
import java.util.Iterator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_7_R4.NBTTagList;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class UtilPlayer
{
    public static void clear(final Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        ((CraftPlayer)player).getHandle().inventory.b(new NBTTagList());
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(3.0f);
        player.setExhaustion(0.0f);
        player.setMaxHealth(20.0);
        player.setHealth(((Damageable)player).getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.getInventory().clear();
        player.getInventory().setHelmet((ItemStack)null);
        player.getInventory().setChestplate((ItemStack)null);
        player.getInventory().setLeggings((ItemStack)null);
        player.getInventory().setBoots((ItemStack)null);
        player.updateInventory();
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
    }

    public static Location getEyeLocation(final Player player) {
        final Location eye = player.getLocation();
        eye.setY(eye.getY() + player.getEyeHeight());
        return eye;
    }

    public static boolean isInWater(final Player player) {
        final Material m = player.getLocation().getBlock().getType();
        return m == Material.STATIONARY_WATER || m == Material.WATER;
    }

    public static boolean isOnClimbable(final Player player) {
        for (final Block block : UtilBlock.getSurrounding(player.getLocation().getBlock(), false)) {
            if (block.getType() == Material.LADDER || block.getType() == Material.VINE) {
                return true;
            }
        }
        return player.getLocation().getBlock().getType() == Material.LADDER || player.getLocation().getBlock().getType() == Material.VINE;
    }

    public static boolean isOnGround(final Player player) {
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            return true;
        }
        Location a = player.getLocation().clone();
        a.setY(a.getY() - 0.5);
        if (a.getBlock().getType() != Material.AIR) {
            return true;
        }
        a = player.getLocation().clone();
        a.setY(a.getY() + 0.5);
        return a.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR || UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER });
    }

    public static List<Entity> getNearbyRidables(final Location loc, final double distance) {
        final List<Entity> entities = new ArrayList<Entity>();
        for (final Entity entity : new ArrayList<Entity>(loc.getWorld().getEntities())) {
            if (!entity.getType().equals((Object)EntityType.HORSE) && !entity.getType().equals((Object)EntityType.BOAT)) {
                continue;
            }
            Bukkit.getServer().broadcastMessage(new StringBuilder(String.valueOf(entity.getLocation().distance(loc))).toString());
            if (entity.getLocation().distance(loc) > distance) {
                continue;
            }
            entities.add(entity);
        }
        return entities;
    }
}