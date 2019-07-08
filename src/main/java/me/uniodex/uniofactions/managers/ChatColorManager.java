package me.uniodex.uniofactions.managers;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.ChatColor;

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
        if (plugin.getConfigManager().getData().getConfigurationSection("chatcolor") == null) return;

        for (String player : plugin.getConfigManager().getData().getConfigurationSection("chatcolor").getKeys(false)) {
            chatColors.put(player, ChatColor.getByChar(plugin.getConfigManager().getData().getString("chatcolor." + player)));
        }
    }

    public void saveColors() {
        plugin.getConfigManager().getData().set("chatcolor", null);

        for (String player : chatColors.keySet()) {
            plugin.getConfigManager().getData().set("chatcolor." + player, chatColors.get(player).getChar());
        }

        plugin.getConfigManager().saveData();
    }

    public ChatColor getChatColor(String player) {
        return chatColors.get(player);
    }

    public void setChatColor(String player, ChatColor color) {
        chatColors.put(player, color);
    }
}
