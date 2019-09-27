package me.uniodex.uniofactions.utils.packages.headgetter;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

// Integrated from ASkyBlock
public class HeadGetter {
    private final Map<String, HeadInfo> cachedHeads = new HashMap<>();
    private final Map<String, String> names = new ConcurrentHashMap<>();
    private final Map<String, Set<Requester>> headRequesters = new HashMap<>();
    private final UnioFactions plugin;

    public HeadGetter(UnioFactions plugin) {
        super();
        this.plugin = plugin;
        runPlayerHeadGetter();
    }

    private void runPlayerHeadGetter() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            synchronized (names) {
                Iterator<Entry<String, String>> it = names.entrySet().iterator();
                if (it.hasNext()) {
                    Entry<String, String> en = it.next();
                    ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
                    meta.setOwner(en.getValue());
                    meta.setDisplayName(ChatColor.WHITE + en.getValue());
                    playerSkull.setItemMeta(meta);
                    // Save in cache
                    cachedHeads.put(en.getKey(), new HeadInfo(en.getValue(), playerSkull));
                    // Tell requesters the head came in
                    if (headRequesters.containsKey(en.getKey())) {
                        for (Requester req : headRequesters.get(en.getKey())) {
                            plugin.getServer().getScheduler().runTask(plugin, () -> req.setHead(new HeadInfo(en.getValue(), playerSkull)));
                        }
                    }
                    it.remove();
                }
            }
        }, 0L, 20L);
    }

    public void getHead(String player, Requester requester) {
        if (player == null) {
            return;
        }
        // Check if in cache
        if (cachedHeads.containsKey(player)) {
            requester.setHead(cachedHeads.get(player));
        } else {
            // Get the name
            headRequesters.putIfAbsent(player, new HashSet<>());
            Set<Requester> requesters = headRequesters.get(player);
            requesters.add(requester);
            headRequesters.put(player, requesters);
            names.put(player, player);
        }
    }

    public class HeadInfo {
        String name = "";
        ItemStack head;

        public HeadInfo(String name, ItemStack head) {
            this.name = name;
            this.head = head;
        }

        public String getName() {
            return name;
        }

        public ItemStack getHead() {
            return head.clone();
        }
    }
}
