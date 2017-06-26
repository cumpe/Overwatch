package overwatch.getPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import overwatch.getPlugin.Overwatch;
import overwatch.getPlugin.utils.C;

public class AlertsCommand
        implements CommandExecutor
{
    private Overwatch overwatch;

    public AlertsCommand(Overwatch overwatch)
    {
        this.overwatch = overwatch;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("You have to be a player to run this command!");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("janitor.staff"))
        {
            sender.sendMessage(C.Red + "No permission.");
            return true;
        }
        if (this.overwatch.hasAlertsOn(player))
        {
            this.overwatch.toggleAlerts(player);
            player.sendMessage(Overwatch.PREFIX + "Alerts toggled " + C.DRed + "OFF");
        }
        else
        {
            this.overwatch.toggleAlerts(player);
            player.sendMessage(Overwatch.PREFIX + "Alerts toggled " + C.DGreen + "ON");
        }
        return true;
    }
}
