package me.uniodex.uniofactions.commands;

import com.gamingmesh.jobs.Jobs;
import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdMeslek implements CommandExecutor {

    private UnioFactions plugin;

    public CmdMeslek(UnioFactions plugin) {
        this.plugin = plugin;
        plugin.getCommand("meslek").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("top10")) {
            plugin.getMenuManager().openTop10Inventory((Player) sender);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("top10")) {
            String job = args[1];
            if (Jobs.getJob(job) == null) {
                sender.sendMessage(plugin.getMessage("messages.jobNotExist"));
                return true;
            }
            plugin.getMenuManager().openTop10Inventory((Player) sender, job);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("ayarlar")) {
            plugin.getMenuManager().openSettingsInventory((Player) sender);
            return true;
        }

        plugin.getMenuManager().openJobsInventory((Player) sender);
        return true;
    }
}
