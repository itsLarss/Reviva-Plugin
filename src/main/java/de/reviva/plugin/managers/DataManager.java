package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DataManager {
    
    private final Reviva plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public DataManager(Reviva plugin) {
        this.plugin = plugin;
        setupDataFile();
    }
    
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        
        if (!dataFile.exists()) {
            plugin.saveResource("playerdata.yml", false);
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Standard-Werte setzen falls nicht vorhanden
        if (!dataConfig.contains("revive_settings.last_revive_timestamp")) {
            dataConfig.set("revive_settings.last_revive_timestamp", "2025-01-01T00:00:00");
            saveData();
        }
    }
    
    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern der Daten: " + e.getMessage());
        }
    }
    
    // Spieler-Herzen
    public int getPlayerHearts(UUID playerId) {
        return dataConfig.getInt("players." + playerId.toString() + ".hearts", 3);
    }
    
    public void setPlayerHearts(UUID playerId, int hearts) {
        dataConfig.set("players." + playerId.toString() + ".hearts", Math.max(0, hearts));
        saveData();
    }
    
    // Wiederbelebungen
    public int getPlayerRevives(UUID playerId) {
        return dataConfig.getInt("players." + playerId.toString() + ".revives_used", 0);
    }
    
    public void setPlayerRevives(UUID playerId, int revives) {
        dataConfig.set("players." + playerId.toString() + ".revives_used", Math.max(0, revives));
        saveData();
    }
    
    public void incrementPlayerRevives(UUID playerId) {
        int currentRevives = getPlayerRevives(playerId);
        setPlayerRevives(playerId, currentRevives + 1);
    }
    
    // Bann-Status
    public boolean isPlayerBanned(UUID playerId) {
        return dataConfig.getBoolean("players." + playerId.toString() + ".banned", false);
    }
    
    public void setPlayerBanned(UUID playerId, boolean banned) {
        dataConfig.set("players." + playerId.toString() + ".banned", banned);
        saveData();
    }
    
    // Globaler Wiederbelebungs-Zeitstempel
    public LocalDateTime getLastReviveTimestamp() {
        String timestamp = dataConfig.getString("revive_settings.last_revive_timestamp", "2025-01-01T00:00:00");
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public void setLastReviveTimestamp(LocalDateTime timestamp) {
        dataConfig.set("revive_settings.last_revive_timestamp", timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        saveData();
    }
    
    // Hilfsmethoden
    public boolean hasPlayerData(UUID playerId) {
        return dataConfig.contains("players." + playerId.toString());
    }
    
    public void createPlayerData(UUID playerId) {
        if (!hasPlayerData(playerId)) {
            String path = "players." + playerId.toString();
            dataConfig.set(path + ".hearts", 3);
            dataConfig.set(path + ".revives_used", 0);
            dataConfig.set(path + ".banned", false);
            saveData();
        }
    }
    
    // Cleanup alte Daten (optional)
    public void cleanupOldData() {
        // Implementierung für das Entfernen alter/inaktiver Spielerdaten
        // Kann später erweitert werden
    }
}