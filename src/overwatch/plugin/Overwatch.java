package overwatch.getPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.check.combat.*;
import overwatch.getPlugin.check.movement.*;
import overwatch.getPlugin.check.other.Crash;
import overwatch.getPlugin.check.other.MorePackets;
import overwatch.getPlugin.check.other.Sneak;
import overwatch.getPlugin.check.other.TabComplete;
import overwatch.getPlugin.commands.AlertsCommand;
import overwatch.getPlugin.commands.AutobanCommand;
import overwatch.getPlugin.commands.OverwatchCommand;
import overwatch.getPlugin.lag.LagCore;
import overwatch.getPlugin.packets.PacketCore;
import overwatch.getPlugin.update.UpdateEvent;
import overwatch.getPlugin.update.UpdateType;
import overwatch.getPlugin.update.Updater;
import overwatch.getPlugin.utils.*;
import overwatch.getPlugin.xray.XrayCatcher;

import java.util.*;

public class Overwatch extends JavaPlugin implements Listener
{
    public static Overwatch Instance;
    public static String PREFIX;
    public Updater updater;
    public PacketCore packet;
    public LagCore lag;
    public XrayCatcher xray;
    public List<Check> Checks;
    public Map<UUID, Map<Check, Integer>> Violations;
    public Map<UUID, Map<Check, Long>> ViolationReset;
    public List<Player> AlertsOn;
    public Map<Player, Map.Entry<Check, Long>> AutoBan;
    public Map<String, Check> NamesBanned;
    Random rand;
    public Config mainConfig;
    public TxtFile autobanMessages;
    public Map<UUID, Map.Entry<Long, Vector>> LastVelocity;

    static {
        Overwatch.PREFIX = C.Red + "[Overwatch] " + C.Gray;
    }

    public Overwatch() {
        super();
        this.Checks = new ArrayList<Check>();
        this.Violations = new HashMap<UUID, Map<Check, Integer>>();
        this.ViolationReset = new HashMap<UUID, Map<Check, Long>>();
        this.AlertsOn = new ArrayList<Player>();
        this.AutoBan = new HashMap<Player, Map.Entry<Check, Long>>();
        this.NamesBanned = new HashMap<String, Check>();
        this.rand = new Random();
        this.LastVelocity = new HashMap<UUID, Map.Entry<Long, Vector>>();
    }

    public void onEnable() {
        Overwatch.Instance = this;
        this.updater = new Updater(this);
        this.packet = new PacketCore(this);
        this.lag = new LagCore(this);
        this.xray = new XrayCatcher(this);
        this.Checks.add(new Spider(this));
        this.Checks.add(new Jesus(this));
        this.Checks.add(new Ascension(this));
        this.Checks.add(new NewAscension(this));
        this.Checks.add(new Speed(this));
        this.Checks.add(new Glide(this));
        this.Checks.add(new Fly(this));
        this.Checks.add(new Regen(this));
        this.Checks.add(new BedLeave(this));
        this.Checks.add(new NoFall(this));
        this.Checks.add(new Step(this));
        this.Checks.add(new VClip(this));
        this.Checks.add(new Phase(this));
        this.Checks.add(new DoubleClick(this));
        this.Checks.add(new KillAuraA(this));
        this.Checks.add(new KillAuraB(this));
        this.Checks.add(new KillAuraC(this));
        this.Checks.add(new KillAuraE(this));
        this.Checks.add(new AttackSpeed(this));
        this.Checks.add(new NoSwing(this));
        this.Checks.add(new FastBow(this));
        this.Checks.add(new Twitch(this));
        this.Checks.add(new VelocityVertical(this));
        this.Checks.add(new Crits(this));
        this.Checks.add(new Reach(this));
        this.Checks.add(new Triggerbot(this));
        this.Checks.add(new MorePackets(this));
        this.Checks.add(new overwatch.getPlugin.check.other.Timer(this));
        this.Checks.add(new Sneak(this));
        this.Checks.add(new TabComplete(this));
        this.Checks.add(new Crash(this));
        for (final Check check : this.Checks) {
            if (check.isEnabled()) {
                this.RegisterListener((Listener)check);
            }
        }
        this.RegisterListener((Listener)this);
        this.getCommand("alerts").setExecutor(new AlertsCommand(this));
        this.getCommand("autoban").setExecutor(new AutobanCommand(this));
        this.getCommand("overwatch").setExecutor(new OverwatchCommand(this));
        (this.mainConfig = new Config(this, "", "config")).setDefault("silentban", true);
        this.mainConfig.setDefault("hcfmode", false);
        this.mainConfig.setDefault("bans", 0);
        this.mainConfig.setDefault("xrayduration", 30);
        if (this.getMainConfig().getConfig().getBoolean("hcfmode")) {
            for (final Check Check : this.Checks) {
                if (Check.isBannable() && !Check.hasBanTimer() && !(Check instanceof Crash)) {
                    Check.setAutobanTimer(true);
                }
            }
        }
        this.autobanMessages = new TxtFile(this, "", "autobanmessages");
    }

