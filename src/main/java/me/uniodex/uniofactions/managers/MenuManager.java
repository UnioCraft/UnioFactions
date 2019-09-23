package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.menus.QuestMenu;
import org.bukkit.entity.Player;

public class MenuManager {

    private UnioFactions plugin;
    private QuestMenu questMenu;

    public MenuManager(UnioFactions plugin) {
        this.plugin = plugin;
        questMenu = new QuestMenu(plugin);
    }

    public void openQuestInventory(Player player) {
        player.openInventory(questMenu.getQuestMenu(player));
    }
}
