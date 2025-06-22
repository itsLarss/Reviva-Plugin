package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

public class ReviveManager {
    
    private final Reviva plugin;
    private final DataManager dataManager;
    private final CooldownManager cooldownManager;
    
    public ReviveManager(Reviva plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.cooldownManager = plugin.getCooldownManager();
    }
    
    /**
     * Versucht einen Spieler wiederzubeleben
     * @param reviver Der Spieler, der wiederbelebt
     * @param targetId UUID des zu wiederbelebenden Spielers
     * @param reviveItem Das verwendete Wiederbelebungsitem
     * @return true wenn erfolgreich
     */
    public boolean revivePlayer(Player reviver, UUID targetId, ItemStack reviveItem) {
        // Grundlegende Prüfungen
        if (!canRevivePlayer(reviver, targetId)) {
            return false;
        }
        
        // Item entfernen
        if (reviveItem.getAmount() > 1) {
            reviveItem.setAmount(reviveItem.getAmount() - 1);
        } else {
            reviver.getInventory().removeItem(reviveItem);
        }
        
        // Wiederbelebung durchführen
        performRevive(reviver, targetId);
        
        return true;
    }
    
    /**
     * Prüft ob eine Wiederbelebung möglich ist
     * @param reviver Der wiederbelebende Spieler
     * @param targetId UUID des Ziels
     * @return true wenn möglich
     */
    public boolean canRevivePlayer(Player reviver, UUID targetId) {
        // Permissions prüfen
        if (!reviver.hasPermission("reviva.revive.use")) {
            reviver.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für Wiederbelebungen!");
            return false;
        }
        
        // Spielerdaten laden
        dataManager.createPlayerData(targetId);
        
        String targetName = plugin.getServer().getOfflinePlayer(targetId).getName();
        
        // Prüfen ob Ziel gebannt ist
        if (!dataManager.isPlayerBanned(targetId)) {
            reviver.sendMessage(ChatColor.RED + targetName + " ist nicht gebannt und benötigt keine Wiederbelebung!");
            return false;
        }
        
        // Prüfen ob Ziel noch Herzen hat
        if (dataManager.getPlayerHearts(targetId) > 0) {
            reviver.sendMessage(ChatColor.RED + targetName + " hat noch Herzen und benötigt keine Wiederbelebung!");
            return false;
        }
        
        // Wiederbelebungslimit prüfen
        if (!reviver.hasPermission("reviva.revive.bypasslimit")) {
            int revives = dataManager.getPlayerRevives(targetId);
            if (revives >= 2) {
                reviver.sendMessage(ChatColor.RED + targetName + " kann nicht mehr wiederbelebt werden! (Limit: 2/2)");
                return false;
            }
        }
        
        // Globalen Cooldown prüfen
        if (!reviver.hasPermission("reviva.revive.bypasscooldown")) {
            if (!cooldownManager.isGlobalCooldownExpired()) {
                long hoursLeft = cooldownManager.getGlobalCooldownHoursLeft();
                reviver.sendMessage(ChatColor.RED + "Wiederbelebung noch nicht möglich! Warte noch " + 
                                  hoursLeft + " Stunden.");
                return false;
            }
        }
        
        // Prüfen ob Ziel immun gegen Wiederbelebung ist
        if (plugin.getServer().getOfflinePlayer(targetId).getPlayer() != null) {
            Player target = plugin.getServer().getOfflinePlayer(targetId).getPlayer();
            if (target.hasPermission("reviva.revive.immune")) {
                reviver.sendMessage(ChatColor.RED + targetName + " kann nicht wiederbelebt werden!");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Führt die Wiederbelebung durch
     * @param reviver Der wiederbelebende Spieler
     * @param targetId UUID des Ziels
     */
    private void performRevive(Player reviver, UUID targetId) {
        String targetName = plugin.getServer().getOfflinePlayer(targetId).getName();
        
        // Spieler entbannen
        plugin.getBanManager().unbanPlayer(targetId);
        
        // Herz geben
        dataManager.setPlayerHearts(targetId, 1);
        
        // Wiederbelebungscounter erhöhen
        dataManager.incrementPlayerRevives(targetId);
        
        // Globalen Cooldown setzen
        cooldownManager.setGlobalCooldown();
        
        // Nachrichten senden
        reviver.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.GOLD + targetName + 
                          ChatColor.GREEN + " erfolgreich wiederbelebt!");
        
        // Server-Nachricht
        plugin.getServer().broadcastMessage(
            ChatColor.GOLD + reviver.getName() + ChatColor.GREEN + " hat " + 
            ChatColor.GOLD + targetName + ChatColor.GREEN + " wiederbelebt! " +
            ChatColor.YELLOW + "Nächste Wiederbelebung in 12 Stunden möglich."
        );
        
        // Wiederbelebungsstatistik
        int revivesUsed = dataManager.getPlayerRevives(targetId);
        plugin.getServer().broadcastMessage(
            ChatColor.GRAY + targetName + " wurde " + revivesUsed + "/2 mal wiederbelebt."
        );
        
        // Log
        plugin.getLogger().info("WIEDERBELEBUNG: " + reviver.getName() + " hat " + targetName + " wiederbelebt. " +
                              "Wiederbelebungen: " + revivesUsed + "/2");
    }
    
    /**
     * Gibt Informationen über mögliche Wiederbelebungen zurück
     * @param player Der anfragende Spieler
     * @return Anzahl der wiederbelebbaren Spieler
     */
    public int getRevivablePlayersCount(Player player) {
        int count = 0;
        
        // Durch alle gebannten Spieler iterieren
        for (OfflinePlayer banned : plugin.getServer().getBannedPlayers()) {
            if (canRevivePlayer(player, banned.getUniqueId())) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Sendet Wiederbelebungsinfo an Spieler
     * @param player Der Spieler
     */
    public void sendReviveInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Wiederbelebungs-Info ===");
        
        // Globaler Cooldown
        if (cooldownManager.isGlobalCooldownExpired()) {
            player.sendMessage(ChatColor.GREEN + "Wiederbelebung verfügbar!");
        } else {
            long hoursLeft = cooldownManager.getGlobalCooldownHoursLeft();
            player.sendMessage(ChatColor.RED + "Wiederbelebung in " + hoursLeft + " Stunden verfügbar.");
        }
        
        // Wiederbelebbare Spieler
        int revivable = getRevivablePlayersCount(player);
        player.sendMessage(ChatColor.YELLOW + "Wiederbelebbare Spieler: " + revivable);
        
        // Permissions
        if (player.hasPermission("reviva.revive.bypasscooldown")) {
            player.sendMessage(ChatColor.AQUA + "Du kannst den Cooldown umgehen.");
        }
        if (player.hasPermission("reviva.revive.bypasslimit")) {
            player.sendMessage(ChatColor.AQUA + "Du kannst das Wiederbelebungslimit umgehen.");
        }
    }
}