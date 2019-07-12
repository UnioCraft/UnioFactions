package me.uniodex.uniofactions;

import com.SirBlobman.combatlogx.CombatLogX;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import me.uniodex.uniofactions.commands.CmdChatcolor;
import me.uniodex.uniofactions.commands.CmdCommands;
import me.uniodex.uniofactions.commands.CmdTpchunk;
import me.uniodex.uniofactions.commands.CmdUniofactions;
import me.uniodex.uniofactions.listeners.CitizensListeners;
import me.uniodex.uniofactions.listeners.PlayerListeners;
import me.uniodex.uniofactions.listeners.ProtectionListeners;
import me.uniodex.uniofactions.managers.ChatColorManager;
import me.uniodex.uniofactions.managers.ConfigManager;
import me.uniodex.uniofactions.managers.MainManager;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;

public class UnioFactions extends JavaPlugin {

    public static String hataPrefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.RED + " ";
    public static String dikkatPrefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.GOLD + " ";
    public static String bilgiPrefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.GREEN + " ";
    public static String consolePrefix = "[UnioFactions] ";

    @Getter
    private MythicMobs mythicMobs;
    @Getter
    private WorldGuardPlugin worldGuard;
    @Getter
    private ConfigManager configManager;
    @Getter
    private ChatColorManager chatColorManager;
    @Getter
    private MainManager mainManager;
    @Getter
    private static UnioFactions instance;
    @Getter
    private CombatLogX combatLogX;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        if (!Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            throw new RuntimeException("Factions plugin couldn't find. UnioFactions is disabling.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            mythicMobs = (MythicMobs) Bukkit.getPluginManager().getPlugin("MythicMobs");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        } else {
            throw new RuntimeException("WorldGuard plugin couldn't find. UnioFactions is disabling.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CombatLogX")) {
            combatLogX = (CombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getLogger().log(Level.SEVERE, "PlaceholderAPI couldn't find. It might cause issues.");
        }

        // Managers
        configManager = new ConfigManager(this);
        mainManager = new MainManager(this);
        chatColorManager = new ChatColorManager(this);

        // Listeners
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            new CitizensListeners(this);
        }
        new PlayerListeners(this);
        new ProtectionListeners(this);

        // Commands
        new CmdUniofactions(this);
        new CmdTpchunk(this);
        new CmdChatcolor(this);
        new CmdCommands(this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> clearOldLogs(), 1L);
    }

    public void onDisable() {
        chatColorManager.saveColors();
    }

    public void reload() {
        reloadConfig();
        getMainManager().updateCommandListPages();
        getMainManager().loadCommandInstances();
    }

    public String getMessage(String configSection) {
        if (getConfig().getString(configSection) == null) return null;
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(configSection).replaceAll("%hataprefix%", hataPrefix).replaceAll("%bilgiprefix%", bilgiPrefix).replaceAll("%dikkatprefix%", dikkatPrefix).replaceAll("%prefix%", bilgiPrefix));
    }

    private void clearOldLogs() {
        long time = new Date().getTime() - 86400000L * 60;

        File folder = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/logs");
        if (!folder.exists()) {
            return;
        }
        File[] files = folder.listFiles();

        if (files == null) return;

        int deleted = 0;
        for (File file : files) {
            if ((file.isFile()) && (file.getName().endsWith(".log.gz")) && (time > Utils.parseTime(file.getName().replace(".log.gz", "")).getTime())) {
                file.delete();
                deleted++;
            }
        }
        System.out.println(consolePrefix + deleted + " log files are removed.");
    }
}
