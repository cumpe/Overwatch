package overwatch.getPlugin.check.other;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class TabComplete extends Check
{
    public Map<UUID, Long> TabComplete;

    public TabComplete(final Overwatch janitor) {
        super("TabComplete", "TabComplete", janitor);
        this.TabComplete = new HashMap<UUID, Long>();
        this.setBannable(false);
    }

    @EventHandler
    public void TabCompleteEvent(final PlayerChatTabCompleteEvent e) {
        final String[] Args = e.getChatMessage().split(" ");
        final Player Player = e.getPlayer();
        if (Args[0].startsWith(".") && Args[0].substring(1, 2).equalsIgnoreCase("/")) {
            return;
        }
        if (Args.length > 1 && (Args[0].startsWith(".") || Args[0].startsWith("-") || Args[0].startsWith("#") || Args[0].startsWith("*"))) {
            if (this.TabComplete.containsKey(Player.getUniqueId()) && System.currentTimeMillis() < this.TabComplete.get(Player.getUniqueId()) + 1000L) {
                return;
            }
            this.getOverwatch().logCheat(this, Player, null, e.getChatMessage());
            this.TabComplete.put(Player.getUniqueId(), System.currentTimeMillis());
        }
    }
}