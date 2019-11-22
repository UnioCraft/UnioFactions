package me.uniodex.uniofactions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import me.clip.placeholderapi.PlaceholderAPI;
import me.uniodex.uniofactions.UnioFactions;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UnknownFormatConversionException;

public class PlayerListeners implements Listener {

    private UnioFactions plugin;

    public PlayerListeners(UnioFactions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatHighest(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Set<Player> recipients = event.getRecipients();
        String format = event.getFormat();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String hoverText = PlaceholderAPI.setPlaceholders(player, plugin.getMessage("messages.chatHover")
                .replaceAll("%time%", timeFormat.format(calendar.getTime()))
                .replaceAll("%player%", player.getDisplayName()));

        // Faction
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        if (format.contains("[FACTION_TITLE]")) {
            format = format.replace("[FACTION_TITLE]", me.getTitle());
        }

        int InsertIndex = format.indexOf("[FACTION]");
        format = format.replace("[FACTION]", "");

        String formatStart = format.substring(0, InsertIndex);
        String formatEnd = format.substring(InsertIndex);

        for (Player listeningPlayer : recipients) {
            FPlayer you = FPlayers.getInstance().getByPlayer(listeningPlayer);
            String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;

            TextComponent textComponent = new TextComponent(String.format(yourFormat, player.getDisplayName(), event.getMessage()));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));

            try {
                listeningPlayer.spigot().sendMessage(textComponent);
            } catch (UnknownFormatConversionException ex) {
                return;
            }
        }
        event.getRecipients().clear();
        //
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChatLow(AsyncPlayerChatEvent event) {
        ChatColor color = plugin.getChatColorManager().getChatColor(event.getPlayer().getName());
        if (color == null) return;
        event.setMessage(color + event.getMessage());

        // What is this?: It puts space after faction tag if player has a faction.
        if (event.getFormat().contains("[FACTION]") && FPlayers.getInstance().getByPlayer(event.getPlayer()).hasFaction()) {
            event.setFormat(event.getFormat().replace("[FACTION]", "[FACTION] "));
        }
    }
}
