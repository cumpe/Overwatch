package overwatch.getPlugin.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

public class FastBow extends Check
{
    public Map<UUID, Long> bowPull;

    public FastBow(final Overwatch overwatch) {
        super("FastBow", "FastBow", overwatch);
        this.bowPull = new HashMap<UUID, Long>();
        this.setViolationsToNotify(2);
        this.setMaxViolations(10);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void Interact(final PlayerInteractEvent e) {
        final Player Player = e.getPlayer();
        if (Player.getItemInHand() != null && Player.getItemInHand().getType().equals((Object)Material.BOW)) {
            this.bowPull.put(Player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onShoot(final ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)e.getEntity();
            if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                if (this.bowPull.containsKey(player.getUniqueId())) {
                    final Long time = System.currentTimeMillis() - this.bowPull.get(player.getUniqueId());
                    final double power = arrow.getVelocity().length();
                    Long timeLimit = 300L;
                    final int ping = this.getOverwatch().lag.getPing(player);
                    if (ping > 400) {
                        timeLimit = 150L;
                    }
                    if (power > 2.5 && time < timeLimit) {
                        this.getOverwatch().logCheat(this, player, null, new String[0]);
                    }
                }
            }
        }
    }
}