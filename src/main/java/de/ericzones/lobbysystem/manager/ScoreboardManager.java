// Created by Eric B. 17.05.2021 12:10
package de.ericzones.lobbysystem.manager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.ericzones.bungeedriver.BungeeDriver;
import de.ericzones.bungeedriver.collectives.plugindata.object.DataCorePlayer;
import de.ericzones.bungeedriver.collectives.plugindata.object.DataFriendPlayer;
import de.ericzones.lobbysystem.LobbySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements Listener {

    private LobbySystem instance;

    public ScoreboardManager(LobbySystem instance) {
        this.instance = instance;
        startUpdater();
    }

    private void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("aaa", "bbb");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String server = String.valueOf(Wrapper.getInstance().getServiceId().getTaskServiceId());
        if(server.length() == 1)
            server = "0"+server;

        objective.setDisplayName("§3•§b● Lobby §8§l┃ §7"+server);

        objective.getScore(" ").setScore(12);
        objective.getScore("§8•§7● Rang").setScore(11);
        objective.getScore(updateScore(scoreboard, "group", "  §8* ", getGroup(player), ChatColor.GRAY)).setScore(10);
        objective.getScore("  ").setScore(9);
        objective.getScore("§8•§7● Coins").setScore(8);
        objective.getScore(updateScore(scoreboard, "coins", "  §8* ", getCoins(player), ChatColor.YELLOW)).setScore(7);
        objective.getScore("   ").setScore(6);
        objective.getScore("§8•§7● Freunde").setScore(5);
        if(getAllFriends(player) == -1)
            objective.getScore(updateScore(scoreboard, "friends", "  §8* §7Lade...", "", ChatColor.AQUA)).setScore(4);
        else
            objective.getScore(updateScore(scoreboard, "friends", "  §8* §b"+getOnlineFriends(player), "§7/"+getAllFriends(player), ChatColor.AQUA)).setScore(4);
        objective.getScore("    ").setScore(3);
        objective.getScore("§8•§7● Spielzeit").setScore(2);
        objective.getScore(updateScore(scoreboard, "playtime", "  §8* ", getPlaytime(player), ChatColor.GREEN)).setScore(1);

        Team admin = registerTeam(scoreboard, "0001Admin", "§cAdmin §8● §c", "");
        Team developer = registerTeam(scoreboard, "0002Dev", "§bDev §8● §b", "");
        Team srmoderator = registerTeam(scoreboard, "0003SrMod", "§aSrMod §8● §a", "");
        Team moderator = registerTeam(scoreboard, "0004Mod", "§aMod §8● §a", "");
        Team supporter = registerTeam(scoreboard, "0005Sup", "§9Sup §8● §9", "");
        Team jrsupporter = registerTeam(scoreboard, "0006JrSup", "§9JrSup §8● §9", "");
        Team srbuilder = registerTeam(scoreboard, "0007SrBuild", "§2SrBuild §8● §2", "");
        Team builder = registerTeam(scoreboard, "0008Build", "§2Build §8● §2", "");
        Team jrbuilder = registerTeam(scoreboard, "0009JrBuild", "§2JrBuild §8● §2", "");
        Team content = registerTeam(scoreboard, "0010Content", "§dContent §8● §d", "");
        Team premium = registerTeam(scoreboard, "0011Prem", "§6Prem §8● §6", "");
        Team spieler = registerTeam(scoreboard, "0012Spieler", "§7Spieler §8● §7", "");

        for (Player current : Bukkit.getOnlinePlayers()) {
            String group = getRank(current);
            switch (group) {
                case "Admin":
                    admin.addEntry(current.getName());
                    break;
                case "Developer":
                    developer.addEntry(current.getName());
                    break;
                case "SrModerator":
                    srmoderator.addEntry(current.getName());
                    break;
                case "Moderator":
                    moderator.addEntry(current.getName());
                    break;
                case "Supporter":
                    supporter.addEntry(current.getName());
                    break;
                case "JrSupporter":
                    jrsupporter.addEntry(current.getName());
                    break;
                case "SrBuilder":
                    srbuilder.addEntry(current.getName());
                    break;
                case "Builder":
                    builder.addEntry(current.getName());
                    break;
                case "JrBuilder":
                    jrbuilder.addEntry(current.getName());
                    break;
                case "Content":
                    content.addEntry(current.getName());
                    break;
                case "Premium":
                    premium.addEntry(current.getName());
                    break;
                default:
                    spieler.addEntry(current.getName());
                    break;
            }
        }
        player.setScoreboard(scoreboard);
    }

    private void updateScoreboard(Player player) {
        if(player.getScoreboard() == null)
            setScoreboard(player);
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("aaa");
        if(objective == null)
            objective = scoreboard.registerNewObjective("aaa", "bbb");

        objective.getScore(updateScore(scoreboard, "group", "  §8* ", getGroup(player), ChatColor.GRAY)).setScore(10);
        objective.getScore(updateScore(scoreboard, "coins", "  §8* ", getCoins(player), ChatColor.YELLOW)).setScore(7);
        if(getAllFriends(player) == -1)
            objective.getScore(updateScore(scoreboard, "friends", "  §8* §7Lade...", "", ChatColor.AQUA)).setScore(4);
        else
            objective.getScore(updateScore(scoreboard, "friends", "  §8* §b"+getOnlineFriends(player), "§7/"+getAllFriends(player), ChatColor.AQUA)).setScore(4);
        objective.getScore(updateScore(scoreboard, "playtime", "  §8* ", getPlaytime(player), ChatColor.GREEN)).setScore(1);

        Team admin = registerTeam(scoreboard, "0001Admin", "§cAdmin §8● §c", "");
        Team developer = registerTeam(scoreboard, "0002Dev", "§bDev §8● §b", "");
        Team srmoderator = registerTeam(scoreboard, "0003SrMod", "§aSrMod §8● §a", "");
        Team moderator = registerTeam(scoreboard, "0004Mod", "§aMod §8● §a", "");
        Team supporter = registerTeam(scoreboard, "0005Sup", "§9Sup §8● §9", "");
        Team jrsupporter = registerTeam(scoreboard, "0006JrSup", "§9JrSup §8● §9", "");
        Team srbuilder = registerTeam(scoreboard, "0007SrBuild", "§2SrBuild §8● §2", "");
        Team builder = registerTeam(scoreboard, "0008Build", "§2Build §8● §2", "");
        Team jrbuilder = registerTeam(scoreboard, "0009JrBuild", "§2JrBuild §8● §2", "");
        Team content = registerTeam(scoreboard, "0010Content", "§dContent §8● §d", "");
        Team premium = registerTeam(scoreboard, "0011Prem", "§6Prem §8● §6", "");
        Team spieler = registerTeam(scoreboard, "0012Spieler", "§7Spieler §8● §7", "");

        for (Player current : Bukkit.getOnlinePlayers()) {
            String group = getRank(current);
            switch (group) {
                case "Admin":
                    admin.addEntry(current.getName());
                    break;
                case "Developer":
                    developer.addEntry(current.getName());
                    break;
                case "SrModerator":
                    srmoderator.addEntry(current.getName());
                    break;
                case "Moderator":
                    moderator.addEntry(current.getName());
                    break;
                case "Supporter":
                    supporter.addEntry(current.getName());
                    break;
                case "JrSupporter":
                    jrsupporter.addEntry(current.getName());
                    break;
                case "SrBuilder":
                    srbuilder.addEntry(current.getName());
                    break;
                case "Builder":
                    builder.addEntry(current.getName());
                    break;
                case "JrBuilder":
                    jrbuilder.addEntry(current.getName());
                    break;
                case "Content":
                    content.addEntry(current.getName());
                    break;
                case "Premium":
                    premium.addEntry(current.getName());
                    break;
                default:
                    spieler.addEntry(current.getName());
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        setScoreboard(player);
        Bukkit.getOnlinePlayers().forEach(this::updateScoreboard);
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(current ->{
                    updateScoreboard(current);
                });
            }
        }.runTaskTimer(instance, 0, 20);
    }

    private Team registerTeam(Scoreboard scoreBoard, String team, String prefix, String suffix) {
        Team rank = scoreBoard.getTeam(team);
        if(rank == null)
            rank = scoreBoard.registerNewTeam(team);
        rank.setPrefix(prefix);
        //rank.setSuffix(suffix);
        return rank;
    }

    private String updateScore(Scoreboard scoreBoard, String team, String prefix, String suffix, ChatColor entry) {
        Team rank = scoreBoard.getTeam(team);
        if(rank == null)
            rank = scoreBoard.registerNewTeam(team);
        rank.setPrefix(prefix);
        rank.setSuffix(suffix);
        rank.addEntry(entry.toString());
        return entry.toString();
    }

    private String getGroup(Player player) {
        DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(player.getUniqueId());
        if(dataCorePlayer != null)
            return dataCorePlayer.getRankPrefix()+dataCorePlayer.getRankName();
        else
            return "§7Lade...";
    }

    private String getCoins(Player player) {
        DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(player.getUniqueId());
        if(dataCorePlayer == null)
            return "§7Lade...";
        int rawCoins = dataCorePlayer.getCoins();
        String coins = String.format("%,d", rawCoins);
        coins = coins.replace(",", ".");
        return coins;
    }

    private String getPlaytime(Player player) {
        DataCorePlayer dataCorePlayer = BungeeDriver.getInstance().getCorePlayerManager().getCorePlayer(player.getUniqueId());
        if(dataCorePlayer == null)
            return "§7Lade...";
        long time = dataCorePlayer.getPlaytime();
        long seconds = time / 1000;
        int hours = (int) (seconds / 3600);
        String playtime = String.valueOf(hours)+" §7h";
        return playtime;
    }

    private String getRank(Player player) {
        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(player.getUniqueId());
        if(permissionUser != null)
            return CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName();
        else
            return "Spieler";
    }

    private int getOnlineFriends(Player player) {
        DataFriendPlayer dataFriendPlayer = BungeeDriver.getInstance().getFriendManager().getFriendPlayer(player.getUniqueId());
        if(dataFriendPlayer != null)
            return BungeeDriver.getInstance().getFriendManager().getFriendPlayer(player.getUniqueId()).getOnlineFriends().size();
        else
            return -1;
    }

    private int getAllFriends(Player player) {
        DataFriendPlayer dataFriendPlayer = BungeeDriver.getInstance().getFriendManager().getFriendPlayer(player.getUniqueId());
        if(dataFriendPlayer != null)
            return BungeeDriver.getInstance().getFriendManager().getFriendPlayer(player.getUniqueId()).getFriends().size();
        else
            return -1;
    }

}
