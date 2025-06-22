package de.reviva.plugin.listeners;

import de.reviva.plugin.Reviva;
import org.bukkit.entity.*;

import java.util.UUID;

public class EntityDeathListener implements Listener {
    
    private final Reviva plugin;
    private final Random random;
    
    public EntityDeathListener(Reviva plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        
        // Nur von Spielern getötete Mobs
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (!(livingEntity.getKiller() instanceof Player)) {
                return;
            }
            Player killer = livingEntity.getKiller();
        
        // Custom Item Drops basierend auf Mob-Typ
        handleCustomDrops(event, entity, killer);
    }
    
    private void handleCustomDrops(EntityDeathEvent event, Entity entity, Player killer) {
        // Essenz der Glut - 10% Chance bei Nether-Mobs
        if (isNetherMob(entity)) {
            if (random.nextDouble() < 0.10) { // 10% Chance
                ItemStack glutEssenz = plugin.getCustomItemManager().createGlutEssenz();
                event.getDrops().add(glutEssenz);
                killer.sendMessage("§6§l✦ §eDu hast eine §6§lEssenz der Glut §eerbeutet!");
            }
        }
        
        // Kristallträne - 25% Chance bei Elder Guardian
        if (entity instanceof ElderGuardian) {
            if (random.nextDouble() < 0.25) { // 25% Chance
                ItemStack kristallTraene = plugin.getCustomItemManager().createKristallTraene();
                event.getDrops().add(kristallTraene);
                killer.sendMessage("§b§l✦ §3Du hast eine §b§lKristallträne §3erbeutet!");
            }
        }
        
        // Seelenblatt - 15% Chance bei allen Mobs (außer Phantoms)
        if (!(entity instanceof Phantom)) {
            if (random.nextDouble() < 0.15) { // 15% Chance
                ItemStack seelenBlatt = plugin.getCustomItemManager().createSeelenBlatt();
                event.getDrops().add(seelenBlatt);
                killer.sendMessage("§2§l✦ §aDu hast ein §2§lSeelenblatt §aerbeutet!");
            }
        }
        
        // Phantomfeder - 50% Chance bei Phantom während Vollmond
        if (entity instanceof Phantom) {
            if (isFullMoon(killer.getWorld()) && random.nextDouble() < 0.50) { // 50% bei Vollmond
                ItemStack phantomFeder = plugin.getCustomItemManager().createPhantomFeder();
                event.getDrops().add(phantomFeder);
                killer.sendMessage("§5§l✦ §dDu hast eine §5§lPhantomfeder §derbeutet! (Vollmond)");
            }
        }
    }
    
    /**
     * Prüft ob Entity ein Nether-Mob ist
     */
    private boolean isNetherMob(Entity entity) {
        return entity instanceof Blaze ||
               entity instanceof Ghast ||
               entity instanceof MagmaCube ||
               entity instanceof WitherSkeleton ||
               entity instanceof Wither ||
               entity instanceof Strider ||
               entity instanceof Hoglin ||
               entity instanceof Piglin ||
               entity instanceof PiglinBrute ||
               entity instanceof Zoglin;
    }
    
    /**
     * Prüft ob gerade Vollmond ist
     * Minecraft Full Moon Phase = 0
     */
    private boolean isFullMoon(org.bukkit.World world) {
        long time = world.getFullTime();
        // Minecraft moon phases: 0 = full moon, 4 = new moon
        // Moon cycle is 8 phases, each lasting 1000 ticks
        long moonPhase = (time / 24000) % 8;
        return moonPhase == 0;
    }
}