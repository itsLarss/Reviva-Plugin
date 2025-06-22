package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


import java.util.UUID;

public class HeartManager {
    
    private final Reviva plugin;
    private final DataManager dataManager;
    
    public HeartManager(Reviva plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    /**
     * Behandelt den Tod eines Spielers
     * @param player Der gestorbene Spieler
     */
    public void handlePlayerDeath(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Prüfen ob Spieler immun gegen Herzverlust ist
        if (player.hasPermission("reviva.death.immune")) {
            player.sendMessage(ChatColor.GREEN + "Du bist immun gegen Herzverlust!");
            return;
        }
        
        // Spielerdaten erstellen falls nicht vorhanden
        dataManager.createPlayerData(playerId);
        
        int currentHearts = dataManager.getPlayerHearts(playerId);
        
        if (currentHearts > 0) {
            // Herz abziehen
            int newHearts = currentHearts - 1;
            dataManager.setPlayerHearts(playerId, newHearts);
            
            // Nachricht senden
            if (newHearts > 0) {
                player.sendMessage(ChatColor.RED + "Du hast ein Herz verloren! " + 
                                 ChatColor.YELLOW + "Verbleibende Herzen: " + ChatColor.RED + newHearts);
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Du hast dein letztes Herz verloren!");
                player.sendMessage(ChatColor.GRAY + "Du wurdest automatisch gebannt. Nur andere Spieler können dich wiederbeleben.");
                
                // Spieler bannen
                plugin.getBanManager().banPlayer(playerId, "Keine Herzen mehr übrig");
            }
            
            // Server-Nachricht
            plugin.getServer().broadcastMessage(
                ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " ist gestorben! " +
                ChatColor.RED + "Herzen: " + newHearts + "/3"
            );
        }
    }
    
    /**
     * Gibt einem Spieler Herzen
     * @param playerId UUID des Spielers
     * @param hearts Anzahl der Herzen
     * @param admin Ob es ein Admin-Befehl ist
     */
    public void giveHearts(UUID playerId, int hearts, boolean admin) {
        dataManager.createPlayerData(playerId);
        
        int maxHearts = admin ? 10 : 3; // Admins können mehr als 3 Herzen geben
        int newHearts = Math.min(hearts, maxHearts);
        
        dataManager.setPlayerHearts(playerId, newHearts);
        
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "Deine Herzen wurden auf " + 
                             ChatColor.RED + newHearts + ChatColor.GREEN + " gesetzt!");
        }
    }
    
    /**
     * Entfernt Herzen von einem Spieler
     * @param playerId UUID des Spielers
     * @param hearts Anzahl der zu entfernenden Herzen
     */
    public void removeHearts(UUID playerId, int hearts) {
        dataManager.createPlayerData(playerId);
        
        int currentHearts = dataManager.getPlayerHearts(playerId);
        int newHearts = Math.max(0, currentHearts - hearts);
        
        dataManager.setPlayerHearts(playerId, newHearts);
        
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null) {
            player.sendMessage(ChatColor.RED + String.valueOf(hearts) + " Herzen wurden entfernt! " +
                             ChatColor.YELLOW + "Verbleibende Herzen: " + ChatColor.RED + String.valueOf(newHearts));
            
            // Wenn 0 Herzen, bannen
            if (newHearts == 0) {
                plugin.getBanManager().banPlayer(playerId, "Keine Herzen mehr übrig");
            }
        }
    }
    
    /**
     * Gibt die Herzen eines Spielers zurück
     * @param playerId UUID des Spielers
     * @return Anzahl der Herzen
     */
    public int getHearts(UUID playerId) {
        dataManager.createPlayerData(playerId);
        return dataManager.getPlayerHearts(playerId);
    }
    
    /**
     * Prüft ob ein Spieler noch Herzen hat
     * @param playerId UUID des Spielers
     * @return true wenn Spieler noch Herzen hat
     */
    public boolean hasHearts(UUID playerId) {
        return getHearts(playerId) > 0;
    }
    
    /**
     * Erstellt eine formatierte Herzanzeige
     * @param hearts Anzahl der Herzen
     * @return Formatierte Herzanzeige
     */
    public String getHeartDisplay(int hearts) {
        StringBuilder display = new StringBuilder();
        
        for (int i = 0; i < 3; i++) {
            if (i < hearts) {
                display.append(ChatColor.RED).append("❤");
            } else {
                display.append(ChatColor.GRAY).append("♡");
            }
        }
        
        return display.toString();
    }
    
    /**
     * Sendet Herzstatus an einen Spieler
     * @param player Der Spieler
     * @param targetId UUID des Zielspielers
     */
    public void sendHeartStatus(Player player, UUID targetId) {
        dataManager.createPlayerData(targetId);
        
        int hearts = dataManager.getPlayerHearts(targetId);
        int revives = dataManager.getPlayerRevives(targetId);
        boolean banned = dataManager.isPlayerBanned(targetId);
        
        String targetName = plugin.getServer().getOfflinePlayer(targetId).getName();
        
        player.sendMessage(ChatColor.GOLD + "=== Herzstatus von " + targetName + " ===");
        player.sendMessage(ChatColor.YELLOW + "Herzen: " + getHeartDisplay(hearts) + 
                         ChatColor.GRAY + " (" + hearts + "/3)");
        player.sendMessage(ChatColor.YELLOW + "Wiederbelebungen: " + ChatColor.WHITE + revives + "/2");
        player.sendMessage(ChatColor.YELLOW + "Status: " + 
                         (banned ? ChatColor.RED + "Gebannt" : ChatColor.GREEN + "Aktiv"));
    }
}