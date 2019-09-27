package me.uniodex.uniofactions.menus;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import me.uniodex.uniofactions.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class JobsMenu {

    private UnioFactions plugin;

    public JobsMenu(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public Inventory getJobsMenu(Player player) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, plugin.getMessage("messages.jobsGUITitle"));

        int itemSlot = 11;
        for (String job : plugin.getConfig().getConfigurationSection("jobs").getKeys(false)) {
            menu.withItem(itemSlot,
                    getJobsItem(player.getName(), job),
                    (player1, action, item) -> {
                        if (Utils.shouldJoin(action, item.getItemMeta().getLore())) {
                            player1.performCommand("jobs join " + job);
                            plugin.getMenuManager().openJobsInventory(player1);
                        } else if (Utils.shouldLeave(action, item.getItemMeta().getLore())) {
                            player1.performCommand("jobs leave " + job);
                            plugin.getMenuManager().openJobsInventory(player1);
                        }
                    },
                    InventoryMenuBuilder.ALL_CLICK_TYPES);
            itemSlot++;
            if (itemSlot == 16) itemSlot = 20;
        }

        menu.withItem(39,
                getQuestsItem(),
                (player1, action, item) -> plugin.getMenuManager().openQuestInventory(player1),
                InventoryMenuBuilder.ALL_CLICK_TYPES);

        menu.withItem(40,
                getTop10Item(),
                (player1, action, item) -> plugin.getMenuManager().openTop10Inventory(player1),
                InventoryMenuBuilder.ALL_CLICK_TYPES);

        menu.withItem(41,
                getSettingsItem(),
                (player1, action, item) -> plugin.getMenuManager().openSettingsInventory(player1),
                InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private ItemStack getJobsItem(String player, String job) {
        ItemStack guiItem = Utils.stringToItemStack(plugin.getConfig().getString("jobs." + job + ".guiItem"));
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + plugin.getMessage("jobs." + job + ".menuDisplayName"));
        String loreStr = plugin.getMessage("messages.jobMenuLore")
                .replaceAll("%jobInfo%", plugin.getMessage("jobs." + job + ".info"))
                .replaceAll("%jobLevel%", getJobLevel(player, job))
                .replaceAll("%clickAction%", getClickAction(player, job));

        List<String> lore = Arrays.asList(loreStr.split("\n"));
        meta.setLore(lore);

        Utils.hideFlags(meta);
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    private ItemStack getQuestsItem() {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getMessage("messages.jobsGUIQuestsItemName"));
        meta.setLore(plugin.getMessages("messages.jobsGUIQuestsItemLore"));
        Utils.hideFlags(meta);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getSettingsItem() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getMessage("messages.jobsGUISettingsItemName"));
        meta.setLore(plugin.getMessages("messages.jobsGUISettingsItemLore"));
        Utils.hideFlags(meta);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getTop10Item() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getMessage("messages.jobsGUITop10ItemName"));
        meta.setLore(plugin.getMessages("messages.jobsGUITop10ItemLore"));
        Utils.hideFlags(meta);
        item.setItemMeta(meta);
        return item;
    }

    private String getJobLevel(String player, String job) {
        int level = 0;
        double experience = 0;
        double maxexp = 0;

        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        JobProgression jobProgression = jobsPlayer.getJobProgression(Jobs.getJob(job));

        if (jobProgression == null) {
            jobProgression = jobsPlayer.getArchivedJobProgression(Jobs.getJob(job));
        }

        if (jobProgression != null) {
            level = jobProgression.getLevel();
            experience = Math.round(jobProgression.getExperience() * 100.0) / 100.0;
            maxexp = jobProgression.getMaxExperience();
        }

        return plugin.getMessage("messages.jobLevel")
                .replaceAll("%jobLevel%", String.valueOf(level))
                .replaceAll("%jobExp%", String.valueOf(experience))
                .replaceAll("%jobMaxExp%", String.valueOf(maxexp));

    }

    private String getClickAction(String player, String job) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer.isInJob(Jobs.getJob(job))) {
            return plugin.getMessage("messages.jobRightClickToLeft");
        } else {
            return plugin.getMessage("messages.jobLeftClickToJoin");
        }
    }
}
