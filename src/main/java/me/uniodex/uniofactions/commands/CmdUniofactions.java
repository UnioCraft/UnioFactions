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
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("getrewards")) {
                String quest = args[1];

                if (!(sender instanceof Player)) {
                    return true;
                }

                Player targetPlayer = (Player) sender;
                if (targetPlayer == null) {
                    sender.sendMessage(plugin.getMessage("messages.playerIsNotOnline"));
                    return true;
                }

                if (!plugin.getQuestManager().isQuestExist(quest)) {
                    sender.sendMessage(plugin.getMessage("messages.questNotExist"));
                    return true;
                }

                if (!plugin.getQuestManager().isQuestCompleted(targetPlayer.getName(), quest)) {
                    sender.sendMessage(plugin.getMessage("messages.questIsNotCompleted"));
                    return true;
                }

                if (plugin.getQuestManager().isQuestRewarded(targetPlayer.getName(), quest)) {
                    sender.sendMessage(plugin.getMessage("messages.questAlreadyCompleted"));
                    return true;
                }

                plugin.getQuestManager().giveQuestRewards(targetPlayer.getName(), quest);

                if (!plugin.getQuestManager().isOneTimeOnly(quest)) {
                    plugin.getQuestManager().setQuestNotCompleted(targetPlayer.getName(), quest);
                } else {
                    plugin.getQuestManager().setQuestRewarded(targetPlayer.getName(), quest);
                }
                sender.sendMessage(plugin.getMessage("quests." + quest + ".successMessage"));
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

            if (args[0].equalsIgnoreCase("completequest")) {
                if (sender.hasPermission("uniofactions.completequest")) {
                    String quest = args[1];
                    String target = args[2];

                    Player targetPlayer = Bukkit.getPlayer(target);
                    if (targetPlayer == null) {
                        sender.sendMessage(plugin.getMessage("messages.playerIsNotOnline"));
                        return true;
                    }

                    if (!plugin.getQuestManager().isQuestExist(quest)) {
                        sender.sendMessage(plugin.getMessage("messages.questNotExist"));
                        return true;
                    }

                    if (plugin.getQuestManager().isQuestCompleted(target, quest)) {
                        sender.sendMessage(plugin.getMessage("messages.questAlreadyCompleted"));
                        return true;
                    }

                    plugin.getQuestManager().setQuestCompleted(target, quest);
                    targetPlayer.sendMessage(plugin.getMessage("messages.questCompletionInfo"));
                    sender.sendMessage(plugin.getMessage("messages.commandCompleteQuestSuccess"));
                }
            }
        }
        return true;
    }
}
