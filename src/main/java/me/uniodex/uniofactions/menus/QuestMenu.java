package me.uniodex.uniofactions.menus;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.QuestProgression;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import me.uniodex.uniofactions.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class QuestMenu {

    private UnioFactions plugin;

    public QuestMenu(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public Inventory getQuestMenu(Player player) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(54, plugin.getMessage("messages.questsGUIitle"));

        int itemSlot = 0;
        for (String quest : plugin.getConfig().getConfigurationSection("quests").getKeys(false)) {
            if (!quest.contains("gunluk")) continue;
            menu.withItem(itemSlot,
                    getQuestItem(player.getName(), quest),
                    (player1, action, item) -> {
                        player1.performCommand("uniofactions getrewards " + quest);
                        player1.closeInventory();
                    },
                    InventoryMenuBuilder.ALL_CLICK_TYPES);
            itemSlot++;
        }

        itemSlot = 27;
        for (String quest : plugin.getConfig().getConfigurationSection("quests").getKeys(false)) {
            if (quest.contains("gunluk")) continue;
            menu.withItem(itemSlot,
                    getQuestItem(player.getName(), quest),
                    (player1, action, item) -> {
                        player1.performCommand("uniofactions getrewards " + quest);
                        player1.closeInventory();
                    },
                    InventoryMenuBuilder.ALL_CLICK_TYPES);
            itemSlot++;
        }

        return menu.build();
    }

    private ItemStack getQuestItem(String player, String quest) {
        ItemStack guiItem = Utils.stringToItemStack(plugin.getConfig().getString("quests." + quest + ".guiItem"));
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + plugin.getMessage("quests." + quest + ".menuDisplayName"));
        String loreStr = plugin.getMessage("messages.questMenuLore")
                .replaceAll("%questType%", getQuestType(quest))
                .replaceAll("%questObjectives%", getQuestObjectives(player, quest))
                .replaceAll("%questRewards%", getQuestRewards(quest))
                .replaceAll("%questAction%", getQuestAction(player, quest));

        List<String> lore = Arrays.asList(loreStr.split("\n"));
        meta.setLore(lore);

        Utils.hideFlags(meta);
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    private String getQuestType(String quest) {
        if (plugin.getConfig().getBoolean("quests." + quest + ".daily")) {
            return plugin.getMessage("messages.questIsDaily");
        } else {
            return plugin.getMessage("messages.questIsOneTimeOnly");
        }
    }

    private String getQuestObjectives(String player, String quest) {
        String objectives = "";
        for (String str : plugin.getMessages("quests." + quest + ".objectives")) {
            if (objectives == "") {
                objectives += str;
            } else {
                objectives += "\n" + str;
            }
        }

        List<Integer> values = plugin.getQuestManager().getQuestProgression(player, quest);

        for (int i = 0; i < values.size(); i++) {
            objectives = objectives.replaceAll("%completed" + (i + 1) + "%", String.valueOf(values.get(i)));
        }

        objectives = objectives.replaceAll("%completedall%", String.valueOf(plugin.getQuestManager().getTotalQuestProgression(player, quest)));

        int completedCount = Utils.countOccurences(objectives, "completed");

        for (int i = 0; i < completedCount; i++) {
            objectives = objectives.replaceAll("%completed" + (i + 1) + "%", "0");
        }
        return objectives;
    }

    private String getQuestRewards(String quest) {
        String rewards = "";
        for (String str : plugin.getMessages("quests." + quest + ".rewards")) {
            if (rewards == "") {
                rewards += str;
            } else {
                rewards += "\n" + str;
            }
        }
        return rewards;
    }

    private String getQuestAction(String player, String quest) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        String action = "";
        String job = plugin.getConfig().getString("quests." + quest + ".job");
        boolean questJobCompleted = false;

        Long validUntil = null;
        for (QuestProgression questProgression : jobsPlayer.getQuestProgressions()) {
            if (questProgression.getQuest().getRewardCmds().get(0).contains(quest)) {
                questJobCompleted = questProgression.isCompleted();
                validUntil = questProgression.getValidUntil();
            }
        }

        if (plugin.getQuestManager().isQuestRewarded(player, quest)) {
            action = plugin.getMessage("messages.questIsCompleted");
        } else if (plugin.getQuestManager().isQuestCompleted(player, quest)) {
            action = plugin.getMessage("messages.questClickToComplete");
        } else if (!plugin.getJobsManager().isPlayerInJob(player, job)) {
            action = plugin.getMessage("messages.questYouMustBeInJob").replaceAll("%job%", job).replaceAll("_", " ");
        } else if (!plugin.getQuestManager().isOneTimeOnly(quest) && questJobCompleted) {
            action = plugin.getMessage("messages.questIsCompleted");
            action += "\n\n";
            action += plugin.getMessage("messages.questRemainingTimeToReset")
                    .replaceAll("%remainingTime%", plugin.getQuestManager().getRemainingTime(validUntil));
        } else {
            action = plugin.getMessage("messages.questIsNotCompletedGui");
        }
        return action;
    }

}
