package me.uniodex.uniofactions.listeners;

import me.uniodex.uniofactions.UnioFactions;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class CitizensListeners implements Listener {

    private UnioFactions plugin;
    private Map<Integer, String> npcCommands = new HashMap<>();

    public CitizensListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        loadNPCCommands();
    }

    private void loadNPCCommands() {
        for (String key : plugin.getConfig().getConfigurationSection("npcCommands").getKeys(false)) {
            Integer npcId = Integer.valueOf(key);
            String npcCommand = plugin.getConfig().getString("npcCommands." + npcId);
            npcCommands.put(npcId, npcCommand);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNPCLeftClick(NPCLeftClickEvent event) {
        NPC npc = event.getNPC();
        Integer npcId = npc.getId();
        Player p = event.getClicker();

        if (npcCommands.containsKey(npcId)) {
            if (npcCommands.get(npcId).startsWith("console:")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), npcCommands.get(npcId).replaceAll("console:", "").replaceAll("%player%", p.getName()));
            } else {
                p.performCommand(npcCommands.get(npcId));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Integer npcId = npc.getId();
        Player p = event.getClicker();

        if (npcCommands.containsKey(npcId)) {
            if (npcCommands.get(npcId).startsWith("console:")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), npcCommands.get(npcId).replaceAll("console:", "").replaceAll("%player%", p.getName()));
            } else {
                p.performCommand(npcCommands.get(npcId));
            }
        }
    }

}
