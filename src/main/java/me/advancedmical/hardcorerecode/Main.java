package me.advancedmical.hardcorerecode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    static Economy econ = null;
    @Override
    public void onLoad() {
        getLogger().info("§a正在加载HardCoreRecode，版本：§3" + getDescription().getVersion());
    }
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
        getLogger().info("§a加载完成，作者Mical，本插件仅用于方块世界服务器内部使用！");
    }
    @Override
    public void onDisable() {
        getLogger().info("§a插件已卸载");
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent pje) {
        for (String a : getConfig().getKeys(false)) {
            if (a.equals(pje.getPlayer().getName())) {
                return;
            }
        }
        getConfig().set(pje.getPlayer().getName(), getConfig().getInt("newPlayerProtectNum"));
        saveConfig();
        pje.getPlayer().sendMessage(getConfig().getString("Lang1").replace("{0}", String.valueOf(this.getConfig().getString("newPlayerProtectNum"))).replace("&", "§"));
    }
    public static double get(OfflinePlayer player) {
        return econ.getBalance(player);
    }
    @EventHandler
    public void onDead(PlayerRespawnEvent pre) {
        Player p = pre.getPlayer();
        if (getConfig().getInt(p.getName()) == 0) {
            econ.withdrawPlayer(p.getPlayer(), getConfig().getDouble("takeNum"));
            p.sendMessage(getConfig().getString("Lang2").replace("{0}", String.valueOf(this.getConfig().getString("takeNum"))).replace("&", "§"));
            new BukkitRunnable() {
                int time = 3;
                @Override
                public void run() {
                    p.sendMessage(getConfig().getString("Lang3").replace("{0}", String.valueOf(time)).replace("&", "§"));
                    time--;
                    if (time == 0) {
                        double eco = econ.getBalance(p);
                        if (econ.getBalance(p.getPlayer()) < 200) {
                            p.sendMessage(getConfig().getString("Lang6").replace("{0}", String.valueOf(econ.getBalance(p.getPlayer()))).replace("&", "§"));
                            cancel();
                        } else {
                            p.setGameMode(GameMode.SURVIVAL);
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(this, 0, 20L);
        } else {
            getConfig().set(p.getName(), getConfig().getInt(p.getName()) - 1);
            saveConfig();
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
            p.sendMessage(this.getConfig().getString("Lang5").replace("{0}", String.valueOf(this.getConfig().getInt(p.getName()))).replace("&", "§"));
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), getConfig().getString("deadCommand").replace("%player%", p.getName()));
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
            if (args[0].equalsIgnoreCase("buy")){
                if (args[1].equalsIgnoreCase("1")){
                    if (sender.hasPermission("hardcore.buy")){
                        Player p = (Player) sender;
                        econ.withdrawPlayer(p.getPlayer(), getConfig().getDouble("takeNum"));
                        if (econ.getBalance(p.getPlayer()) < 200){
                            p.sendMessage(this.getConfig().getString("Lang9"));
                        } else {
                            String.valueOf(this.getConfig().getInt(p.getName()) + 1);
                        }
                    }
                }
            }
        }
        return false;
    }

    public void helpMsg(CommandSender sender){
        Player player = (Player)sender;
        player.sendMessage("§7--------[§1§l重生系统§a使用帮助§7]--------");
        player.sendMessage("  §7注:重生时请确保你的金钱§c§l>200§7,否则重生§c§l可能失败§7.");
        player.sendMessage("  §5/hardcore help §1--- §7显示使用帮助.");
        player.sendMessage("  §5/hardcore reload §1--- §7重载插件.");
        player.sendMessage("§7----------------------------------------");
    }
    /*

    public boolean onCommand(InventoryClickEvent event, PlayerEvent e, CommandSender sender, Command command, String label, String[] args) {
            Player player = e.getPlayer();
            Inventory inv = Bukkit.createInventory(player, 3 * 9, String.valueOf(this.getConfig().getString("InventoryName").replace("&", "§")));
            ItemStack im = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta ima = im.getItemMeta();
            ima.setDisplayName(this.getConfig().getString("InventoryFrameName"));
            List<String> lorelist = new ArrayList<>();
            ima.setLore(lorelist);
            lorelist.add("&6&l嗷呜~放过我吧呜呜呜");
            inv.setItem(0, im);
            player.openInventory(inv);
            if (event.getView().getTitle().equals(this.getConfig().getString("InventoryName"))) {
                event.setCancelled(true);
            }

        if (command.getName().equalsIgnoreCase("hardcore")){
            if(args[0].equalsIgnoreCase("help")){
                if(args[1].equalsIgnoreCase("gui")){
                    if(sender.isOp()){
                        player.openInventory(inv);
                    }
                }
            }
            if (sender.hasPermission("hc.use")){
                helpMsg(sender);
            } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            if (args[0].equalsIgnoreCase("reload")){
                if (sender.hasPermission("hc.load")) {
                    this.reloadConfig();
                    sender.sendMessage(this.getConfig().getString("Lang7").replace("&", "§"));
                } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            } else {
                helpMsg(sender);
            }
            if (args[0].equalsIgnoreCase("help")){
                if (sender.hasPermission("hc.help")){
                    helpMsg(sender);
                } else { sender.sendMessage(this.getConfig().getString("Lang8").replace("&", "§")); }
            } else {
                helpMsg(sender);
            }
        }
        return false;
    }

     */
}