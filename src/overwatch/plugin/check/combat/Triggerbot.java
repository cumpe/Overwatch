package overwatch.getPlugin.check.combat;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilPlayer;

public class Triggerbot extends Check
{
    public Triggerbot(final Overwatch overwatch) {
        super("Triggerbot1", "Triggerbot (Type A)", overwatch);
        this.setBannable(false);
        this.setMaxViolations(30);
        this.setViolationsToNotify(5);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (!e.getCause().equals((Object) EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        final Player damager = (Player)e.getDamager();
        final Player player = (Player)e.getEntity();
        if (damager.getAllowFlight()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        double MaxReach = 4.1 + player.getVelocity().length() * 4.0;
        if (damager.getVelocity().length() > 0.08) {
            MaxReach += damager.getVelocity().length();
        }
        final double Reach = UtilPlayer.getEyeLocation(damager).distance(player.getLocation());
        final int Ping = this.getOverwatch().getLag().getPing(damager);
        if (Ping >= 100 && Ping < 200) {
            MaxReach += 0.2;
        }
        else if (Ping >= 200 && Ping < 250) {
            MaxReach += 0.4;
        }
        else if (Ping >= 250 && Ping < 300) {
            MaxReach += 0.7;
        }
        else if (Ping >= 300 && Ping < 350) {
            ++MaxReach;
        }
        else if (Ping >= 350 && Ping < 400) {
            MaxReach += 1.4;
        }
        else if (Ping > 400) {
            return;
        }
        this.dumplog(damager, "Reach: " + Reach + ", MaxReach: " + MaxReach + ", Damager Velocity: " + damager.getVelocity().length() + ", " + "Player Velocity: " + player.getVelocity().length() + ", Ping:" + Ping);
        if (damager.getLocation().getY() > player.getLocation().getY()) {
            final double Difference = damager.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 4.0;
        }
        else if (player.getLocation().getY() > damager.getLocation().getY()) {
            final double Difference = player.getLocation().getY() - damager.getLocation().getY();
            MaxReach += Difference / 4.0;
        }
        if (Reach > MaxReach) {
            this.getOverwatch().logCheat(this, damager, "Reach", new String[0]);
        }
    }

    public class ReachEntry
    {
        public Long LastTime;
        public List<Double> Reachs;

        public ReachEntry(final Long LastTime, final List<Double> Reachs) {
            super();
            this.Reachs = new ArrayList<Double>();
            this.LastTime = LastTime;
            this.Reachs = Reachs;
        }

        public Long getLastTime() {
            return this.LastTime;
        }

        public List<Double> getReachs() {
            return this.Reachs;
        }

        public void setLastTime(final Long LastTime) {
            this.LastTime = LastTime;
        }

        public void setReachs(final List<Double> Reachs) {
            this.Reachs = Reachs;
        }

        public void addReach(final Double Reach) {
            this.Reachs.add(Reach);
        }
    }
}