    public List<Check> getChecks() {
        return new ArrayList<Check>(this.Checks);
    }

    public Map<String, Check> getNamesBanned() {
        return new HashMap<String, Check>(this.NamesBanned);
    }

    public List<Player> getAutobanQueue() {
        return new ArrayList<Player>(this.AutoBan.keySet());
    }

    public void removeFromAutobanQueue(final Player player) {
        this.AutoBan.remove(player);
    }

    public void removeViolations(final Player player) {
        this.Violations.remove(player.getUniqueId());
    }

    public boolean hasAlertsOn(final Player player) {
        return this.AlertsOn.contains(player);
    }

    public void toggleAlerts(final Player player) {
        if (this.hasAlertsOn(player)) {
            this.AlertsOn.remove(player);
        }
        else {
            this.AlertsOn.add(player);
        }
    }

    public Config getMainConfig() {
        return this.mainConfig;
    }

    public LagCore getLag() {
        return this.lag;
    }

    @EventHandler
    public void Join(final PlayerJoinEvent e) {
        if (!e.getPlayer().hasPermission("overwatch.staff")) {
            return;
        }
        this.AlertsOn.add(e.getPlayer());
    }

    @EventHandler
    public void autobanupdate(final UpdateEvent event) {
        if (!event.getType().equals(UpdateType.SEC)) {
            return;
        }
        final Map<Player, Map.Entry<Check, Long>> AutoBan = new HashMap<Player, Map.Entry<Check, Long>>(this.AutoBan);
        for (final Player player : AutoBan.keySet()) {
            if (player == null || !player.isOnline()) {
                this.AutoBan.remove(player);
            }
            else {
                final Long time = AutoBan.get(player).getValue();
                if (System.currentTimeMillis() < time) {
                    continue;
                }
                this.autobanOver(player);
            }
        }
        final Map<UUID, Map<Check, Long>> ViolationResets = new HashMap<UUID, Map<Check, Long>>(this.ViolationReset);
        for (final UUID uid : ViolationResets.keySet()) {
            if (!this.Violations.containsKey(uid)) {
                continue;
            }
            final Map<Check, Long> Checks = new HashMap<Check, Long>(ViolationResets.get(uid));
            for (final Check check : Checks.keySet()) {
                final Long time2 = Checks.get(check);
                if (System.currentTimeMillis() >= time2) {
                    this.ViolationReset.get(uid).remove(check);
                    this.Violations.get(uid).remove(check);
                }
            }
        }
    }

    public Integer getViolations(final Player player, final Check check) {
        if (this.Violations.containsKey(player.getUniqueId())) {
            return this.Violations.get(player.getUniqueId()).get(check);
        }
        return 0;
    }

    public Map<Check, Integer> getViolations(final Player player) {
        if (this.Violations.containsKey(player.getUniqueId())) {
            return new HashMap<Check, Integer>(this.Violations.get(player.getUniqueId()));
        }
        return null;
    }

