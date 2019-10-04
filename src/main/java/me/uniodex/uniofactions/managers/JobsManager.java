package me.uniodex.uniofactions.managers;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import me.uniodex.uniofactions.UnioFactions;
import me.uniodex.uniofactions.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsManager {

    private UnioFactions plugin;
    private Map<Player, Block> lastBrokenBlocks = new HashMap<>();

    public JobsManager(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public void addBrokenBlock(Player player, Block block) {
        lastBrokenBlocks.put(player, block);
    }

    public Block getLastBrokenBlock(Player player) {
        return lastBrokenBlocks.get(player);
    }

    public void clearPlayerData(Player player) {
        lastBrokenBlocks.remove(player);
    }

    public boolean isLocationInMine(Location location) {
        List<String> mineNames = new ArrayList<>();
        mineNames.add(plugin.getConfig().getString("mineNames.default"));
        mineNames.add(plugin.getConfig().getString("mineNames.vip"));
        mineNames.add(plugin.getConfig().getString("mineNames.uvip"));
        mineNames.add(plugin.getConfig().getString("mineNames.uvip+"));

        for (String mineName : mineNames) {
            if (Utils.isLocationInArea(location, mineName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInJob(String player, String job) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jobsPlayer == null) return false;
        if (Jobs.getJob(job) == null) return false;
        return jobsPlayer.isInJob(Jobs.getJob(job));
    }

    public int getJobLevel(String player, String jobName) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        Job job = Jobs.getJob(jobName);
        if (jobsPlayer == null) return 0;
        if (job == null) return 0;
        JobProgression jobProgression = jobsPlayer.getJobProgression(job);
        if (jobProgression == null) return 0;

        return jobProgression.getLevel();
    }
}
