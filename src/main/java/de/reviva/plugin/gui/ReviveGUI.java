package de.reviva.plugin.gui;

import de.reviva.plugin.Reviva;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReviveGUI implements Listener {
    
    private final Reviva plugin;
    private static final String GUI_TITLE = "§4§lWiederbelebung - Herz der Rückkehr";
    
    public ReviveGUI(Reviva plugin) {
        this.plugin = plugin;
        // Listener registrieren
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Öffnet die Wiederbelebungs-GUI für einen Spieler
     * @param player Der Spieler
     */
    public void openReviveGUI(Player player) {
        // Prüfen ob Spieler das Herz der Rückkehr hat
        if (!hasHeartOfReturn(player)) {
            player.sendMessage(ChatColor.RED + "Du benötigst das 'Herz der Rückkehr' um diese GUI zu verwenden!");
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        
        // Cooldown-Info oben
        addCooldownInfo(gui);
        
        // Wiederbelebbare Spieler laden
        List<OfflinePlayer> revivablePlayers = getRevivablePlayers(player);
        
        if (revivablePlayers.isEmpty()) {
            // Keine wiederbelebbaren Spieler
            addNoPlayersInfo(gui);
        } else {
            // Spieler-Köpfe hinzufügen
            addPlayerHeads(gui, revivablePlayers, player);
        }
        
        // Schließen-Button
        addCloseButton(gui);
        
        player.openInventory(gui);
    }
    
    private void addCooldownInfo(Inventory gui) {
        ItemStack cooldownItem = new ItemStack(Material.CLOCK);
        ItemMeta meta = cooldownItem.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "Wiederbelebungs-Cooldown");
        
        boolean cooldownExpired = plugin.getCooldownManager().isGlobalCooldownExpired();
        String timeLeft = plugin.getCooldownManager().getFormattedCooldownTime();
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        if (cooldownExpired) {
            lore.add(ChatColor.GREEN + "✓ Wiederbelebung verfügbar!");
        } else {
            lore.add(ChatColor.RED + "✗ Cooldown aktiv");
            lore.add(ChatColor.YELLOW + "Verbleibende Zeit: " + ChatColor.WHITE + timeLeft);
        }
        
        lore.add("");
        lore.add(ChatColor.GRAY + "Globaler Cooldown: 12 Stunden");
        
        meta.setLore(lore);
        cooldownItem.setItemMeta(meta);
        
        gui.setItem(4, cooldownItem);
    }
    
    private void addNoPlayersInfo(Inventory gui) {
        ItemStack noPlayersItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = noPlayersItem.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "Keine wiederbelebbaren Spieler");
        meta.setLore(Arrays.asList(
            "",
            ChatColor.GRAY + "Derzeit sind keine Spieler verfügbar,",
            ChatColor.GRAY + "die wiederbelebt werden können.",
            "",
            ChatColor.YELLOW + "Mögliche Gründe:",
            ChatColor.GRAY + "• Alle Spieler sind bereits aktiv",
            ChatColor.GRAY + "• Wiederbelebungslimit erreicht (2/2)",
            ChatColor.GRAY + "• Globaler Cooldown aktiv"
        ));
        
        noPlayersItem.setItemMeta(meta);
        gui.setItem(22, noPlayersItem);
    }
    
    private void addPlayerHeads(Inventory gui, List<OfflinePlayer> players, Player reviverPlayer) {
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        
        for (int i = 0; i < Math.min(players.size(), slots.length); i++) {
            OfflinePlayer deadPlayer = players.get(i);
            ItemStack head = createPlayerHead(deadPlayer, reviverPlayer);
            gui.setItem(slots[i], head);
        }
    }
    
    private ItemStack createPlayerHead(OfflinePlayer deadPlayer, Player reviverPlayer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        meta.setOwningPlayer(deadPlayer);
        meta.setDisplayName(ChatColor.RED + deadPlayer.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "Gebannt (0 Herzen)");
        
        int revives = plugin.getDataManager().getPlayerRevives(deadPlayer.getUniqueId());
        lore.add(ChatColor.GRAY + "Wiederbelebungen: " + ChatColor.WHITE + revives + "/2");
        
        lore.add("");
        
        // Prüfen ob Wiederbelebung möglich ist
        if (plugin.getReviveManager().canRevivePlayer(reviverPlayer, deadPlayer.getUniqueId())) {
            lore.add(ChatColor.GREEN + "✓ Wiederbelebung möglich");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Klicken zum Wiederbeleben");
        } else {
            lore.add(ChatColor.RED + "✗ Wiederbelebung nicht möglich");
            
            // Grund ermitteln
            if (revives >= 2 && !reviverPlayer.hasPermission("reviva.revive.bypasslimit")) {
                lore.add(ChatColor.GRAY + "Grund: Limit erreicht");
            } else if (!plugin.getCooldownManager().isGlobalCooldownExpired() && 
                      !reviverPlayer.hasPermission("reviva.revive.bypasscooldown")) {
                lore.add(ChatColor.GRAY + "Grund: Cooldown aktiv");
            }
        }
        
        meta.setLore(lore);
        head.setItemMeta(meta);
        
        return head;
    }
    
    private void addCloseButton(Inventory gui) {
        ItemStack closeButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = closeButton.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "Schließen");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "Klicken zum Schließen"));
        
        closeButton.setItemMeta(meta);
        gui.setItem(49, closeButton);
    }
    
    private List<OfflinePlayer> getRevivablePlayers(Player reviver) {
        List<OfflinePlayer> revivable = new ArrayList<>();
        
        // Durch alle gespeicherten Spieler iterieren
        // Hier sollte normalerweise die DataManager eine Methode haben um alle Spieler zu laden
        // Für jetzt verwenden wir eine vereinfachte Version
        
        for (OfflinePlayer offlinePlayer : plugin.getServer().getBannedPlayers()) {
            if (plugin.getDataManager().isPlayerBanned(offlinePlayer.getUniqueId())) {
                revivable.add(offlinePlayer);
            }
        }
        
        return revivable;
    }
    
    private boolean hasHeartOfReturn(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (plugin.getCustomItemManager().isHeartOfReturn(item)) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        
        // Schließen-Button
        if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
            player.closeInventory();
            return;
        }
        
        // Spieler-Kopf angeklickt
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            OfflinePlayer target = meta.getOwningPlayer();
            
            if (target != null) {
                handleReviveAttempt(player, target.getUniqueId());
            }
        }
    }
    
    private void handleReviveAttempt(Player reviver, UUID targetId) {
        // Herz der Rückkehr finden und entfernen
        ItemStack heartItem = null;
        for (ItemStack item : reviver.getInventory().getContents()) {
            if (plugin.getCustomItemManager().isHeartOfReturn(item)) {
                heartItem = item;
                break;
            }
        }
        
        if (heartItem == null) {
            reviver.sendMessage(ChatColor.RED + "Du hast kein 'Herz der Rückkehr' mehr!");
            reviver.closeInventory();
            return;
        }
        
        // Wiederbelebung versuchen
        if (plugin.getReviveManager().revivePlayer(reviver, targetId, heartItem)) {
            reviver.closeInventory();
            reviver.sendMessage(ChatColor.GREEN + "Wiederbelebung erfolgreich!");
        } else {
            // GUI aktualisieren
            openReviveGUI(reviver);
        }
    }
}