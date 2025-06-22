package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CooldownManager {
    
    private final Reviva plugin;
    private final DataManager dataManager;
    
    private static final int COOLDOWN_HOURS = 12;
    
    public CooldownManager(Reviva plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    /**
     * Setzt den globalen Cooldown für Wiederbelebungen
     */
    public void setGlobalCooldown() {
        LocalDateTime now = LocalDateTime.now();
        dataManager.setLastReviveTimestamp(now);
        
        plugin.getLogger().info("Globaler Wiederbelebungs-Cooldown gesetzt: " + now);
    }
    
    /**
     * Prüft ob der globale Cooldown abgelaufen ist
     * @return true wenn Cooldown abgelaufen
     */
    public boolean isGlobalCooldownExpired() {
        LocalDateTime lastRevive = dataManager.getLastReviveTimestamp();
        LocalDateTime now = LocalDateTime.now();
        
        long hoursPassed = ChronoUnit.HOURS.between(lastRevive, now);
        return hoursPassed >= COOLDOWN_HOURS;
    }
    
    /**
     * Gibt die verbleibenden Stunden des Cooldowns zurück
     * @return Verbleibende Stunden
     */
    public long getGlobalCooldownHoursLeft() {
        if (isGlobalCooldownExpired()) {
            return 0;
        }
        
        LocalDateTime lastRevive = dataManager.getLastReviveTimestamp();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cooldownEnd = lastRevive.plusHours(COOLDOWN_HOURS);
        
        return ChronoUnit.HOURS.between(now, cooldownEnd);
    }
    
    /**
     * Gibt die verbleibenden Minuten des Cooldowns zurück
     * @return Verbleibende Minuten
     */
    public long getGlobalCooldownMinutesLeft() {
        if (isGlobalCooldownExpired()) {
            return 0;
        }
        
        LocalDateTime lastRevive = dataManager.getLastReviveTimestamp();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cooldownEnd = lastRevive.plusHours(COOLDOWN_HOURS);
        
        return ChronoUnit.MINUTES.between(now, cooldownEnd);
    }
    
    /**
     * Gibt eine formatierte Cooldown-Zeit zurück
     * @return Formatierte Zeit (z.B. "2h 30m")
     */
    public String getFormattedCooldownTime() {
        if (isGlobalCooldownExpired()) {
            return "Verfügbar";
        }
        
        long hoursLeft = getGlobalCooldownHoursLeft();
        long minutesLeft = getGlobalCooldownMinutesLeft() % 60;
        
        if (hoursLeft > 0) {
            return hoursLeft + "h " + minutesLeft + "m";
        } else {
            return minutesLeft + "m";
        }
    }
    
    /**
     * Gibt den Zeitpunkt der letzten Wiederbelebung zurück
     * @return LocalDateTime der letzten Wiederbelebung
     */
    public LocalDateTime getLastReviveTime() {
        return dataManager.getLastReviveTimestamp();
    }
    
    /**
     * Gibt den Zeitpunkt zurück, wann die nächste Wiederbelebung möglich ist
     * @return LocalDateTime der nächsten möglichen Wiederbelebung
     */
    public LocalDateTime getNextReviveTime() {
        return dataManager.getLastReviveTimestamp().plusHours(COOLDOWN_HOURS);
    }
    
    /**
     * Resettet den globalen Cooldown (Admin-Funktion)
     */
    public void resetGlobalCooldown() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(COOLDOWN_HOURS + 1);
        dataManager.setLastReviveTimestamp(pastTime);
        
        plugin.getLogger().info("Globaler Wiederbelebungs-Cooldown wurde zurückgesetzt.");
    }
    
    /**
     * Gibt detaillierte Cooldown-Informationen zurück
     * @return Detaillierte Info-String
     */
    public String getDetailedCooldownInfo() {
        LocalDateTime lastRevive = getLastReviveTime();
        LocalDateTime nextRevive = getNextReviveTime();
        LocalDateTime now = LocalDateTime.now();
        
        StringBuilder info = new StringBuilder();
        info.append("=== Wiederbelebungs-Cooldown ===\n");
        info.append("Letzte Wiederbelebung: ").append(lastRevive.toString()).append("\n");
        info.append("Nächste Wiederbelebung: ").append(nextRevive.toString()).append("\n");
        info.append("Aktuell: ").append(now.toString()).append("\n");
        info.append("Status: ").append(isGlobalCooldownExpired() ? "Verfügbar" : "Cooldown aktiv").append("\n");
        
        if (!isGlobalCooldownExpired()) {
            info.append("Verbleibende Zeit: ").append(getFormattedCooldownTime());
        }
        
        return info.toString();
    }
}