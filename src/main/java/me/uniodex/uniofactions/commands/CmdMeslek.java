package me.uniodex.uniofactions.commands;

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

        plugin.getMenuManager().openQuestInventory((Player) sender);
        return true;
    }
}
