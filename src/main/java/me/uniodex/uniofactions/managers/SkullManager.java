package me.uniodex.uniofactions.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.uniodex.uniofactions.UnioFactions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

public class SkullManager {

    private UnioFactions plugin;

    public SkullManager(UnioFactions plugin) {
        this.plugin = plugin;
    }

    public ItemStack getSkull(String playerName) {
        String[] textureAndSiganture;
        textureAndSiganture = plugin.getSqlManager().getSkin(playerName);
        if (textureAndSiganture == null) {
            textureAndSiganture = getTextureAndSignature(playerName);
        }
        return getSkull(playerName, textureAndSiganture[0], textureAndSiganture[1]);
    }

    private ItemStack getSkull(String playerName, String value, String signature) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            return null;
        }
        skullMeta.setDisplayName("§f" + playerName + " isimli oyuncunun kafası");

        item.setItemMeta(skullMeta);
        return item;
    }

    private String[] getTextureAndSignature(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
            return null;
        }
    }
}
