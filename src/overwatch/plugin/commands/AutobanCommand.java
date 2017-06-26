package overwatch.getPlugin.commands;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.utils.C;

public class AutobanCommand
        implements CommandExecutor
{
    private Overwatch overwatch;

    public AutobanCommand(Overwatch overwatch)
    {
        this.overwatch = overwatch;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!sender.hasPermission("janitor.staff")) {
            sender.sendMessage(String.valueOf(C.Red) + "No permission.");
            return true;
        }
        if (args.length == 2) {
            final String type = args[0];
            final String playerName = args[1];
            final Player player = Bukkit.getServer().getPlayer(playerName);
            if (player == null || !player.isOnline()) {
                sender.sendMessage(String.valueOf(C.Red) + "This player does not exist.");
                return true;
            }
            if (this.overwatch.getAutobanQueue().contains(player)) {
                final String lowerCase;
                switch (lowerCase = type.toLowerCase()) {
                    case "cancel": {
                        System.out.println("[" + player.getUniqueId().toString() + "] " + sender.getName() + "'s auto-ban has been cancelled by " + sender.getName());
                        Bukkit.broadcast(String.valueOf(Overwatch.PREFIX) + C.Red + player.getName() + C.Gray + "'s auto-ban has been cancelled by " + C.Red + sender.getName(), "janitor.staff");
                        break;
                    }
                    case "ban": {
                        System.out.println("[" + player.getUniqueId().toString() + "] " + sender.getName() + "'s auto-ban has been forced by " + sender.getName());
                        Bukkit.broadcast(String.valueOf(Overwatch.PREFIX) + C.Red + player.getName() + C.Gray + "'s auto-ban has been forced by " + C.Red + sender.getName(), "janitor.staff");
                        this.overwatch.autobanOver(player);
                        break;
                    }
                    default:
                        break;
                }
                this.overwatch.removeFromAutobanQueue(player);
                this.overwatch.removeViolations(player);
            }
            else {
                sender.sendMessage(String.valueOf(C.Red) + "This player is not in the autoban queue!");
            }
        }
        return true;
    }
}