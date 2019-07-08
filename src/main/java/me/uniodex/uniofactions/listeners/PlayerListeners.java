package me.uniodex.uniofactions.listeners;

import com.massivecraft.factions.FPlayers;
import me.uniodex.uniofactions.UnioFactions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerListeners implements Listener {

    private UnioFactions plugin;

    public PlayerListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        ChatColor color = plugin.getChatColorManager().getChatColor(event.getPlayer().getName());
        if (color == null) return;
        event.setMessage(color + event.getMessage());

        // What is this?: It puts space after faction tag if player has a faction.
        if (event.getFormat().contains("[FACTION]") && FPlayers.getInstance().getByPlayer(event.getPlayer()).hasFaction()) {
            event.setFormat(event.getFormat().replace("[FACTION]", "[FACTION] "));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] command = event.getMessage().replaceFirst("/", "").split(" ");
        Player p = event.getPlayer();

        for (String cmd : plugin.getMainManager().getCommands().keySet()) {
            if (cmd.split(" ")[0].equalsIgnoreCase(command[0])) {
                if (command.length < cmd.split(" ").length) continue;
                String commandToUse = plugin.getMainManager().getCommands().get(cmd);
                for (int i = 0; i < command.length; i++) {
                    commandToUse = commandToUse.replaceAll("%arg-" + i + "%", command[i]);
                    commandToUse = commandToUse.replaceAll("%args-" + i + "%", StringUtils.join(command, ' ', i, command.length));
                }

                p.performCommand(commandToUse);
                event.setCancelled(true);
                break;
            }
        }
    }
}
