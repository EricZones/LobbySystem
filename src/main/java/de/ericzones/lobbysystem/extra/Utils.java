// Created by Eric B. 17.05.2021 11:56
package de.ericzones.lobbysystem.extra;

public class Utils {

        /*
    Prefixes
     */

    // #### Server Management ####
    public static final String prefix_console = "§8[§b" + "LobbySystem" + "§8] §7";
    public static final String prefix_proxy = "§3•§b● §bProxy §8§l┃ ";
    public static final String prefix_cloud = "§3•§b● §bCloud §8§l┃ ";
    public static final String prefix_server = "§3•§b● §bServer §8§l┃ ";
    public static final String prefix_chat = "§3•§b● §bChat §8§l┃ ";

    // #### Freunde System ####
    public static final String prefix_freunde = "§3•§b● §bFreunde §8§l┃ ";
    public static final String prefix_party = "§3•§b● §bParty §8§l┃ ";
    public static final String prefix_msg = "§3•§b● §bMsg §8§l┃ ";

    // #### Spielmodi ####
    public static final String prefix_lobby = "§3•§b● §bLobby §8§l┃ ";
    public static final String prefix_ttt = "§4•§c● §cTTT §8§l┃ ";
    public static final String prefix_starbattle = "§1•§9● §9StarBattle §8§l┃ ";

    // #### Systeme ####
    public static final String prefix_coinsystem = "§6•§e● §eCoins §8§l┃ ";
    public static final String prefix_bannsystem = "§4•§c● §cPunish §8§l┃ ";
    public static final String prefix_reportsystem = "§4•§c● §cReport §8§l┃ ";
    public static final String prefix_anticheat = "§4•§c● §cAntiCheat §8§l┃ ";
    public static final String prefix_bausystem = "§2•§a● §aBauSystem §8§l┃ ";

    /*
    Messages
     */

    // #### Error Messages ####
    public static final String error_noperms = prefix_proxy+"§7Fehlende §cRechte §7für diesen Befehl";
    public static final String error_noconsole = prefix_console+"§cDieser Befehl ist nur als Spieler ausführbar";
    public static final String error_nocommand = prefix_proxy+"§7Dieser §cBefehl §7existiert nicht";
    public static final String error_notonline = "§7Dieser §cSpieler §7wurde nicht gefunden";
    public static final String error_noserver = prefix_proxy+"§7Dieser §cServer §7wurde nicht gefunden";
    public static final String error_alreadyconnected = prefix_proxy+"§7Du bist bereits auf diesem Server";
    public static final String error_targetalreadyconnected = prefix_proxy+"§7Dieser §cSpieler §7ist bereits auf diesem Server";

    // #### MySQL Messages ####
    public static final String sql_connected = prefix_console+"§7MySQL Verbindung aufgebaut";
    public static final String sql_disconnected = prefix_console+"§7MySQL Verbindung getrennt";
    public static final String sql_error = prefix_console+"§cEs konnte keine MySQL Verbindung aufgebaut werden";

}
