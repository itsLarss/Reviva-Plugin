package de.reviva.plugin.commands;

import de.reviva.plugin.Reviva;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HeartsCommand implements CommandExecutor, TabCompleter {
    
    private final Reviva plugin;
    
    public HeartsCommand(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("reviva.status.check")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Befehl!");
            return true;
        }
        
        // Kein Argument = eigene Herzen anzeigen
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Spielername erforderlich!");
                return true;
            }
            
            Player player = (Player) sender;
            plugin.getHeartManager().sendHeartStatus(player, player.getUniqueId());
            return true;
        }
        
        // Anderer Spieler
        String targetName = args[0];
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(targetName);
        
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Spieler '" + targetName + "' nicht gefunden!");
            return true;
        }
        
        // Berechtigung für andere Spieler prüfen
        if (!sender.hasPermission("reviva.hearts.viewall") && sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (!senderPlayer.getUniqueId().equals(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Du kannst nur deine eigenen Herzen anzeigen!");
                return true;
            }
        }
        
        if (sender instanceof Player) {
            plugin.getHeartManager().sendHeartStatus((Player) sender, target.getUniqueId());
        } else {
            // Console output
            int hearts = plugin.getHeartManager().getHearts(target.getUniqueId());
            int revives = plugin.getDataManager().getPlayerRevives(target.getUniqueId());
            boolean banned = plugin.getDataManager().isPlayerBanned(target.getUniqueId());
            
            sender.sendMessage("=== Herzstatus von " + target.getName() + " ===");
            sender.sendMessage("Herzen: " + hearts + "/3");
            sender.sendMessage("Wiederbelebungen: " + revives + "/2");
            sender.sendMessage("Status: " + (banned ? "Gebannt" : "Aktiv"));
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Online-Spieler vorschlagen
            plugin.getServer().getOnlinePlayers().forEach(p -> {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            });
        }
        
        return completions;
    }
}