package overwatch.getPlugin.check.combat;

import org.bukkit.event.EventHandler;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketPlayerEvent;
import overwatch.getPlugin.packets.events.PacketPlayerType;

public class Twitch
        extends Check
{
    public Twitch(Overwatch overwatch)
    {
        super("Twitch", "Twitch", overwatch);

        setAutobanTimer(true);
    }

    @EventHandler
    public void Player(final PacketPlayerEvent e)
    {
        if (e.getType() != PacketPlayerType.LOOK) {
            return;
        }
        if ((e.getPitch() > 90.1F) || (e.getPitch() < -90.1F)) {
            getOverwatch().logCheat(this, e.getPlayer(), null, new String[0]);
        }
    }
}
