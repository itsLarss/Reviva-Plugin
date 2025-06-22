package de.reviva.plugin.listeners;

import de.reviva.plugin.Reviva;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {
    
    private final Reviva plugin;
    
    public PlayerJoinListener(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        // BanManager prüft ob Spieler beitreten darf
        if (!plugin.getBanManager().handleBannedPlayerLogin(event.getPlayer())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, 
                         "Du bist tot und kannst nicht beitreten!");
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Spielerdaten erstellen falls nicht vorhanden
        plugin.getDataManager().createPlayerData(event.getPlayer().getUniqueId());
        
        // Willkommensnachricht mit Herzstatus
        int hearts = plugin.getHeartManager().getHearts(event.getPlayer().getUniqueId());
        String heartDisplay = plugin.getHeartManager().getHeartDisplay(hearts);
        
        event.getPlayer().sendMessage("§7[§bReviva§7] §aDeine Herzen: " + heartDisplay + " §7(" + hearts + "/3)");
    }
}