    public void addViolation(final Player player, final Check check) {
        Map<Check, Integer> map = new HashMap<Check, Integer>();
        if (this.Violations.containsKey(player.getUniqueId())) {
            map = this.Violations.get(player.getUniqueId());
        }
        if (!map.containsKey(check)) {
            map.put(check, 1);
        }
        else {
            map.put(check, map.get(check) + 1);
        }
        this.Violations.put(player.getUniqueId(), map);
    }

    public void removeViolations(final Player player, final Check check) {
        if (this.Violations.containsKey(player.getUniqueId())) {
            this.Violations.get(player.getUniqueId()).remove(check);
        }
    }

    public void setViolationResetTime(final Player player, final Check check, final long time) {
        Map<Check, Long> map = new HashMap<Check, Long>();
        if (this.ViolationReset.containsKey(player.getUniqueId())) {
            map = this.ViolationReset.get(player.getUniqueId());
        }
        map.put(check, time);
        this.ViolationReset.put(player.getUniqueId(), map);
    }

    public void autobanOver(final Player player) {
        final Map<Player, Map.Entry<Check, Long>> AutoBan = new HashMap<Player, Map.Entry<Check, Long>>(this.AutoBan);
        if (AutoBan.containsKey(player)) {
            this.banPlayer(player, AutoBan.get(player).getKey());
            this.AutoBan.remove(player);
        }
    }

