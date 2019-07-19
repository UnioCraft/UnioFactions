package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.managers.VIPManager.VIPType;
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
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("vipreward")) {
                if (sender.hasPermission("uniofactions.vipreward")) {
                    String target = args[1];
                    String vipType = args[2];

                    VIPType type = plugin.getVipManager().getVIPType(vipType);
                    if (type == null) {
                        sender.sendMessage(plugin.getMessage("messages.invalidVIPType"));
                        return true;
                    }

                    plugin.getVipManager().giveVIPReward(target, type);
                    sender.sendMessage(plugin.getMessage("messages.vipRewardHasGiven"));
                }
            }
        }
        return true;
    }
}
