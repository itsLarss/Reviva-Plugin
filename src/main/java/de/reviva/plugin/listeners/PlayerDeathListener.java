package de.reviva.plugin.listeners;

import de.reviva.plugin.Reviva;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    
    private final Reviva plugin;
    
    public PlayerDeathListener(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // HeartManager behandelt den Tod
        plugin.getHeartManager().handlePlayerDeath(event.getEntity());
    }
}