# Reviva Plugin

Ein vollständiges Minecraft Spigot/Paper Plugin für ein virtuelles Herz- und Wiederbelebungssystem.

## 📋 Übersicht

**Reviva** ist ein Minecraft-Plugin, das ein serverseitiges Herzensystem verwaltet. Spieler verlieren durch Tode virtuelle Herzen, werden bei 0 Herzen gebannt und können von anderen Spielern mit besonderen Items wiederbelebt werden.

## ⭐ Features

### 🫀 Herzsystem
- Jeder Spieler startet mit **3 virtuellen Herzen**
- **1 Herz Verlust** bei jedem Tod (unabhängig von Todesart)
- **Automatischer Bann** bei 0 Herzen
- Persistente Speicherung aller Herzstände

### 🔄 Wiederbelebungssystem
- Verwendung des **"Herz der Rückkehr"** Items
- Maximale Wiederbelebungen: **2 pro Spieler**
- **12 Stunden globaler Cooldown** zwischen Wiederbelebungen
- Nach Wiederbelebung: Entbannung + 1 Herz zurück

### ⚡ Custom Items

#### Herz der Rückkehr (Nether Star)
Wird aus 4 seltenen Materialien + Nether Star gecraftet:

1. **Essenz der Glut** (Blaze Powder) - 10% Drop bei Nether-Mobs
2. **Kristallträne** (Prismarine Crystal) - 25% Drop bei Elder Guardians  
3. **Seelenblatt** (Fern) - 15% Drop bei sterbenden Mobs
4. **Phantomfeder** (Feather) - 50% Drop bei Phantomen während Vollmond

#### Crafting Recipe:
```
G K G
S P S  
G K G

G = Essenz der Glut
K = Kristallträne  
S = Seelenblatt
P = Phantomfeder
```

## 🔧 Installation

1. **Voraussetzungen:**
   - Minecraft Server 1.21+
   - Spigot oder Paper
   - Java 17+

2. **Plugin installieren:**
   ```bash
   # Plugin kompilieren
   mvn clean package
   
   # JAR-Datei in plugins/ Ordner kopieren
   cp target/Reviva-1.0.0.jar /path/to/server/plugins/
   
   # Server neustarten
   ```

3. **Erste Schritte:**
   - Plugin startet automatisch
   - Konfiguration in `plugins/Reviva/playerdata.yml`
   - Permissions mit LuckPerms oder anderem Plugin setzen

## 📝 Commands

### Spieler Commands
| Command | Beschreibung | Permission |
| ------- | ------------ | ---------- |
| `/hearts [Spieler]` | Herzstatus anzeigen | `reviva.status.check` |
| `/revive` | Wiederbelebungs-GUI öffnen | `reviva.revive.gui` |
| `/reviva info` | Plugin-Informationen | - |
| `/reviva help` | Hilfe anzeigen | - |
| `/reviva cooldown` | Cooldown-Status | - |

### Admin Commands
| Command | Beschreibung | Permission |
| ------- | ------------ | ---------- |
| `/reviva hearts set <Spieler> <Anzahl>` | Herzen setzen | `reviva.hearts.set` |
| `/reviva hearts give <Spieler> <Anzahl>` | Herzen geben | `reviva.hearts.set` |
| `/reviva hearts remove <Spieler> <Anzahl>` | Herzen entfernen | `reviva.hearts.set` |
| `/reviva status <Spieler>` | Detaillierter Status | `reviva.hearts.viewall` |
| `/reviva reload` | Plugin neu laden | `reviva.admin` |

## 🛡️ Permissions

### Grundlegende Permissions
- `reviva.revive.use` - Herz der Rückkehr verwenden *(Standard: true)*
- `reviva.revive.gui` - Wiederbelebungs-GUI öffnen *(Standard: true)*
- `reviva.status.check` - Herzstatus anzeigen *(Standard: true)*

### Bypass Permissions
- `reviva.revive.bypasscooldown` - Cooldown ignorieren *(Standard: op)*
- `reviva.revive.bypasslimit` - Mehr als 2 Wiederbelebungen *(Standard: op)*
- `reviva.join.bypassdeath` - Trotz Tod joinen *(Standard: op)*
- `reviva.death.immune` - Kein Herzverlust *(Standard: op)*
- `reviva.revive.immune` - Kann nicht wiederbelebt werden *(Standard: false)*

### Admin Permissions
- `reviva.admin` - Vollzugriff *(Standard: op)*
- `reviva.hearts.set` - Herzen verwalten *(Standard: op)*
- `reviva.hearts.viewall` - Alle Spieler anzeigen *(Standard: op)*

