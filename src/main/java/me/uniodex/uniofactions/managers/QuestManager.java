package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.managers.ConfigManager.Config;
import org.bukkit.Bukkit;

import java.util.List;

public class QuestManager {

    private UnioFactions plugin;

    public QuestManager(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public boolean isQuestCompleted(String player, String quest) {
        List<String> completedQuests = plugin.getConfigManager().getConfig(Config.COMPLETEDQUESTS).getStringList(player);
        if (completedQuests == null) return false;
        return completedQuests.contains(quest);
    }

    public void setQuestCompleted(String player, String quest) {
        List<String> completedQuests = plugin.getConfigManager().getConfig(Config.COMPLETEDQUESTS).getStringList(player);
        if (completedQuests.contains(quest)) return;
        completedQuests.add(quest);
        plugin.getConfigManager().getConfig(Config.COMPLETEDQUESTS).set(player, completedQuests);
    }

    private List<String> getQuestRewards(String questName) {
        return plugin.getConfig().getStringList("questRewards." + questName);
    }

    public void giveQuestRewards(String player, String questName) {
        List<String> rewards = getQuestRewards(questName);
        for (String reward : rewards) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replaceAll("%player%", player));
        }
    }

    public boolean isQuestExist(String questName) {
        return plugin.getConfig().getStringList("questRewards." + questName) != null;
    }

    public boolean isOneTimeOnly(String questName) {
        return plugin.getConfig().getStringList("oneTimeQuests").contains(questName);
    }
}
