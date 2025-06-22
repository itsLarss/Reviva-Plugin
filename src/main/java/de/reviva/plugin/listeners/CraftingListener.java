package de.reviva.plugin.listeners;

import de.reviva.plugin.Reviva;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {
    
    private final Reviva plugin;
    
    public CraftingListener(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        
        // Prüfen ob Herz der Rückkehr gecraftet wird
        if (plugin.getCustomItemManager().isHeartOfReturn(result)) {
            Player player = (Player) event.getWhoClicked();
            
            // Permission prüfen
            if (!player.hasPermission("reviva.revive.use")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, das Herz der Rückkehr zu craften!");
                return;
            }
            
            // Erfolgs-Nachricht
            player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.GREEN + "Du hast das " + 
                             ChatColor.RED + ChatColor.BOLD + "Herz der Rückkehr" + 
                             ChatColor.GREEN + " erfolgreich gecraftet!");
            player.sendMessage(ChatColor.YELLOW + "Verwende es, um gefallene Spieler wiederzubeleben!");
            
            // Server-Broadcast
            plugin.getServer().broadcastMessage(
                ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " hat das " +
                ChatColor.RED + ChatColor.BOLD + "Herz der Rückkehr" + 
                ChatColor.YELLOW + " gecraftet!"
            );
            
            // Log
            plugin.getLogger().info("CRAFTING: " + player.getName() + " hat das Herz der Rückkehr gecraftet.");
        }
    }
}