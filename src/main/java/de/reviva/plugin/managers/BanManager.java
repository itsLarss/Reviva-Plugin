package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


import java.util.Date;
import java.util.UUID;

public class BanManager {
    
    private final Reviva plugin;
    private final DataManager dataManager;
    
    public BanManager(Reviva plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    /**
     * Bannt einen Spieler wegen fehlender Herzen
     * @param playerId UUID des Spielers
     * @param reason Grund für den Bann
     */
    public void banPlayer(UUID playerId, String reason) {
        Player player = plugin.getServer().getPlayer(playerId);
        String playerName = player != null ? player.getName() : 
                           plugin.getServer().getOfflinePlayer(playerId).getName();
        
        // In Datenbank als gebannt markieren
        dataManager.setPlayerBanned(playerId, true);
        
        // Minecraft-Bann setzen
        BanList banList = plugin.getServer().getBanList(BanList.Type.NAME);
        banList.addBan(playerName, reason, (Date) null, "Reviva-System");
        
        // Spieler kicken falls online
        if (player != null && player.isOnline()) {
            player.kickPlayer(ChatColor.RED + "Du wurdest gebannt!\n\n" +
                            ChatColor.YELLOW + "Grund: " + reason + "\n" +
                            ChatColor.GRAY + "Du kannst nur durch andere Spieler wiederbelebt werden.\n" +
                            ChatColor.GREEN + "Verwende das 'Herz der Rückkehr' Item!");
        }
        
        // Server-Nachricht
        plugin.getServer().broadcastMessage(
            ChatColor.RED + playerName + " wurde gebannt! " +
            ChatColor.GRAY + "(" + reason + ")"
        );
        
        // Log
        plugin.getLogger().info("BANN: " + playerName + " wurde gebannt - " + reason);
    }
    
    /**
     * Entbannt einen Spieler
     * @param playerId UUID des Spielers
     */
    public void unbanPlayer(UUID playerId) {
        String playerName = plugin.getServer().getOfflinePlayer(playerId).getName();
        
        // Aus Datenbank entfernen
        dataManager.setPlayerBanned(playerId, false);
        
        // Minecraft-Bann entfernen
        BanList banList = plugin.getServer().getBanList(BanList.Type.NAME);
        banList.pardon(playerName);
        
        // Log
        plugin.getLogger().info("ENTBANNT: " + playerName + " wurde entbannt durch Wiederbelebung");
    }
    
    /**
     * Prüft ob ein Spieler gebannt ist
     * @param playerId UUID des Spielers
     * @return true wenn gebannt
     */
    public boolean isPlayerBanned(UUID playerId) {
        return dataManager.isPlayerBanned(playerId);
    }
    
    /**
     * Behandelt Login-Versuch eines gebannten Spielers
     * @param player Der Spieler
     * @return true wenn Login erlaubt
     */
    public boolean handleBannedPlayerLogin(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Bypass-Permission prüfen
        if (player.hasPermission("reviva.join.bypassdeath")) {
            return true;
        }
        
        // Prüfen ob Spieler gebannt ist
        if (isPlayerBanned(playerId)) {
            int hearts = dataManager.getPlayerHearts(playerId);
            int revives = dataManager.getPlayerRevives(playerId);
            
            // Kick-Nachricht
            player.kickPlayer(ChatColor.RED + "Du bist tot und kannst nicht beitreten!\n\n" +
                            ChatColor.YELLOW + "Herzen: " + ChatColor.RED + hearts + "/3\n" +
                            ChatColor.YELLOW + "Wiederbelebungen: " + ChatColor.WHITE + revives + "/2\n\n" +
                            ChatColor.GRAY + "Warte auf Wiederbelebung durch andere Spieler.\n" +
                            ChatColor.GREEN + "Benötigt: 'Herz der Rückkehr' Item");
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Gibt Bann-Informationen zurück
     * @param playerId UUID des Spielers
     * @return Bann-Info String
     */
    public String getBanInfo(UUID playerId) {
        if (!isPlayerBanned(playerId)) {
            return ChatColor.GREEN + "Spieler ist nicht gebannt.";
        }
        
        int hearts = dataManager.getPlayerHearts(playerId);
        int revives = dataManager.getPlayerRevives(playerId);
        String playerName = plugin.getServer().getOfflinePlayer(playerId).getName();
        
        return ChatColor.RED + playerName + " ist gebannt:\n" +
               ChatColor.YELLOW + "Herzen: " + ChatColor.RED + hearts + "/3\n" +
               ChatColor.YELLOW + "Wiederbelebungen: " + ChatColor.WHITE + revives + "/2";
    }
}