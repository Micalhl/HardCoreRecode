package me.advancedmical.hardcorerecode;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlaceHolder extends PlaceholderExpansion {
    private static final String HOOK_NAME = "hardcore";

    File file;
    YamlConfiguration yaml;

    public static void hook(){
        PlaceholderAPI.registerPlaceholderHook("hardcore", new PlaceHolder());
    }

    public static void unhook(){
        PlaceholderAPI.unregisterPlaceholderHook("hardcore");
    }

    @Override
    public String onPlaceholderRequest(Player p, String indentifier) {
        if (p == null)
            return "";
        if (indentifier.equals("protect")){
            return String.valueOf(JavaPlugin.getPlugin(Main.class).yaml.getInt(String.valueOf(p.getUniqueId())));
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return "hardcore";
    }

    @Override
    public String getAuthor() {
        return "Mical";
    }

    @Override
    public String getVersion() {
        return JavaPlugin.getPlugin(Main.class).getDescription().getVersion();
    }
}
