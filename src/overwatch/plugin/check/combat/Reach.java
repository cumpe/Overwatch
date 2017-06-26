package overwatch.getPlugin.check.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.UtilMath;
import overwatch.getPlugin.utils.UtilPlayer;
import overwatch.getPlugin.utils.UtilTime;

public class Reach extends Check
{
    private Map<UUID, ReachEntry> ReachTicks;

    public Reach(final Overwatch overwatch) {
        super("Reach", "Reach", overwatch);
        this.ReachTicks = new HashMap<UUID, ReachEntry>();
        this.setEnabled(true);
        this.setAutobanTimer(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (!e.getCause().equals((Object)EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
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
        long Time = System.currentTimeMillis();
        List<Double> Reachs = new ArrayList<Double>();
        if (this.ReachTicks.containsKey(damager.getUniqueId())) {
            Time = this.ReachTicks.get(damager.getUniqueId()).getLastTime();
            Reachs = new ArrayList<Double>(this.ReachTicks.get(damager.getUniqueId()).getReachs());
        }
        double MaxReach = 4.0;
        if (damager.hasPotionEffect(PotionEffectType.SPEED)) {
            int Level = 0;
            for (final PotionEffect Effect : damager.getActivePotionEffects()) {
                if (Effect.getType().equals((Object)PotionEffectType.SPEED)) {
                    Level = Effect.getAmplifier();
                    break;
                }
            }
            MaxReach += (Level + 1) * 0.1;
            switch (Level) {
                case 0: {
                    MaxReach = 4.1;
                    break;
                }
                case 1: {
                    MaxReach = 4.2;
                    break;
                }
                case 2: {
                    MaxReach = 4.3;
                    break;
                }
                default: {
                    return;
                }
            }
        }
        if (player.getVelocity().length() > 0.08 || this.getOverwatch().LastVelocity.containsKey(player.getUniqueId())) {
            return;
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
            MaxReach += 0.8;
        }
        else if (Ping >= 300 && Ping < 350) {
            MaxReach += 1.2;
        }
        else if (Ping >= 350 && Ping < 400) {
            MaxReach += 1.5;
        }
        else if (Ping > 400) {
            return;
        }
        this.dumplog(damager, "Reach: " + Reach + ", MaxReach: " + MaxReach + ", Damager Velocity: " + damager.getVelocity().length() + ", " + "Player Velocity: " + player.getVelocity().length());
        if (damager.getLocation().getY() > player.getLocation().getY()) {
            final double Difference = damager.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 4.0;
        }
        else if (player.getLocation().getY() > damager.getLocation().getY()) {
            final double Difference = player.getLocation().getY() - damager.getLocation().getY();
            MaxReach += Difference / 4.0;
        }
        if (Reach > MaxReach) {
            Reachs.add(Reach);
            Time = System.currentTimeMillis();
        }
        if (this.ReachTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 25000L)) {
            Reachs.clear();
            Time = System.currentTimeMillis();
        }
        this.dumplog(damager, "Reach Count: " + Reachs.size());
        if (Reachs.size() > 3) {
            final Double AverageReach = UtilMath.averageDouble(Reachs);
            Double A = 6.0 - MaxReach;
            if (A < 0.0) {
                A = 0.0;
            }
            Double B = AverageReach - MaxReach;
            if (B < 0.0) {
                B = 0.0;
            }
            final int Level2 = (int)Math.round(B / A * 100.0);
            Reachs.clear();
            this.dumplog(damager, "Logged for Reach. Average Reach: " + AverageReach + ", Level: " + Level2 + "%, Max Reach: " + MaxReach);
            this.getOverwatch().logCheat(this, damager, null, String.valueOf(Level2) + "%");
        }
        this.ReachTicks.put(damager.getUniqueId(), new ReachEntry(Time, Reachs));
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