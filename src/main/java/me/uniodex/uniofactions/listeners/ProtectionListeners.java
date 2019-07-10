package me.uniodex.uniofactions.listeners;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class ProtectionListeners implements Listener {

    private UnioFactions plugin;

    public ProtectionListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.getName().toLowerCase().startsWith("faction_")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, plugin.getMessage("messages.disallowedName"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getCombatLogX() == null) return;
        if (!CombatUtil.isInCombat(player)) return;

        if (event.getBlockPlaced().getType().equals(Material.WEB)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessage("messages.webInCombatDisabled"));
        }
    }
}
