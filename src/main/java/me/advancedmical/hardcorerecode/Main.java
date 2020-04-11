package me.advancedmical.hardcorerecode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.sasl.SaslServer;
import java.io.File;
public class Main extends JavaPlugin implements Listener {
    static Economy econ = null;
    public String prefix = "§6§l复活币系统§9§l>>>";
    @Override
    public void onLoad() {
        getLogger().info("§a正在加载HardCoreRecode，版本：§3" + getDescription().getVersion());
    }
    File file;
    YamlConfiguration yaml;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("§b          _______  _______  ______   _______  _______  _______  _______ ");
        Bukkit.getLogger().info("§b|\\     /|(  ___  )(  ____ )(  __  \\ (  ____ \\(  ___  )(  ____ )(  ____ \\");
        Bukkit.getLogger().info("§b| )   ( || (   ) || (    )|| (  \\  )| (    \\/| (   ) || (    )|| (    \\/");
        Bukkit.getLogger().info("§b| (___) || (___) || (____)|| |   ) || |      | |   | || (____)|| (__    ");
        Bukkit.getLogger().info("§b|  ___  ||  ___  ||     __)| |   | || |      | |   | ||     __)|  __) ");
        Bukkit.getLogger().info("§b| (   ) || (   ) || (\\ (   | |   ) || |      | |   | || (\\ (   | (  ");
        Bukkit.getLogger().info("§b| )   ( || )   ( || ) \\ \\__| (__/  )| (____/\\| (___) || ) \\ \\__| (____/\\");
        Bukkit.getLogger().info("§b|/     \\||/     \\||/   \\__/(______/ (_______/(_______)|/   \\__/(_______/");
        saveDefaultConfig();
        getLogger().info("§a成功开启模块：§3重生扣除金钱");
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("§c没有发现§3Vault§c，插件即将卸载...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("§a成功挂钩：§3Vault");
        }
        if (getServer().getPluginManager().getPlugin("CMI") == null){
            getLogger().info("§c没有发现§3CMI§c，插件即将卸载...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("§a成功挂钩：§3CMI");
        }
        if (getServer().getPluginManager().getPlugin("CMIEInjector") == null){
            getLogger().info("§c没有发现§3CMIEInjector§c，插件即将卸载...");
            return;
        } else {
            getLogger().info("§a成功挂钩：§3CMIEInjector");
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        getLogger().info("§a成功开启模块：§3经济");
        getLogger().info("§a加载完成");
        file= new File(this.getDataFolder(), "\\playerdata\\data.yml");
        yaml=YamlConfiguration.loadConfiguration(file);
        try {
            yaml.save(file);
        } catch (Exception e){
            e.printStackTrace();
        }
        load();
    }

    protected void load(){
        PlaceHolder.hook();
    }

    @EventHandler
    public void Join(PlayerJoinEvent e){
        Player player = e.getPlayer();
        new BukkitRunnable(){

            @Override
            public void run() {
                Player player = e.getPlayer();
                String toString = yaml.getString(player.getUniqueId() + "-gm");
                player.setGameMode(GameMode.valueOf(toString==null?"SURVIVAL":toString));
                cancel();
            }
        }.runTaskTimer(this, 20L, 20L);
    }
    @EventHandler
    public void Quit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        yaml.set(player.getUniqueId() + "-gm",player.getGameMode().toString());
        try {
            yaml.save(file);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("§a插件已卸载");
        PlaceHolder.unhook();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent pje) {
        Player player = pje.getPlayer();
        for (String a : yaml.getKeys(false)) {
            if (a.equals(player.getUniqueId())) {
                return;
            }
        }
        yaml.set(String.valueOf(player.getUniqueId()), getConfig().getInt("newPlayerProtectNum"));
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pje.getPlayer().sendMessage(getConfig().getString("Lang1").replace("{0}", String.valueOf(this.getConfig().getString("newPlayerProtectNum"))).replace("&", "§"));
    }
    public static double get(OfflinePlayer player) {
        return econ.getBalance(player);
    }
    @EventHandler
    public void onDead(PlayerRespawnEvent pre) {
        Player p = pre.getPlayer();
        if (yaml.getInt(String.valueOf(p.getUniqueId())) == 0) {
            p.sendMessage(getConfig().getString("Lang2").replace("{0}", String.valueOf(this.getConfig().getString("takeNum"))).replace("&", "§"));
            new BukkitRunnable() {
                int time = 3;
                @Override
                public void run() {
                    p.sendMessage(getConfig().getString("Lang3").replace("{0}", String.valueOf(time)).replace("&", "§"));
                    time--;
                    if (time == 0) {
                        double eco = econ.getBalance(p);
                        if (econ.getBalance(p.getPlayer()) < getConfig().getInt("takeNum")) {
                            p.sendMessage(getConfig().getString("Lang6").replace("{0}", String.valueOf(econ.getBalance(p.getPlayer()))).replace("&", "§"));
                            cancel();
                        } else {
                            econ.withdrawPlayer(p.getPlayer(), getConfig().getDouble("takeNum"));
                            p.setGameMode(GameMode.SURVIVAL);
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(this, 0, 20L);
        } else {
            yaml.set(String.valueOf(p.getUniqueId()), yaml.getInt(String.valueOf(p.getUniqueId())) - 1);
            try {
                yaml.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BukkitRunnable() {
                int time = 3;
                @Override
                public void run() {
                    p.sendMessage(getConfig().getString("Lang4").replace("{0}", String.valueOf(time)).replace("&", "§"));
                    time--;
                    if (time == 0) {
                        p.setGameMode(GameMode.SURVIVAL);
                        cancel();
                    }
                }
            }.runTaskTimer(this, 0, 20L);
            p.sendMessage(this.getConfig().getString("Lang5").replace("{0}", String.valueOf(yaml.getInt(String.valueOf(p.getUniqueId())))).replace("&", "§"));
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hardcore")){
            if (sender.hasPermission("hc.use")){
                helpMsg(sender);
                return true;
            } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            if (args[0].equalsIgnoreCase("reload")){
                if (sender.hasPermission("hc.load")) {
                    this.reloadConfig();
                    sender.sendMessage(this.getConfig().getString("Lang7").replace("&", "§"));
                    return true;
                } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            } else {
                helpMsg(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("help")){
                if (sender.hasPermission("hc.help")){
                    helpMsg(sender);
                    return true;
                } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            } else {
                helpMsg(sender);
                return true;
            }
            }
        return false;
    }

    public void helpMsg(CommandSender sender){
        Player player = (Player)sender;
        player.sendMessage("§7--------[§1§l重生系统§a使用帮助§7]--------");
        player.sendMessage("  §7注:重生时请确保你的金钱§c§l>" + getConfig().getInt("takeNum") + "§7,否则重生§c§l可能失败§7.");
        player.sendMessage("  §5/hardcore help §1--- §7显示使用帮助.");
        player.sendMessage("  §5/hardcore reload §1--- §7重载插件.");
        player.sendMessage("§7----------------------------------------");
    }
}