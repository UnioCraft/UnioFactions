package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdUniofactions implements CommandExecutor {

    private UnioFactions plugin;

    public CmdUniofactions(UnioFactions plugin) {
        this.plugin = plugin;
        plugin.getCommand("uniofactions").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("uniofactions.reload")) {
                    plugin.reload();
                    sender.sendMessage(plugin.getMessage("messages.configReloaded"));
                } else {
                    sender.sendMessage(plugin.getMessage("messages.noPermission"));
                }
            }
        }
        return true;
    }
}
