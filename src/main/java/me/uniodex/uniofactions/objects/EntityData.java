package me.uniodex.uniofactions.objects;

import java.util.*;

// This is for LootProtection
public class EntityData {

    private UUID entityId;
    private Map<String, Double> takenDamages = new HashMap<>();
    private Long lastDamageTaken;

    public EntityData(UUID entityId) {
        this.entityId = entityId;
    }

    public void addDamage(String player, double amount) {
        if (takenDamages.containsKey(player)) {
            takenDamages.put(player, takenDamages.get(player) + amount);
        } else {
            takenDamages.put(player, amount);
        }
        lastDamageTaken = System.currentTimeMillis();
    }

    public String getTopDamager() {
        return Collections.max(takenDamages.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
    }

    // We are removing entities from memory who didn't take damage (from players) last 30 seconds.
    public boolean isInactive() {
        return System.currentTimeMillis() - lastDamageTaken >= (30 * 1000);
    }

}