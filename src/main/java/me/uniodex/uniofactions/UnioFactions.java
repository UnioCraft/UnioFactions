package me.uniodex.uniofactions;

import com.SirBlobman.combatlogx.CombatLogX;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import me.uniodex.uniofactions.commands.*;
import me.uniodex.uniofactions.listeners.CitizensListeners;
import me.uniodex.uniofactions.listeners.JobsListeners;
import me.uniodex.uniofactions.listeners.PlayerListeners;
import me.uniodex.uniofactions.listeners.ProtectionListeners;
import me.uniodex.uniofactions.managers.*;
import me.uniodex.uniofactions.utils.Utils;
import me.uniodex.uniofactions.utils.packages.headgetter.HeadGetter;
import me.uniodex.uniofactions.utils.packages.menubuilder.inventory.InventoryListener;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private VIPManager vipManager;
    @Getter
    private JobsManager jobsManager;
    @Getter
    private QuestManager questManager;
    @Getter
    private MenuManager menuManager;
    @Getter
    private static UnioFactions instance;
    @Getter
    private CombatLogX combatLogX;
    @Getter
    private Permission permission;
    @Getter
    private InventoryListener inventoryListener;
    @Getter
    private HeadGetter headGetter;

    public void onEnable() {
        instance = this;

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

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            permission = rsp.getProvider();
        }

        // Managers
        configManager = new ConfigManager(this);
        mainManager = new MainManager(this);
        chatColorManager = new ChatColorManager(this);
        vipManager = new VIPManager((this));
        questManager = new QuestManager((this));
        headGetter = new HeadGetter(this);
        menuManager = new MenuManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("Jobs")) {
            jobsManager = new JobsManager((this));
            new JobsListeners(this);
        }

        // Listeners
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            new CitizensListeners(this);
        }
        new PlayerListeners(this);
        new ProtectionListeners(this);
        inventoryListener = new InventoryListener(this);

        // Commands
        new CmdUniofactions(this);
        new CmdTpchunk(this);
        new CmdChatcolor(this);
        new CmdCommands(this);
        new CmdQuests(this);
        new CmdMeslek(this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::clearOldLogs, 1L);
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
        FileConfiguration config;
        if (configSection.startsWith("messages.")) {
            config = getConfigManager().getConfig(ConfigManager.Config.LANG);
        } else {
            config = getConfig();
        }
        if (config.getString(configSection) == null) return null;

        return ChatColor.translateAlternateColorCodes('&', config.getString(configSection).replaceAll("%hataprefix%", hataPrefix).replaceAll("%bilgiprefix%", bilgiPrefix).replaceAll("%dikkatprefix%", dikkatPrefix).replaceAll("%prefix%", bilgiPrefix));
    }

    public List<String> getMessages(String configSection) {
        FileConfiguration config;
        if (configSection.startsWith("messages.")) {
            config = getConfigManager().getConfig(ConfigManager.Config.LANG);
        } else {
            config = getConfig();
        }
        if (config.getString(configSection) == null) return null;

        List<String> newList = new ArrayList<>();
        for (String msg : config.getStringList(configSection)) {
            newList.add(ChatColor.translateAlternateColorCodes('&', msg.replaceAll("%hataprefix%", hataPrefix).replaceAll("%bilgiprefix%", bilgiPrefix).replaceAll("%dikkatprefix%", dikkatPrefix).replaceAll("%prefix%", bilgiPrefix)));
        }
        return newList;
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
