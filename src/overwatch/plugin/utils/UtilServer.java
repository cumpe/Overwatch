package overwatch.getPlugin.utils;

import java.util.Iterator;
import java.util.Collection;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import java.util.ArrayList;
import org.bukkit.entity.Entity;
import java.util.List;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UtilServer
{
    public static Player[] getPlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static List<Entity> getEntities(final World world) {
        final List<Entity> entities = new ArrayList<Entity>();
        final net.minecraft.server.v1_7_R4.World nmsworld = (net.minecraft.server.v1_7_R4.World)((CraftWorld)world).getHandle();
        for (final Object o : new ArrayList<Object>(nmsworld.entityList)) {
            if (o instanceof net.minecraft.server.v1_7_R4.Entity) {
                final net.minecraft.server.v1_7_R4.Entity mcEnt = (net.minecraft.server.v1_7_R4.Entity)o;
                final Entity bukkitEntity = (Entity)mcEnt.getBukkitEntity();
                if (bukkitEntity == null) {
                    continue;
                }
                entities.add(bukkitEntity);
            }
        }
        return entities;
    }
}