## 🎮 Spielmechaniken

### Herzenverlust
- **Jeder Tod** = -1 Herz
- **0 Herzen** = Automatischer Bann
- **Todesart irrelevant** (PvP, Mobs, Fall, etc.)

### Wiederbelebung
- **Erforderlich:** Herz der Rückkehr Item
- **Cooldown:** 12 Stunden global
- **Limit:** 2 Wiederbelebungen pro Spieler
- **Effekt:** Entbannung + 1 Herz

### Custom Item Drops
- **Nether-Mobs:** 10% Chance auf Essenz der Glut
- **Elder Guardian:** 25% Chance auf Kristallträne
- **Alle Mobs:** 15% Chance auf Seelenblatt
- **Phantome bei Vollmond:** 50% Chance auf Phantomfeder

## 💾 Datenspeicherung

### Struktur (playerdata.yml)
```yaml
players:
  550e8400-e29b-41d4-a716-446655440000:
    hearts: 2
    revives_used: 1
    banned: false
    
revive_settings:
  last_revive_timestamp: "2025-01-22T15:30:00"
```

### Gespeicherte Daten
- **Herzanzahl** pro Spieler (0-3)
- **Wiederbelebungen** pro Spieler (0-2)
- **Bann-Status** (true/false)
- **Globaler Cooldown** (Zeitstempel)

## 🔧 Konfiguration

### LuckPerms Integration
```bash
# Basis-Permissions für alle Spieler
lp group default permission set reviva.revive.use true
lp group default permission set reviva.revive.gui true
lp group default permission set reviva.status.check true

# VIP-Permissions (Cooldown umgehen)
lp group vip permission set reviva.revive.bypasscooldown true

# Admin-Permissions
lp group admin permission set reviva.admin true
```

### Vollmond-Erkennung
Das Plugin erkennt automatisch Vollmond-Phasen für Phantomfeder-Drops:
- Minecraft Mondphasen: 0-7
- Vollmond = Phase 0
- Automatische Berechnung basierend auf Weltzeit

## 📊 Statistiken & Logs

### Automatische Logs
- Herzenverluste bei Tod
- Wiederbelebungen (Wer hat wen wiederbelebt)
- Item-Crafting von Herz der Rückkehr
- Cooldown-Resets

### Beispiel-Log
```
[INFO] [Reviva] HERZENVERLUST: SpielerA ist gestorben - Herzen: 2/3
[INFO] [Reviva] WIEDERBELEBUNG: SpielerB hat SpielerA wiederbelebt - Wiederbelebungen: 1/2
[INFO] [Reviva] CRAFTING: SpielerC hat das Herz der Rückkehr gecraftet
```

## 🐛 Fehlerbehebung

### Häufige Probleme

**Plugin lädt nicht:**
- Java 17+ installiert?
- Paper/Spigot 1.21+ verwendet?
- JAR-Datei in `plugins/` Ordner?

**Permissions funktionieren nicht:**
- LuckPerms oder anderes Permission-Plugin installiert?
- Permissions korrekt gesetzt?
- Server neugestartet nach Permission-Änderungen?

**Items droppen nicht:**
- Wurden Mobs von Spielern getötet?
- Vollmond-Phase für Phantomfedern?
- Korrekte Mob-Typen?

### Debug-Befehle
```bash
# Cooldown-Status prüfen
/reviva cooldown

# Spieler-Status prüfen  
/reviva status <spieler>

# Plugin-Info
/reviva info
```

## 🤝 Unterstützung

### Kompatibilität
- **Minecraft:** 1.21+
- **Server:** Spigot, Paper, Purpur
- **Java:** 17+
- **Plugins:** LuckPerms, Vault (optional)

### Bekannte Kompatibilitäten
- ✅ LuckPerms
- ✅ Vault
- ✅ WorldGuard
- ✅ Essentials
- ✅ PlaceholderAPI (geplant)

## 📋 Entwicklung

### Projekt-Struktur
```
src/main/java/de/reviva/plugin/
├── Reviva.java                 # Haupt-Plugin Klasse
├── commands/                   # Command Handler
├── listeners/                  # Event Listener
├── managers/                   # Logik-Manager
└── gui/                        # GUI-Komponenten
```

### Kompilierung
```bash
# Maven verwenden
mvn clean compile package

# Ausgabe: target/Reviva-1.0.0.jar
```

## 📄 Lizenz

Dieses Plugin ist als Open Source Beispiel bereitgestellt. 
Verwendung und Modifikation auf eigene Verantwortung.

---

**Entwickelt für Minecraft Server-Communities**  
*Hergestellt mit ❤️ für spannende Survival-Erlebnisse*