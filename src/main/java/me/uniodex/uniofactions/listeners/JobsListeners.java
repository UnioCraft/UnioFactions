package me.uniodex.uniofactions.listeners;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.Job;
import me.uniodex.uniocustomitems.CustomItems;
import me.uniodex.uniocustomitems.managers.ItemManager;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class JobsListeners implements Listener {

    private UnioFactions plugin;

    public JobsListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private double getPayment(Player player, Job job, double amount) {
        if (!job.getName().equalsIgnoreCase("Madenci")) return amount;
        double newAmount = amount;

        Block lastBrokenBlock = plugin.getJobsManager().getLastBrokenBlock(player);
        if (lastBrokenBlock == null || !plugin.getJobsManager().isLocationInMine(lastBrokenBlock.getLocation())) {
            return 0;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (CustomItems.instance.itemManager.isItemNamed(item)) {
            if (CustomItems.instance.itemManager.getItem(ItemManager.Items.vaghlodarKazmasi).getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                newAmount = (amount / 5.2D);
            } else if (CustomItems.instance.itemManager.getItem(ItemManager.Items.karaBuyuluVaghlodarKazmasi).getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                newAmount = (amount / 4);
            } else if (CustomItems.instance.itemManager.getItem(ItemManager.Items.buyuluAlanKazmasi).getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                newAmount = (amount / 40.4D);
            }
        }

        if (Utils.isLocationInArea(lastBrokenBlock.getLocation(), plugin.getConfig().getString("mineNames.vip"))) {
            if (player.hasPermission("uniofactions.rank.vip+")) {
                newAmount *= 1.75;
            } else {
                newAmount *= 1.5;
            }
        } else if (Utils.isLocationInArea(lastBrokenBlock.getLocation(), plugin.getConfig().getString("mineNames.uvip"))) {
            newAmount *= 2;
        } else if (Utils.isLocationInArea(lastBrokenBlock.getLocation(), plugin.getConfig().getString("mineNames.uvip+"))) {
            newAmount *= 2.25;
        }

        return newAmount;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPayment(JobsPrePaymentEvent event) {
        if (event.getPlayer().getPlayer() == null) return;
        Player player = event.getPlayer().getPlayer();

        double amount = getPayment(player, event.getJob(), event.getAmount());
        event.setAmount(amount);
        event.setPoints(amount);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPayment(JobsExpGainEvent event) {
        if (event.getPlayer().getPlayer() == null) return;
        Player player = event.getPlayer().getPlayer();

        double amount = getPayment(player, event.getJob(), event.getExp());
        event.setExp(amount);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        plugin.getJobsManager().addBrokenBlock(player, block);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getJobsManager().clearPlayerData(event.getPlayer());
    }
}
