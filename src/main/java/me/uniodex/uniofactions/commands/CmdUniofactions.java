package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.managers.VIPManager.VIPType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            if (args[0].equalsIgnoreCase("jobreward")) {
                if (sender.hasPermission("uniofactions.jobreward")) {
                    String quest = args[1];
                    String target = args[2];

                    Player targetPlayer = Bukkit.getPlayer(target);
                    if (targetPlayer == null) {
                        sender.sendMessage(plugin.getMessage("messages.commandJobRewardNoPlayer"));
                        return true;
                    }

                    if (plugin.getQuestManager().isQuestExist(quest)) {
                        sender.sendMessage(plugin.getMessage("messages.commandJobRewardNoQuest"));
                        return true;
                    }

                    if (plugin.getQuestManager().isQuestCompleted(target, quest)) {
                        sender.sendMessage(plugin.getMessage("messages.commandJobRewardOneTime"));
                        return true;
                    }

                    if (plugin.getQuestManager().isOneTimeOnly(quest)) {
                        plugin.getQuestManager().setQuestCompleted(target, quest);
                    }

                    plugin.getQuestManager().giveQuestRewards(target, quest);
                    sender.sendMessage(plugin.getMessage("messages.commandJobRewardSuccess"));
                }
            }
        }
        return true;
    }
}
