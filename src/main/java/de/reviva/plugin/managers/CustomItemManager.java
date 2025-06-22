package de.reviva.plugin.managers;

import de.reviva.plugin.Reviva;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class CustomItemManager {
    
    private final Reviva plugin;
    
    // Custom Item Keys
    private final NamespacedKey REVIVA_ITEM_KEY;
    private final NamespacedKey GLUT_ESSENZ_KEY;
    private final NamespacedKey KRISTALL_TRAENE_KEY;
    private final NamespacedKey SEELEN_BLATT_KEY;
    private final NamespacedKey PHANTOM_FEDER_KEY;
    
    public CustomItemManager(Reviva plugin) {
        this.plugin = plugin;
        
        // Keys initialisieren
        REVIVA_ITEM_KEY = new NamespacedKey(plugin, "reviva_item");
        GLUT_ESSENZ_KEY = new NamespacedKey(plugin, "glut_essenz");
        KRISTALL_TRAENE_KEY = new NamespacedKey(plugin, "kristall_traene");
        SEELEN_BLATT_KEY = new NamespacedKey(plugin, "seelen_blatt");
        PHANTOM_FEDER_KEY = new NamespacedKey(plugin, "phantom_feder");
    }
    
    /**
     * Erstellt das "Herz der Rückkehr" Item
     * @return ItemStack des Herz der Rückkehr
     */
    public ItemStack createHeartOfReturn() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Herz der Rückkehr");
        
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Ein mächtiges Artefakt, das die Kraft besitzt,",
            ChatColor.GRAY + "gefallene Seelen ins Leben zurückzurufen.",
            "",
            ChatColor.YELLOW + "Rechtsklick: " + ChatColor.WHITE + "Spieler wiederbeleben",
            "",
            ChatColor.RED + "⚠ " + ChatColor.GRAY + "Kann nur alle 12 Stunden verwendet werden",
            ChatColor.RED + "⚠ " + ChatColor.GRAY + "Maximale Wiederbelebungen pro Spieler: 2",
            "",
            ChatColor.DARK_PURPLE + "Seltenheit: " + ChatColor.LIGHT_PURPLE + "Legendär"
        );
        
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        // Custom Key setzen
        meta.getPersistentDataContainer().set(REVIVA_ITEM_KEY, PersistentDataType.STRING, "heart_of_return");
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Erstellt Essenz der Glut
     * @return ItemStack der Essenz der Glut
     */
    public ItemStack createGlutEssenz() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Essenz der Glut");
        
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Die brennende Essenz der Nether-Flammen,",
            ChatColor.GRAY + "gefangen in kristalliner Form.",
            "",
            ChatColor.YELLOW + "Benötigt für: " + ChatColor.RED + "Herz der Rückkehr",
            ChatColor.YELLOW + "Drop-Chance: " + ChatColor.WHITE + "10% (Nether-Mobs)",
            "",
            ChatColor.DARK_PURPLE + "Seltenheit: " + ChatColor.BLUE + "Selten"
        );
        
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(GLUT_ESSENZ_KEY, PersistentDataType.STRING, "glut_essenz");
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Erstellt Kristallträne
     * @return ItemStack der Kristallträne
     */
    public ItemStack createKristallTraene() {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Kristallträne");
        
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Die gefrorene Träne eines uralten",
            ChatColor.GRAY + "Ozean-Wächters voller magischer Kraft.",
            "",
            ChatColor.YELLOW + "Benötigt für: " + ChatColor.RED + "Herz der Rückkehr",
            ChatColor.YELLOW + "Drop-Chance: " + ChatColor.WHITE + "25% (Elder Guardian)",
            "",
            ChatColor.DARK_PURPLE + "Seltenheit: " + ChatColor.BLUE + "Selten"
        );
        
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(KRISTALL_TRAENE_KEY, PersistentDataType.STRING, "kristall_traene");
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Erstellt Seelenblatt
     * @return ItemStack des Seelenblatt
     */
    public ItemStack createSeelenBlatt() {
        ItemStack item = new ItemStack(Material.FERN);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Seelenblatt");
        
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Ein mystisches Blatt, das aus den Seelen",
            ChatColor.GRAY + "gefallener Kreaturen erwächst.",
            "",
            ChatColor.YELLOW + "Benötigt für: " + ChatColor.RED + "Herz der Rückkehr",
            ChatColor.YELLOW + "Drop-Chance: " + ChatColor.WHITE + "15% (Tod von Mobs)",
            "",
            ChatColor.DARK_PURPLE + "Seltenheit: " + ChatColor.BLUE + "Selten"
        );
        
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(SEELEN_BLATT_KEY, PersistentDataType.STRING, "seelen_blatt");
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Erstellt Phantomfeder
     * @return ItemStack der Phantomfeder
     */
    public ItemStack createPhantomFeder() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Phantomfeder");
        
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Eine geisterhafte Feder, die nur bei Vollmond",
            ChatColor.GRAY + "von Phantomen fallen gelassen wird.",
            "",
            ChatColor.YELLOW + "Benötigt für: " + ChatColor.RED + "Herz der Rückkehr",
            ChatColor.YELLOW + "Drop-Chance: " + ChatColor.WHITE + "50% (Phantom bei Vollmond)",
            "",
            ChatColor.DARK_PURPLE + "Seltenheit: " + ChatColor.BLUE + "Selten"
        );
        
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(PHANTOM_FEDER_KEY, PersistentDataType.STRING, "phantom_feder");
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Registriert alle Custom Recipes
     */
    public void registerRecipes() {
        // Herz der Rückkehr Recipe
        NamespacedKey recipeKey = new NamespacedKey(plugin, "heart_of_return_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createHeartOfReturn());
        
        recipe.shape("GKG", "SPS", "GKG");
        recipe.setIngredient('G', createGlutEssenz());
        recipe.setIngredient('K', createKristallTraene());
        recipe.setIngredient('S', createSeelenBlatt());
        recipe.setIngredient('P', createPhantomFeder());
        
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Custom Recipe für 'Herz der Rückkehr' registriert.");
    }
    
    /**
     * Prüft ob ein ItemStack ein Custom Item ist
     * @param item Das zu prüfende Item
     * @param itemType Der Item-Typ
     * @return true wenn es das gesuchte Custom Item ist
     */
    public boolean isCustomItem(ItemStack item, String itemType) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(getKeyForItemType(itemType), PersistentDataType.STRING)) {
            return false;
        }
        
        return meta.getPersistentDataContainer().get(getKeyForItemType(itemType), PersistentDataType.STRING)
                .equals(itemType);
    }
    
    /**
     * Gibt den NamespacedKey für einen Item-Typ zurück
     * @param itemType Der Item-Typ
     * @return NamespacedKey
     */
    private NamespacedKey getKeyForItemType(String itemType) {
        switch (itemType) {
            case "heart_of_return":
                return REVIVA_ITEM_KEY;
            case "glut_essenz":
                return GLUT_ESSENZ_KEY;
            case "kristall_traene":
                return KRISTALL_TRAENE_KEY;
            case "seelen_blatt":
                return SEELEN_BLATT_KEY;
            case "phantom_feder":
                return PHANTOM_FEDER_KEY;
            default:
                return REVIVA_ITEM_KEY;
        }
    }
    
    /**
     * Prüft ob ein ItemStack das Herz der Rückkehr ist
     * @param item Das zu prüfende Item
     * @return true wenn es das Herz der Rückkehr ist
     */
    public boolean isHeartOfReturn(ItemStack item) {
        return isCustomItem(item, "heart_of_return");
    }
}