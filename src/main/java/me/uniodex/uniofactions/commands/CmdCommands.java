package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdCommands implements CommandExecutor {

    private UnioFactions plugin;

    public CmdCommands(UnioFactions plugin) {
        this.plugin = plugin;
        plugin.getCommand("commands").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        if (args.length > 0) page = Integer.valueOf(args[0]);

        sender.sendMessage(plugin.getMessage("messages.commandCommandsHeader").replaceAll("%page%", String.valueOf(page)).replaceAll("%maxpage%", String.valueOf(plugin.getMainManager().getCommandListPages().size())));

        if (plugin.getMainManager().getCommandListPages().get(page) == null) {
            page = 1;
        }

        for (String line : plugin.getMainManager().getCommandListPages().get(page)) {
            sender.sendMessage(Utils.colorizeMessage(line));
        }

        if (plugin.getMainManager().getCommandListPages().get(page + 1) != null) {
            sender.sendMessage(plugin.getMessage("messages.commandCommandsNextPage").replaceAll("%page%", String.valueOf((page + 1))));
        }

        return true;
    }
}
