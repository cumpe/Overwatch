package overwatch.getPlugin.commands;

import java.util.List;
import java.util.Iterator;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.check.Check;
import overwatch.getPlugin.utils.C;

public class OverwatchCommand implements CommandExecutor
{
    private Overwatch overwatch;

    public OverwatchCommand(final Overwatch overwatch) {
        super();
        this.overwatch = overwatch;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!sender.hasPermission("overwatch.admin")) {
            sender.sendMessage(String.valueOf(C.Red) + "No permission.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
            sender.sendMessage(String.valueOf(C.Gray) + "*** " + C.Red + "Overwatch (Helper) AntiCheat" + C.Gray + " ***");
            sender.sendMessage(" ");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " dump" + C.Gray + "   - Dump a check log of a player.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " toggle" + C.Gray + "  - Toggles a check on and off.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " bannable" + C.Gray + "  - Makes a check bannable or not.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " timer" + C.Gray + "  - Makes a check timer or not.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " alerts" + C.Gray + "  - Makes a check alert or not.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " ping" + C.Gray + "  - Get the ping of a player.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " reload" + C.Gray + " - Reloads config.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " toggle" + C.Gray + "  - Toggles a check on and off.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " bans" + C.Gray + " - Lists bans this restart.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " status" + C.Gray + " - Gets the status of Janitor.");
            sender.sendMessage(String.valueOf(C.Gray) + "/overwatch" + C.Reset + " status" + C.Gray + "  - Get the status of a player.");
            sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
            return true;
        }
        if (args.length >= 2) {
            if (args.length == 3) {
                final String lowerCase;
                switch (lowerCase = args[0].toLowerCase()) {
                    case "dump": {
                        final String playerName = args[1];
                        final String checkName = args[2];
                        Check check = null;
                        for (final Check checkcheck : this.overwatch.getChecks()) {
                            if (checkcheck.getIdentifier().equalsIgnoreCase(checkName)) {
                                check = checkcheck;
                            }
                        }
                        if (check == null) {
                            sender.sendMessage(String.valueOf(C.Red) + "This check does not exist!");
                            return true;
                        }
                        final String result = check.dump(playerName);
                        if (result == null) {
                            sender.sendMessage(String.valueOf(C.Red) + "Error creating dump file for player " + playerName + ".");
                            break;
                        }
                        sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "Dropped dump thread at " + C.Yellow + "/dumps/" + result + ".txt");
                        break;
                    }
                    default:
                        break;
                }
            }
            final String lowerCase2;
            switch (lowerCase2 = args[0].toLowerCase()) {
                case "bannable": {
                    String name = "";
                    for (int i = 1; i < args.length; ++i) {
                        name = String.valueOf(name) + args[i] + ((args.length - 1 == i) ? "" : " ");
                    }
                    for (final Check check : this.overwatch.getChecks()) {
                        if (check.getIdentifier().equalsIgnoreCase(name)) {
                            if (check.isBannable()) {
                                sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + " is now " + C.Green + "not bannable" + C.Gray + ".");
                                check.setBannable(false);
                                break;
                            }
                            sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + " is now " + C.Red + "bannable" + C.Gray + ".");
                            check.setBannable(true);
                            break;
                        }
                    }
                    break;
                }
                case "reload": {
                    this.overwatch.getMainConfig().reload();
                    sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The config(s) have been reloaded!");
                    break;
                }
                case "status": {
                    final String playerName2 = args[1];
                    final Player player = this.overwatch.getServer().getPlayer(playerName2);
                    if (player == null || !player.isOnline()) {
                        sender.sendMessage(String.valueOf(C.Red) + "This player is not online!");
                        return true;
                    }
                    sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
                    sender.sendMessage(String.valueOf(C.Gray) + "*** " + C.Red + "Status of " + player.getName() + C.Gray + " ***");
                    sender.sendMessage(" ");
                    final Map<Check, Integer> Checks = this.overwatch.getViolations(player);
                    if (Checks == null || Checks.isEmpty()) {
                        sender.sendMessage(String.valueOf(C.Gray) + "This player set off 0 checks. Yay!");
                    }
                    else {
                        for (final Check Check : Checks.keySet()) {
                            final Integer Violations = Checks.get(Check);
                            sender.sendMessage(String.valueOf(C.Gray) + Check.getName() + " - " + C.Red + Violations + " VL");
                        }
                    }
                    sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
                    break;
                }
                case "toggle": {
                    String name = "";
                    for (int i = 1; i < args.length; ++i) {
                        name = String.valueOf(name) + args[i] + ((args.length - 1 == i) ? "" : " ");
                    }
                    for (final Check check : this.overwatch.getChecks()) {
                        if (check.getIdentifier().equalsIgnoreCase(name)) {
                            if (check.isEnabled()) {
                                sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + " is now " + C.Red + "disabled" + C.Gray + ".");
                                check.setEnabled(false);
                                break;
                            }
                            sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + " is now " + C.Green + "enabled" + C.Gray + ".");
                            check.setEnabled(true);
                            break;
                        }
                    }
                    break;
                }
                case "ping": {
                    final String playerName2 = args[1];
                    final Player player = this.overwatch.getServer().getPlayer(playerName2);
                    if (player == null || !player.isOnline()) {
                        sender.sendMessage(String.valueOf(C.Red) + "This player doesn't exist or is not online!");
                        return true;
                    }
                    final int ping = this.overwatch.getLag().getPing(player);
                    sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Reset + "Overwatch" + C.Gray + " says your ping is " + C.Red + player.getName() + C.Gray + "'s ping is " + C.Yellow + ping + "ms" + C.Gray + ".");
                    break;
                }
                case "timer": {
                    String name = "";
                    for (int i = 1; i < args.length; ++i) {
                        name = String.valueOf(name) + args[i] + ((args.length - 1 == i) ? "" : " ");
                    }
                    for (final Check check : this.overwatch.getChecks()) {
                        if (check.getIdentifier().equalsIgnoreCase(name)) {
                            if (check.hasBanTimer()) {
                                sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + "'s autoban timer is now " + C.DRed + "DISABLED" + C.Gray + ".");
                                check.setAutobanTimer(false);
                                break;
                            }
                            sender.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + "The check " + check.getName() + "'s autoban timer is now " + C.DGreen + "ENABLED" + C.Gray + ".");
                            check.setAutobanTimer(true);
                            break;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
        else if (args.length == 1) {
            final String lowerCase3;
            switch (lowerCase3 = args[0].toLowerCase()) {
                case "status": {
                    sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
                    sender.sendMessage(String.valueOf(C.Gray) + "*** " + C.Red + "Overwatch (Helper) Anticheat" + C.Gray + " ***");
                    sender.sendMessage(" ");
                    sender.sendMessage(String.valueOf(C.Gray) + "TPS: " + C.Red + this.overwatch.getLag().getTPS());
                    sender.sendMessage(String.valueOf(C.Gray) + "Bans last restart: " + C.Red + this.overwatch.getNamesBanned().size());
                    sender.sendMessage(String.valueOf(C.Gray) + "Total Bans: " + C.Red + this.overwatch.getMainConfig().getConfig().getInt("bans"));
                    sender.sendMessage(" ");
                    final List<Check> autobanChecks = new ArrayList<Check>();
                    for (final Check check2 : this.overwatch.getChecks()) {
                        if (check2.isBannable() && !check2.hasBanTimer() && !check2.isJudgmentDay()) {
                            autobanChecks.add(check2);
                        }
                    }
                    final List<Check> timerChecks = new ArrayList<Check>();
                    for (final Check check3 : this.overwatch.getChecks()) {
                        if (check3.isBannable() && check3.hasBanTimer() && !check3.isJudgmentDay()) {
                            timerChecks.add(check3);
                        }
                    }
                    final List<Check> silentChecks = new ArrayList<Check>();
                    for (final Check check4 : this.overwatch.getChecks()) {
                        if (!check4.isBannable() && !check4.isJudgmentDay()) {
                            silentChecks.add(check4);
                        }
                    }
                    final List<Check> JDchecks = new ArrayList<Check>();
                    for (final Check check5 : this.overwatch.getChecks()) {
                        if (check5.isJudgmentDay()) {
                            JDchecks.add(check5);
                        }
                    }
                    String checks = String.valueOf(C.Gray) + "Autoban: ";
                    for (int j = 0; j < autobanChecks.size(); ++j) {
                        final Check check6 = autobanChecks.get(j);
                        checks = String.valueOf(checks) + (check6.isEnabled() ? C.Green : C.Red) + check6.getName() + ((autobanChecks.size() - 1 == j) ? "" : (String.valueOf(C.Gray) + ", "));
                    }
                    String checks2 = String.valueOf(C.Gray) + "Timer: ";
                    for (int k = 0; k < timerChecks.size(); ++k) {
                        final Check check7 = timerChecks.get(k);
                        checks2 = String.valueOf(checks2) + (check7.isEnabled() ? C.Green : C.Red) + check7.getName() + ((timerChecks.size() - 1 == k) ? "" : (String.valueOf(C.Gray) + ", "));
                    }
                    String checks3 = String.valueOf(C.Gray) + "Silent: ";
                    for (int l = 0; l < silentChecks.size(); ++l) {
                        final Check check8 = silentChecks.get(l);
                        checks3 = String.valueOf(checks3) + (check8.isEnabled() ? C.Green : C.Red) + check8.getName() + ((silentChecks.size() - 1 == l) ? "" : (String.valueOf(C.Gray) + ", "));
                    }
                    String checks4 = String.valueOf(C.Gray) + "Judgement Day: ";
                    for (int m = 0; m < JDchecks.size(); ++m) {
                        final Check check9 = JDchecks.get(m);
                        checks4 = String.valueOf(checks4) + (check9.isEnabled() ? C.Green : C.Red) + check9.getName() + ((JDchecks.size() - 1 == m) ? "" : (String.valueOf(C.Gray) + ", "));
                    }
                    sender.sendMessage(checks3);
                    sender.sendMessage(checks2);
                    sender.sendMessage(checks);
                    sender.sendMessage(checks4);
                    sender.sendMessage(String.valueOf(C.Gray) + C.Strike + "----------------------------------------------------");
                    break;
                }
                case "bans": {
                    String bans = String.valueOf(Overwatch.PREFIX) + C.Gray + "Bans this restart (" + this.overwatch.getNamesBanned().size() + "): ";
                    final List<Map.Entry<String, Check>> entrybans = new ArrayList<Map.Entry<String, Check>>(this.overwatch.getNamesBanned().entrySet());
                    for (int i2 = 0; i2 < entrybans.size(); ++i2) {
                        final Map.Entry<String, Check> entry = entrybans.get(i2);
                        bans = String.valueOf(bans) + C.Green + entry.getKey() + " (" + entry.getValue().getName() + ")" + ((this.overwatch.getNamesBanned().size() - 1 == i2) ? "" : (String.valueOf(C.Gray) + ", "));
                    }
                    sender.sendMessage(bans);
                    break;
                }
                case "ping": {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("You have to be a player to run this command!");
                        return true;
                    }
                    final Player player2 = (Player)sender;
                    final int ping2 = this.overwatch.getLag().getPing(player2);
                    player2.sendMessage(String.valueOf(Overwatch.PREFIX) + C.Gray + " thinks your ping is " + C.Yellow + ping2 + "ms" + C.Gray + ".");
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }
}