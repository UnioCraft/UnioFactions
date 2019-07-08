package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdChatcolor implements CommandExecutor {

    private UnioFactions plugin;

    public CmdChatcolor(UnioFactions plugin) {
        this.plugin = plugin;
        plugin.getCommand("chatcolor").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(UnioFactions.hataPrefix + "Bunu yapabilmek için oyuncu olmalısınız!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("uniofactions.chatcolor")) {
            player.sendMessage(plugin.getMessage("messages.noPermission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getMessage("messages.commandChatColorUsage"));
            return true;
        }

        ChatColor color = Utils.stringToChatColor(args[0]);

        if (color == null) {
            player.sendMessage(plugin.getMessage("messages.commandChatColorWrongColor"));
            return true;
        }

        plugin.getChatColorManager().setChatColor(player.getName(), color);
        player.sendMessage(plugin.getMessage("messages.commandChatColorSuccess").replaceAll("%s", args[0]));
        return true;
    }
}
