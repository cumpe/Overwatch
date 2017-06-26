package overwatch.getPlugin.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.packets.events.PacketUseEntityEvent;

public class DoubleClick extends Check
{
    private Map<UUID, Long[]> LastMSCPS;

    public DoubleClick(final Overwatch overwatch) {
        super("DoubleClick", "DoubleClick", overwatch);
        this.LastMSCPS = new HashMap<UUID, Long[]>();
        this.setBannable(false);
        this.setViolationsToNotify(2);
        this.setMaxViolations(50);
    }

    @Override
    public void onEnable() {
    }

    @EventHandler
    public void UseEntity(final PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        if (!(e.getAttacked() instanceof Player)) {
            return;
        }
        final Player damager = e.getAttacker();
        Long first = 0L;
        Long second = 0L;
        if (this.LastMSCPS.containsKey(damager.getUniqueId())) {
            first = this.LastMSCPS.get(damager.getUniqueId())[0];
            second = this.LastMSCPS.get(damager.getUniqueId())[1];
        }
        if (first == 0L) {
            first = System.currentTimeMillis();
        }
        else if (second == 0L) {
            second = System.currentTimeMillis();
            first = System.currentTimeMillis() - first;
        }
        else {
            second = System.currentTimeMillis() - second;
            if (first > 50L && second == 0L) {
                this.getOverwatch().logCheat(this, damager, null, new String[0]);
            }
            first = 0L;
            second = 0L;
        }
        this.LastMSCPS.put(damager.getUniqueId(), new Long[] { first, second });
    }
}