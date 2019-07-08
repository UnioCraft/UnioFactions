package me.uniodex.uniofactions.commands;

import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTpchunk implements CommandExecutor {

    private UnioFactions plugin;

    public CmdTpchunk(UnioFactions plugin) {
        this.plugin = plugin;
        plugin.getCommand("tpchunk").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("unio.tpchunk") || !(sender instanceof Player)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Kullanım: /tpchunk x z [world]");
            return true;
        }

        World world = Bukkit.getWorld("world");
        Player player = (Player) sender;

        if (args.length == 3) {
            world = Bukkit.getWorld(args[2]);
        }

        Block b = world.getChunkAt(Integer.parseInt(args[0]), Integer.parseInt(args[1])).getBlock(8, 0, 8);

        int X = b.getX();
        int Y = b.getY();
        int Z = b.getZ();

        player.teleport(new Location(world, X, world.getHighestBlockYAt(new Location(world, X, Y, Z)), Z));

        return true;
    }

}
