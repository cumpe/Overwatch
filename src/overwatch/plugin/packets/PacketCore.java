package overwatch.getPlugin.packets;

import com.comphenix.protocol.events.PacketListener;
import java.util.Iterator;
import org.bukkit.entity.Player;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.packets.events.*;
import overwatch.getPlugin.utils.UtilServer;

public class PacketCore
{
    public Overwatch overwatch;

    public PacketCore(final Overwatch overwatch) {
        super();
        this.overwatch = overwatch;
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                if (packet.getHandle() instanceof PacketPlayInUseEntity) {
                    final PacketPlayInUseEntity packetNMS = (PacketPlayInUseEntity)packet.getHandle();
                    if (packetNMS.c() == null) {
                        return;
                    }
                }
                final EnumWrappers.EntityUseAction type = (EnumWrappers.EntityUseAction)packet.getEntityUseActions().read(0);
                final int entityId = (int)packet.getIntegers().read(0);
                Entity entity = null;
                for (final Entity entityentity : UtilServer.getEntities(player.getWorld())) {
                    if (entityentity.getEntityId() == entityId) {
                        entity = entityentity;
                    }
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketUseEntityEvent(type, player, entity));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.POSITION_LOOK }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketPlayerEvent(player, (double)event.getPacket().getDoubles().read(0), (double)event.getPacket().getDoubles().read(1), (double)event.getPacket().getDoubles().read(2), (float)event.getPacket().getFloat().read(0), (float)event.getPacket().getFloat().read(1), PacketPlayerType.POSLOOK));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.LOOK }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketPlayerEvent(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), (float)event.getPacket().getFloat().read(0), (float)event.getPacket().getFloat().read(1), PacketPlayerType.LOOK));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.POSITION }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketPlayerEvent(player, (double)event.getPacket().getDoubles().read(0), (double)event.getPacket().getDoubles().read(1), (double)event.getPacket().getDoubles().read(2), player.getLocation().getYaw(), player.getLocation().getPitch(), PacketPlayerType.POSITION));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.ENTITY_ACTION }) {
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketEntityActionEvent(player, (int)packet.getIntegers().read(1)));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.KEEP_ALIVE }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketKeepAliveEvent(player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketSwingArmEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.HELD_ITEM_SLOT }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketHeldItemChangeEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketBlockPlacementEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.overwatch, new PacketType[] { PacketType.Play.Client.FLYING }) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent((Event)new PacketPlayerEvent(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), PacketPlayerType.FLYING));
            }
        });
    }
}