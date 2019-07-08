package me.uniodex.uniofactions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.objects.EntityData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class LootProtectionListeners implements Listener {

    private UnioFactions plugin;

    public LootProtectionListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!plugin.getLootProtectionManager().isItemProtected(event.getItem())) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getEntity();

        if (player.hasPermission("uniofactions.lootprotection.bypass")) return;

        String owner = plugin.getLootProtectionManager().getItemOwner(event.getItem());
        if (owner == null) return;
        if (player.getName().equals(owner)) return;

        Player ownerPlayer = Bukkit.getPlayerExact(owner);
        if (ownerPlayer != null) {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            FPlayer fowner = FPlayers.getInstance().getByPlayer(ownerPlayer);

            if (fplayer.getFaction().equals(fowner.getFaction())) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Player) player = (Player) event.getDamager();
        if (event.getDamager() instanceof Projectile) player = (Player) ((Projectile) event.getDamager()).getShooter();
        if (player == null) return;

        Entity entity = event.getEntity();

        if (!plugin.getLootProtectionManager().getEntityDatas().containsKey(entity.getUniqueId())) {
            plugin.getLootProtectionManager().getEntityDatas().put(entity.getUniqueId(), new EntityData(entity.getUniqueId()));
        }

        plugin.getLootProtectionManager().getEntityDatas().get(entity.getUniqueId()).addDamage(player.getName(), event.getDamage());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getDrops().isEmpty()) return;
        if (!plugin.getLootProtectionManager().getEntityDatas().containsKey(event.getEntity().getUniqueId())) return;

        Entity entity = event.getEntity();
        String topDamager = plugin.getLootProtectionManager().getEntityDatas().get(entity.getUniqueId()).getTopDamager();
        if (topDamager == null) {
            plugin.getLootProtectionManager().clearData(event.getEntity());
            return;
        }

        try {
            plugin.getLootProtectionManager().protectLoot(event.getDrops(), topDamager, entity.getLocation());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            event.getDrops().clear();
        }

        plugin.getLootProtectionManager().clearData(event.getEntity());

        if (!plugin.getLootProtectionManager().shouldSendMessage(event.getEntity())) return;

        if (event.getEntity().getKiller() != null && !event.getEntity().getKiller().getName().equals(topDamager)) {
            Player killer = event.getEntity().getKiller();
            if (event.getEntity() instanceof Player) {
                killer.sendMessage(plugin.getMessage("messages.playerLootBelongToSomeoneElse").replaceAll("%lootOwner%", topDamager));
            } else {
                killer.sendMessage(plugin.getMessage("messages.mobLootBelongToSomeoneElse").replaceAll("%lootOwner%", topDamager));
            }
        }

        if (Bukkit.getPlayerExact(topDamager) != null) {
            Player killer = Bukkit.getPlayerExact(topDamager);
            killer.sendMessage(plugin.getMessage("messages.lootBelongToYou"));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (Bukkit.getPlayerExact(topDamager) != null) {
                    killer.sendMessage(plugin.getMessage("messages.lootProtectionTimedOut"));
                }
            }, plugin.getLootProtectionManager().getProtectionTimeout() * 20);
        }
    }
}
