package overwatch.getPlugin.check.combat;


import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;

public class KillAuraE extends Check
{
    private Map<Player, Map.Entry<Integer, Long>> lastAttack;

    public KillAuraE(final Overwatch overwatch) {
        super("KillAuraE", "Kill Aura (MultiAura)", overwatch);
        this.lastAttack = new HashMap<Player, Map.Entry<Integer, Long>>();
        this.setBannable(false);
    }

    @Override
    public void onEnable() {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void Damage(final EntityDamageByEntityEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)e.getDamager();
        if (this.lastAttack.containsKey(player)) {
            final Integer entityid = this.lastAttack.get(player).getKey();
            final Long time = this.lastAttack.get(player).getValue();
            if (entityid != e.getEntity().getEntityId() && System.currentTimeMillis() - time < 5L) {
                this.getOverwatch().logCheat(this, player, "MultiAura", new String[0]);
            }
            this.lastAttack.remove(player);
        }
        else {
            this.lastAttack.put(player, new AbstractMap.SimpleEntry<Integer, Long>(e.getEntity().getEntityId(), System.currentTimeMillis()));
        }
    }
}