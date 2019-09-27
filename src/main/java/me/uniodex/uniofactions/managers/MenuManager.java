package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.menus.JobsMenu;
import me.uniodex.uniofactions.menus.QuestMenu;
import me.uniodex.uniofactions.menus.SettingsMenu;
import me.uniodex.uniofactions.menus.TopMenu;
import org.bukkit.entity.Player;

public class MenuManager {

    private UnioFactions plugin;
    private QuestMenu questMenu;
    private JobsMenu jobsMenu;
    private TopMenu topMenu;
    private SettingsMenu settingsMenu;

    public MenuManager(UnioFactions plugin) {
        this.plugin = plugin;
        jobsMenu = new JobsMenu(plugin);
        questMenu = new QuestMenu(plugin);
        topMenu = new TopMenu(plugin);
        settingsMenu = new SettingsMenu(plugin);
    }

    public void openQuestInventory(Player player) {
        player.openInventory(questMenu.getQuestMenu(player));
    }

    public void openJobsInventory(Player player) {
        player.openInventory(jobsMenu.getJobsMenu(player));
    }

    public void openSettingsInventory(Player player) {
        player.openInventory(settingsMenu.getSettingsInventory(player.getUniqueId().toString()));
    }

    public void openTop10Inventory(Player player) {
        player.openInventory(topMenu.getTopMenu());
    }

    public void openTop10Inventory(Player player, String job) {
        player.openInventory(topMenu.getTopMenu(job));
    }
}