    public void autoban(final Check check, final Player player) {
        if (this.lag.getTPS() < 17.0) {
            return;
        }
        if (check.hasBanTimer()) {
            if (this.AutoBan.containsKey(player)) {
                return;
            }
            this.AutoBan.put(player, new AbstractMap.SimpleEntry<Check, Long>(check, System.currentTimeMillis() + 15000L));
            System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName() + " will be banned in 15s for " + check.getName() + ".");
            final UtilActionMessage msg = new UtilActionMessage();
            msg.addText(Overwatch.PREFIX);
            msg.addText(String.valueOf(C.Red) + player.getName()).addHoverText(String.valueOf(C.Gray) + "(Click to teleport to " + C.Red + player.getName() + C.Gray + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
            msg.addText(String.valueOf(C.Gray) + " will be banned for ");
            msg.addText(String.valueOf(C.Red) + check.getName());
            msg.addText(String.valueOf(C.Gray) + " in 15s. ");
            msg.addText(String.valueOf(C.Red) + C.Bold + "[F]").addHoverText(String.valueOf(C.Gray) + "(Click to freeze " + C.Red + player.getName() + C.Gray + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/freeze " + player.getName());
            msg.addText(" ");
            msg.addText(String.valueOf(C.DGray) + C.Bold + "[C]").addHoverText(String.valueOf(C.Gray) + "(Click to cancel " + C.Red + player.getName() + C.Gray + "'s autoban)").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban cancel " + player.getName());
            msg.addText(" ");
            msg.addText(String.valueOf(C.DRed) + C.Bold + "[B]").addHoverText(String.valueOf(C.Gray) + "(Click to ban " + C.Red + player.getName() + C.Gray + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban ban " + player.getName());
            Player[] players;
            for (int length = (players = UtilServer.getPlayers()).length, i = 0; i < length; ++i) {
                final Player playerplayer = players[i];
                if (playerplayer.hasPermission("overwatch.staff")) {
                    msg.sendToPlayer(playerplayer);
                }
            }
        }
        else {
            this.banPlayer(player, check);
        }
    }

    public void banPlayer(final Player player, final Check check) {
        this.NamesBanned.put(player.getName(), check);
        final boolean silentban = this.mainConfig.getConfig().getBoolean("silentban");
        this.Violations.remove(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "ban " + (silentban ? "-s " : "") + player.getName() + " [OverWatch] Cheating");
            }
        }, 10L);
        this.mainConfig.getConfig().set("bans", (Object)(this.mainConfig.getConfig().getInt("bans") + 1));
        this.mainConfig.save();
        final List<String> a = new ArrayList<String>();
        this.autobanMessages.readTxtFile();
        for (String line : this.autobanMessages.getLines()) {
            line = line.replaceAll("%player%", player.getName());
            line = line.replaceAll("&", C.Split);
            a.add(line);
        }
        if (a.size() > 0) {
            Bukkit.getServer().broadcastMessage((String)a.get(this.rand.nextInt(a.size())));
        }
    }

    public void alert(final String message) {
        for (final Player playerplayer : this.AlertsOn) {
            playerplayer.sendMessage(String.valueOf(Overwatch.PREFIX) + message);
        }
    }

    public void logCheat(final Check check, final Player player, final String hoverabletext, final String... identifiers) {
        String a = "";
        if (identifiers != null) {
            for (final String b : identifiers) {
                a = String.valueOf(a) + " (" + b + ")";
            }
        }
        this.addViolation(player, check);
        this.setViolationResetTime(player, check, System.currentTimeMillis() + check.getViolationResetTime());
        final Integer violations = this.getViolations(player, check);
        System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName() + " failed " + (check.isJudgmentDay() ? "JD check " : "") + check.getName() + a + " [" + violations + " VL]");
        if(violations > check.getViolationsToNotify()) {
            final UtilActionMessage msg = new UtilActionMessage();
            msg.addText(Overwatch.PREFIX);
            msg.addText(String.valueOf(C.Red) + player.getName()).addHoverText(String.valueOf(C.Gray) + "(Click to teleport to " + C.Aqua + player.getName() + C.Gray + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
            msg.addText(String.valueOf(C.Gray) + " failed " + (check.isJudgmentDay() ? "JD check " : ""));
            final UtilActionMessage.AMText CheckText = msg.addText(String.valueOf(C.Green) + check.getName());
            if (hoverabletext != null) {
                CheckText.addHoverText(hoverabletext);
            }
            msg.addText(String.valueOf(C.Red) + a + C.Gray + " ");
            msg.addText(String.valueOf(C.Gray) + "[" + C.Red + violations + C.Gray + " VL]");
            if (violations % check.getViolationsToNotify() == 0) {
                for (final Player playerplayer : this.AlertsOn) {
                    if (check.isJudgmentDay() && !playerplayer.hasPermission("overwatch.admin")) {
                        continue;
                    }
                    msg.sendToPlayer(playerplayer);
                }
            }if (check.isJudgmentDay()) {
                return;
            }
            if (violations > check.getMaxViolations() && check.isBannable()) {
                this.autoban(check, player);
            }
        }
    }

    public void onDisable() {
        this.updater.Disable();
    }

    public void RegisterListener(final Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, (Plugin)this);
    }

    public Map<UUID, Map.Entry<Long, Vector>> getLastVelocity() {
        return new HashMap<UUID, Map.Entry<Long, Vector>>(this.LastVelocity);
    }

    @EventHandler
    public void Velocity(final PlayerVelocityEvent event) {
        this.LastVelocity.put(event.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<Long, Vector>(System.currentTimeMillis(), event.getVelocity()));
    }

    @EventHandler
    public void Update(final UpdateEvent event) {
        if (!event.getType().equals(UpdateType.TICK)) {
            return;
        }
        for (final UUID uid : this.getLastVelocity().keySet()) {
            final Player player = this.getServer().getPlayer(uid);
            if (player == null || !player.isOnline()) {
                this.LastVelocity.remove(uid);
            }
            else {
                final Vector velocity = this.getLastVelocity().get(uid).getValue();
                final Long time = this.getLastVelocity().get(uid).getKey();
                if (time + 500L > System.currentTimeMillis()) {
                    continue;
                }
                final double velY = velocity.getY() * velocity.getY();
                final double Y = player.getVelocity().getY() * player.getVelocity().getY();
                if (Y < 0.02) {
                    this.LastVelocity.remove(uid);
                }
                else {
                    if (Y <= velY * 3.0) {
                        continue;
                    }
                    this.LastVelocity.remove(uid);
                }
            }
        }
    }

    @EventHandler
    public void Kick(final PlayerKickEvent event) {
        if (event.getReason().equals("Flying is not enabled on this server")) {
            this.alert(String.valueOf(C.Gray) + event.getPlayer().getName() + " was kicked for flying");
        }
    }
}