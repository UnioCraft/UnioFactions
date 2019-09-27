package me.uniodex.uniofactions.menus;

import com.gamingmesh.jobs.stuff.ToggleBarHandling;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsMenu {

    private UnioFactions plugin;

    public SettingsMenu(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public Inventory getSettingsInventory(String playerUUID) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(9, plugin.getMessage("messages.settingsMenuGUITitle"));

        menu.withItem(3,
                getToggleActionBarItem(playerUUID),
                (player1, action, item) -> {
                    player1.performCommand("jobs toggle actionbar");
                    plugin.getMenuManager().openSettingsInventory(player1);
                },
                InventoryMenuBuilder.ALL_CLICK_TYPES);

        menu.withItem(5,
                getToggleBossBarItem(playerUUID),
                (player1, action, item) -> {
                    player1.performCommand("jobs toggle bossbar");
                    plugin.getMenuManager().openSettingsInventory(player1);
                },
                InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private ItemStack getToggleActionBarItem(String uuid) {
        boolean active = true;
        if (ToggleBarHandling.getActionBarToggle().containsKey(uuid) && !ToggleBarHandling.getActionBarToggle().get(uuid)) {
            active = false;
        }
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((active ? ChatColor.GREEN : ChatColor.RED) + plugin.getMessage("messages.settingsActionBar"));
        if (active) {
            meta.setLore(plugin.getMessages("messages.settingsActionBarDisableLore"));
        } else {
            meta.setLore(plugin.getMessages("messages.settingsActionBarEnableLore"));
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getToggleBossBarItem(String uuid) {
        boolean active = true;
        if (ToggleBarHandling.getBossBarToggle().containsKey(uuid) && !ToggleBarHandling.getBossBarToggle().get(uuid)) {
            active = false;
        }
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((active ? ChatColor.GREEN : ChatColor.RED) + plugin.getMessage("messages.settingsBossBar"));
        if (active) {
            meta.setLore(plugin.getMessages("messages.settingsBossBarDisableLore"));
        } else {
            meta.setLore(plugin.getMessages("messages.settingsBossBarEnableLore"));
        }
        item.setItemMeta(meta);
        return item;
    }
}
