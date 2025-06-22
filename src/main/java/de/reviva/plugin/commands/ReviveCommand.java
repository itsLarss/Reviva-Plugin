package de.reviva.plugin.commands;

import de.reviva.plugin.Reviva;
import de.reviva.plugin.gui.ReviveGUI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReviveCommand implements CommandExecutor, TabCompleter {
    
    private final Reviva plugin;
    
    public ReviveCommand(Reviva plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Hauptbefehl /reviva
        if (command.getName().equalsIgnoreCase("reviva")) {
            return handleRevivaCommand(sender, args);
        }
        
        // GUI-Befehl /revive
        if (command.getName().equalsIgnoreCase("revive")) {
            return handleReviveGUICommand(sender);
        }
        
        return false;
    }
    
    private boolean handleRevivaCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
                
            case "info":
                sendInfo(sender);
                break;
                
            case "status":
                handleStatusCommand(sender, args);
                break;
                
            case "hearts":
                handleHeartsCommand(sender, args);
                break;
                
            case "cooldown":
                handleCooldownCommand(sender);
                break;
                
            case "reload":
                handleReloadCommand(sender);
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Unbekannter Befehl! Verwende " + 
                                 ChatColor.YELLOW + "/reviva help");
        }
        
        return true;
    }
    
    private boolean handleReviveGUICommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können die GUI verwenden!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("reviva.revive.gui")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für die Wiederbelebungs-GUI!");
            return true;
        }
        
        ReviveGUI gui = new ReviveGUI(plugin);
        gui.openReviveGUI(player);
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Reviva Plugin Hilfe ===");
        sender.sendMessage(ChatColor.YELLOW + "/reviva help" + ChatColor.GRAY + " - Diese Hilfe anzeigen");
        sender.sendMessage(ChatColor.YELLOW + "/reviva info" + ChatColor.GRAY + " - Plugin-Informationen");
        sender.sendMessage(ChatColor.YELLOW + "/reviva status [Spieler]" + ChatColor.GRAY + " - Herzstatus anzeigen");
        sender.sendMessage(ChatColor.YELLOW + "/reviva cooldown" + ChatColor.GRAY + " - Wiederbelebungs-Cooldown");
        sender.sendMessage(ChatColor.YELLOW + "/hearts [Spieler]" + ChatColor.GRAY + " - Herzstatus anzeigen");
        sender.sendMessage(ChatColor.YELLOW + "/revive" + ChatColor.GRAY + " - Wiederbelebungs-GUI öffnen");
        
        if (sender.hasPermission("reviva.admin")) {
            sender.sendMessage(ChatColor.RED + "=== Admin-Befehle ===");
            sender.sendMessage(ChatColor.YELLOW + "/reviva hearts set <Spieler> <Anzahl>" + ChatColor.GRAY + " - Herzen setzen");
            sender.sendMessage(ChatColor.YELLOW + "/reviva hearts give <Spieler> <Anzahl>" + ChatColor.GRAY + " - Herzen geben");
            sender.sendMessage(ChatColor.YELLOW + "/reviva hearts remove <Spieler> <Anzahl>" + ChatColor.GRAY + " - Herzen entfernen");
            sender.sendMessage(ChatColor.YELLOW + "/reviva reload" + ChatColor.GRAY + " - Plugin neu laden");
        }
    }
    
    private void sendInfo(CommandSender sender) {
        if (sender instanceof Player) {
            plugin.getReviveManager().sendReviveInfo((Player) sender);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Reviva Plugin v1.0.0");
            sender.sendMessage(ChatColor.YELLOW + "Virtuelles Herz- und Wiederbelebungssystem");
        }
    }
    
    private void handleStatusCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("reviva.status.check")) {
            sender.sendMessage(ChatColor.RED + "Keine Berechtigung!");
            return;
        }
        
        if (args.length < 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                plugin.getHeartManager().sendHeartStatus(player, player.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "Spielername erforderlich!");
            }
            return;
        }
        
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
            return;
        }
        
        if (sender instanceof Player) {
            plugin.getHeartManager().sendHeartStatus((Player) sender, target.getUniqueId());
        } else {
            // Console output
            int hearts = plugin.getHeartManager().getHearts(target.getUniqueId());
            sender.sendMessage("Herzstatus von " + target.getName() + ": " + hearts + "/3");
        }
    }
    
    private void handleHeartsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("reviva.hearts.set")) {
            sender.sendMessage(ChatColor.RED + "Keine Berechtigung!");
            return;
        }
        
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Verwendung: /reviva hearts <set|give|remove> <Spieler> <Anzahl>");
            return;
        }
        
        String action = args[1].toLowerCase();
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[2]);
        
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
            return;
        }
        
        try {
            int amount = Integer.parseInt(args[3]);
            UUID targetId = target.getUniqueId();
            
            switch (action) {
                case "set":
                    plugin.getHeartManager().giveHearts(targetId, amount, true);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " hat jetzt " + amount + " Herzen.");
                    break;
                    
                case "give":
                    int currentHearts = plugin.getHeartManager().getHearts(targetId);
                    plugin.getHeartManager().giveHearts(targetId, currentHearts + amount, true);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " hat " + amount + " Herzen erhalten.");
                    break;
                    
                case "remove":
                    plugin.getHeartManager().removeHearts(targetId, amount);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " hat " + amount + " Herzen verloren.");
                    break;
                    
                default:
                    sender.sendMessage(ChatColor.RED + "Unbekannte Aktion! Verwende: set, give, remove");
            }
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Ungültige Anzahl!");
        }
    }
    
    private void handleCooldownCommand(CommandSender sender) {
        String cooldownInfo = plugin.getCooldownManager().getDetailedCooldownInfo();
        sender.sendMessage(ChatColor.GOLD + cooldownInfo);
    }
    
    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("reviva.admin")) {
            sender.sendMessage(ChatColor.RED + "Keine Berechtigung!");
            return;
        }
        
        plugin.reloadConfig();
        plugin.getDataManager().saveData();
        sender.sendMessage(ChatColor.GREEN + "Reviva Plugin wurde neu geladen!");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("reviva")) {
            if (args.length == 1) {
                completions.addAll(Arrays.asList("help", "info", "status", "cooldown", "hearts", "reload"));
            } else if (args.length == 2 && args[0].equalsIgnoreCase("hearts")) {
                completions.addAll(Arrays.asList("set", "give", "remove"));
            } else if (args.length == 3 && args[0].equalsIgnoreCase("hearts")) {
                // Spielernamen hinzufügen
                plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
            }
        }
        
        return completions;
    }
}