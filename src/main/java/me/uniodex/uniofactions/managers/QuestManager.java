package me.uniodex.uniofactions.managers;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.container.QuestProgression;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.managers.ConfigManager.Config;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
        plugin.getConfigManager().saveConfig(Config.COMPLETEDQUESTS);
    }

    public void setQuestNotCompleted(String player, String quest) {
        List<String> completedQuests = plugin.getConfigManager().getConfig(Config.COMPLETEDQUESTS).getStringList(player);
        if (!completedQuests.contains(quest)) return;
        completedQuests.remove(quest);
        plugin.getConfigManager().getConfig(Config.COMPLETEDQUESTS).set(player, completedQuests);
        plugin.getConfigManager().saveConfig(Config.COMPLETEDQUESTS);
    }

    public boolean isQuestRewarded(String player, String quest) {
        List<String> rewardedQuests = plugin.getConfigManager().getConfig(Config.REWARDEDQUESTS).getStringList(player);
        if (rewardedQuests == null) return false;
        return rewardedQuests.contains(quest);
    }

    public void setQuestRewarded(String player, String quest) {
        List<String> rewardedQuests = plugin.getConfigManager().getConfig(Config.REWARDEDQUESTS).getStringList(player);
        if (rewardedQuests.contains(quest)) return;
        rewardedQuests.add(quest);
        plugin.getConfigManager().getConfig(Config.REWARDEDQUESTS).set(player, rewardedQuests);
        plugin.getConfigManager().saveConfig(Config.REWARDEDQUESTS);
    }

    private List<String> getQuestRewards(String questName) {
        return plugin.getConfig().getStringList("quests." + questName + ".rewardCommands");
    }

    public void giveQuestRewards(String player, String questName) {
        List<String> rewards = getQuestRewards(questName);
        for (String reward : rewards) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replaceAll("%player%", player));
        }
    }

    public boolean isQuestExist(String questName) {
        return plugin.getConfig().get("quests." + questName) != null;
    }

    public boolean isOneTimeOnly(String questName) {
        return !plugin.getConfig().getBoolean("quests." + questName + ".daily");
    }

    public List<Integer> getQuestProgression(String player, String quest) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        List<Integer> values = new ArrayList<>();

        for (QuestProgression questProgression : jobsPlayer.getQuestProgressions()) {
            if (questProgression.getQuest().getRewardCmds().get(0).contains(quest)) {
                for (QuestObjective questObjective : questProgression.getQuest().getObjectives().values()) {
                    values.add(questProgression.getAmountDone(questObjective));
                }
                break;
            }
        }
        return values;
    }

    public int getTotalQuestProgression(String player, String quest) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

        for (QuestProgression questProgression : jobsPlayer.getQuestProgressions()) {
            if (questProgression.getQuest().getRewardCmds().get(0).contains(quest)) {
                return questProgression.getTotalAmountDone();
            }
        }
        return 0;
    }

    public String getRemainingTime(Long validUntil) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime resetTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(validUntil), TimeZone.getDefault().toZoneId());
        LocalDateTime tempDateTime = LocalDateTime.from(today);

        long years = tempDateTime.until(resetTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(resetTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(resetTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);


        long hours = tempDateTime.until(resetTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(resetTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long finalMinutes = minutes;
        long finalHours = hours;
        long finalDays = days;
        long finalMonths = months;

        String finalDate = "";
        if (years >= 1) finalDate = finalDate + years + " yıl ";
        if (finalMonths >= 1) finalDate = finalDate + finalMonths + " ay ";
        if (finalDays >= 1) finalDate = finalDate + finalDays + " gün";
        if (finalDate == "") {
            if (finalHours >= 1) finalDate = finalDate + finalHours + " saat ";
            if (finalMinutes >= 1) finalDate = finalDate + finalMinutes + " dakika";
        }

        if (finalDate == "") {
            finalDate = "<1 dakika";
        }

        return finalDate;
    }
}
