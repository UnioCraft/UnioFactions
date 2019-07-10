package me.uniodex.uniofactions.utils;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date parseTime(String time) {
        try {
            String[] frag = time.split("-");
            if (frag.length < 2) {
                return new Date();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(frag[0] + "-" + frag[1] + "-" + frag[2]);
        } catch (Exception ignored) {
        }
        return new Date();
    }

    public static ChatColor stringToChatColor(String color) {
        switch (color.toLowerCase()) {
            case "yeşil":
                return ChatColor.GREEN;
            case "açık_mavi":
                return ChatColor.AQUA;
            case "açık_kırmızı":
                return ChatColor.RED;
            case "eflatun":
                return ChatColor.LIGHT_PURPLE;
            case "sarı":
                return ChatColor.YELLOW;
            case "beyaz":
                return ChatColor.WHITE;
            case "siyah":
                return ChatColor.BLACK;
            case "koyu_mavi":
                return ChatColor.DARK_BLUE;
            case "koyu_yeşil":
                return ChatColor.DARK_GREEN;
            case "turkuaz":
                return ChatColor.DARK_AQUA;
            case "koyu_kırmızı":
                return ChatColor.DARK_RED;
            case "koyu_mor":
                return ChatColor.DARK_PURPLE;
            case "turuncu":
                return ChatColor.GOLD;
            case "gri":
                return ChatColor.GRAY;
            case "koyu_gri":
                return ChatColor.DARK_GRAY;
            case "mavi":
                return ChatColor.BLUE;
            default:
                return null;
        }
    }

    public static String colorizeMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replaceAll("%hataprefix%", UnioFactions.hataPrefix).replaceAll("%bilgiprefix%", UnioFactions.bilgiPrefix).replaceAll("%dikkatprefix%", UnioFactions.dikkatPrefix).replaceAll("%prefix%", UnioFactions.bilgiPrefix));
    }
}
