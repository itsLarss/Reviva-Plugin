package de.reviva.plugin.listeners;

import de.reviva.plugin.Reviva;
import de.reviva.plugin.gui.ReviveGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {
    
    private final Reviva plugin;
    
    public InteractListener(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Prüfen ob Spieler das Herz der Rückkehr verwendet
        if (item != null && plugin.getCustomItemManager().isHeartOfReturn(item)) {
            event.setCancelled(true);
            
            // Permission prüfen
            if (!player.hasPermission("reviva.revive.use")) {
                player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, das Herz der Rückkehr zu verwenden!");
                return;
            }
            
            // GUI öffnen falls Permission vorhanden
            if (player.hasPermission("reviva.revive.gui")) {
                ReviveGUI gui = new ReviveGUI(plugin);
                gui.openReviveGUI(player);
            } else {
                // Fallback: Direkte Wiederbelebung des ersten verfügbaren Spielers
                handleDirectRevive(player, item);
            }
        }
    }
    
    /**
     * Behandelt direkte Wiederbelebung ohne GUI
     */
    private void handleDirectRevive(Player player, ItemStack item) {
        // Hier könnte eine Liste aller gebannten/wiederbelebbaren Spieler abgerufen werden
        // Für Simplicität nehmen wir an, dass eine einfache Nachricht gesendet wird
        
        player.sendMessage(ChatColor.YELLOW + "Verwende " + ChatColor.GOLD + "/revive" + 
                         ChatColor.YELLOW + " um die Wiederbelebungs-GUI zu öffnen!");
        player.sendMessage(ChatColor.GRAY + "Oder verwende " + ChatColor.WHITE + "/reviva info" + 
                         ChatColor.GRAY + " für weitere Informationen.");
    }
}