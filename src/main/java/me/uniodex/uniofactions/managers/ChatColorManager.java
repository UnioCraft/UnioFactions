package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.ChatColor;
import me.uniodex.uniofactions.managers.ConfigManager.Config;

import java.util.HashMap;
import java.util.Map;

public class ChatColorManager {

    private UnioFactions plugin;
    private Map<String, ChatColor> chatColors = new HashMap<>();

    public ChatColorManager(UnioFactions plugin) {
        this.plugin = plugin;
        loadColors();
    }

    private void loadColors() {
        if (plugin.getConfigManager().getConfig(Config.CHATCOLOR).getConfigurationSection("chatcolor") == null) return;

        for (String player : plugin.getConfigManager().getConfig(Config.CHATCOLOR).getConfigurationSection("chatcolor").getKeys(false)) {
            chatColors.put(player, ChatColor.getByChar(plugin.getConfigManager().getConfig(Config.CHATCOLOR).getString("chatcolor." + player)));
        }
    }

    public void saveColors() {
        plugin.getConfigManager().getConfig(Config.CHATCOLOR).set("chatcolor", null);

        for (String player : chatColors.keySet()) {
            plugin.getConfigManager().getConfig(Config.CHATCOLOR).set("chatcolor." + player, chatColors.get(player).getChar());
        }

        plugin.getConfigManager().saveConfig(Config.CHATCOLOR);
    }

    public ChatColor getChatColor(String player) {
        return chatColors.get(player);
    }

    public void setChatColor(String player, ChatColor color) {
        chatColors.put(player, color);
    }
}
