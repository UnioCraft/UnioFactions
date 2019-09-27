package me.uniodex.uniofactions.menus;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.TopList;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import me.uniodex.uniofactions.utils.packages.headgetter.HeadGetter.HeadInfo;
import me.uniodex.uniofactions.utils.packages.headgetter.Requester;
import me.uniodex.uniofactions.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopMenu implements Requester {

    private UnioFactions plugin;
    private Map<String, ItemStack> topTenHeads = new HashMap<>();

    public TopMenu(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public Inventory getTopMenu() {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(36, plugin.getMessage("messages.topMenuGUITitle"));

        int itemSlot = 11;
        for (String job : plugin.getConfig().getConfigurationSection("jobs").getKeys(false)) {
            menu.withItem(itemSlot,
                    getJobsItem(job),
                    (player1, action, item) -> plugin.getMenuManager().openTop10Inventory(player1, job),
                    InventoryMenuBuilder.ALL_CLICK_TYPES);
            itemSlot++;
            if (itemSlot == 16) itemSlot = 20;
        }

        return menu.build();
    }

    public Inventory getTopMenu(String job) {
        List<TopList> topList = Jobs.getJobsDAO().toplist(job, -10);
        Map<Integer, Integer> itemSlots = new HashMap<>();
        itemSlots.put(0, 4);
        itemSlots.put(1, 12);
        itemSlots.put(2, 14);
        itemSlots.put(3, 19);
        itemSlots.put(4, 20);
        itemSlots.put(5, 21);
        itemSlots.put(6, 22);
        itemSlots.put(7, 23);
        itemSlots.put(8, 24);
        itemSlots.put(9, 25);

        InventoryMenuBuilder menu = new InventoryMenuBuilder(27, plugin.getMessage("messages.topMenuGUITitleWithJob").replaceAll("%job%", job));

        int i = 0;
        for (TopList player : topList) {
            menu.withItem(itemSlots.get(i), getTopPlayerItem(player, i + 1),
                    (player1, action, item) -> player1.performCommand("bal " + player.getPlayerName()),
                    InventoryMenuBuilder.ALL_CLICK_TYPES);
            i++;
        }
        return menu.build();
    }

    public ItemStack getTopPlayerItem(TopList player, int rank) {
        String playerName = player.getPlayerName();

        boolean headExist = true;

        ItemStack item = topTenHeads.get(playerName);
        if (item == null) {
            item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            headExist = false;
        }
        ItemMeta meta = item.getItemMeta();

        Utils.hideFlags(meta);
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "<!> " + ChatColor.GOLD + playerName + ChatColor.GRAY + " (#" + rank + ")");

        String loreStr = plugin.getMessage("messages.toptenPlayerLore")
                .replaceAll("%jobLevel%", String.valueOf(player.getLevel()))
                .replaceAll("%jobExp%", String.valueOf(player.getExp()));

        List<String> lore = Arrays.asList(loreStr.split("\n"));
        meta.setLore(lore);

        item.setItemMeta(meta);

        if (!headExist) {
            topTenHeads.put(playerName, item);
            plugin.getHeadGetter().getHead(playerName, this);
        }

        return item;
    }

    private ItemStack getJobsItem(String job) {
        ItemStack guiItem = Utils.stringToItemStack(plugin.getConfig().getString("jobs." + job + ".guiItem"));
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + plugin.getMessage("jobs." + job + ".menuDisplayName"));
        String loreStr = plugin.getMessage("messages.toptenJobLore").replaceAll("%job%", job.replaceAll("_", " "));

        List<String> lore = Arrays.asList(loreStr.split("\n"));
        meta.setLore(lore);

        Utils.hideFlags(meta);
        guiItem.setItemMeta(meta);
        return guiItem;
    }

    @Override
    public void setHead(HeadInfo headInfo) {
        topTenHeads.put(headInfo.getName(), headInfo.getHead());
    }
}
