package de.reviva.plugin;

import de.reviva.plugin.commands.ReviveCommand;
import de.reviva.plugin.commands.HeartsCommand;
import de.reviva.plugin.listeners.PlayerDeathListener;
import de.reviva.plugin.listeners.PlayerJoinListener;
import de.reviva.plugin.listeners.EntityDeathListener;
import de.reviva.plugin.listeners.CraftingListener;
import de.reviva.plugin.listeners.InteractListener;
import de.reviva.plugin.managers.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public class Reviva extends JavaPlugin {
    
    private static Reviva instance;
    private Logger logger;
    
    // Manager instances
    private DataManager dataManager;
    private HeartManager heartManager;
    private ReviveManager reviveManager;
    private CustomItemManager customItemManager;
    private BanManager banManager;
    private CooldownManager cooldownManager;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        logger.info("Reviva Plugin wird geladen...");
        
        // Config erstellen falls nicht vorhanden
        saveDefaultConfig();
        
        // Manager initialisieren
        initializeManagers();
        
        // Commands registrieren
        registerCommands();
        
        // Event Listener registrieren
        registerListeners();
        
        // Custom Recipes registrieren
        customItemManager.registerRecipes();
        
        logger.info("Reviva Plugin erfolgreich geladen!");
        logger.info("Virtuelles Herzsystem aktiv - Viel Spaß!");
    }
    
    @Override
    public void onDisable() {
        logger.info("Reviva Plugin wird deaktiviert...");
        
        // Daten speichern
        if (dataManager != null) {
            dataManager.saveData();
        }
        
        logger.info("Alle Daten gespeichert. Reviva Plugin deaktiviert.");
    }
    
    private void initializeManagers() {
        // Reihenfolge ist wichtig - DataManager zuerst
        dataManager = new DataManager(this);
        heartManager = new HeartManager(this);
        cooldownManager = new CooldownManager(this);
        reviveManager = new ReviveManager(this);
        customItemManager = new CustomItemManager(this);
        banManager = new BanManager(this);
        
        logger.info("Alle Manager erfolgreich initialisiert.");
    }
    
    private void registerCommands() {
        getCommand("reviva").setExecutor(new ReviveCommand(this));
        getCommand("hearts").setExecutor(new HeartsCommand(this));
        getCommand("revive").setExecutor(new ReviveCommand(this));
        
        logger.info("Commands registriert.");
    }
    
    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new EntityDeathListener(this), this);
        pm.registerEvents(new CraftingListener(this), this);
        pm.registerEvents(new InteractListener(this), this);
        
        logger.info("Event Listener registriert.");
    }
    
    // Getter für andere Klassen
    public static Reviva getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public HeartManager getHeartManager() {
        return heartManager;
    }
    
    public ReviveManager getReviveManager() {
        return reviveManager;
    }
    
    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }
    
    public BanManager getBanManager() {
        return banManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}