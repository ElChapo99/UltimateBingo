// Este proyecto está basado en Mega Bingo de Elmer Lion
// Puedes encontrar el proyecto original aquí https://github.com/ElmerLion/megabingo

// Distribuido bajo la Licencia Pública General GNU v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.*;
import io.shantek.tools.MaterialList;
import io.shantek.tools.BingoFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UltimateBingo extends JavaPlugin {
    public BingoManager bingoManager;
    private MaterialList materialList;
    public BingoFunctions bingoFunctions;
    public BingoGameGUIManager bingoGameGUIManager;
    public BingoPlayerGUIManager bingoPlayerGUIManager;
    public BingoPlayerGUIListener bingoPlayerGUIListener;
    public BingoCommand bingoCommand;
    public Location bingoSpawnLocation;
    public ConfigFile configFile;
    public int gameTime = 0;
    private YamlConfiguration gameConfig;
    public CardTypes cardTypes;
    public boolean consoleLogs = true;
    public boolean bingoCardActive = false;
    public boolean respawnTeleport = true;
    public boolean bingoStarted = false;
    public Material bingoCardMaterial = Material.COMPASS;
    public long gameStartTime;
    public boolean playedSinceReboot = false;
    public Metrics metrics;

    private SettingsManager settingsManager;
    public InGameConfigManager inGameConfigManager;

    // Añadir campo de Leaderboard
    private Leaderboard leaderboard;
    // Configuración guardada para preparar juegos
    public String fullCard = "full card";
    public String difficulty;
    public String cardSize;
    public String uniqueCard;
    public String gameMode = "traditional";
    public String revealCards = "enabled";
    public int loadoutType = 1;
    public String bingoWorld = "default";
    public boolean multiWorldServer = false;
    public boolean countSoloGames = false;
    public int shuffleIntervalMinutes = 5;

    // Configuración actual del juego - Implementado para permitir
    // asignación aleatoria de la configuración del juego
    public boolean currentFullCard = false;
    public String currentDifficulty;
    public String currentCardSize;
    public boolean currentUniqueCard;
    public String currentGameMode = "traditional";
    public boolean currentRevealCards = true;
    public int currentLoadoutType = 1;

    public boolean bingoButtonActive = true;

    // Seguimiento del modo Shuffle
    public int shuffleTaskId = -1;

    // Inventario usado para modo de juego en grupo
    public Inventory groupInventory;

    // Inventarios usados para modo por equipos
    public Inventory blueTeamInventory;
    public Inventory redTeamInventory;
    public Inventory yellowTeamInventory;


    // MUY importante que nunca se establezca como un ítem incluido en tus cartas de bingo
    // ¡ya que romperá la funcionalidad del juego!
    public Material tickedItemMaterial = Material.LIME_CONCRETE;

    public static UltimateBingo instance;

    @Override
    public void onEnable() {
        // Guardar la instancia del plugin
        instance = this;

        // Inicializar managers en el orden correcto
        settingsManager = new SettingsManager(this);

        // Inicializar BingoManager primero sin BingoCommand
        bingoManager = new BingoManager(this, null); // Temporalmente null para BingoCommand

        // Ahora inicializar BingoCommand y pasar la referencia real de bingoManager
        bingoCommand = new BingoCommand(this, settingsManager, bingoManager, inGameConfigManager);

        // Establecer la referencia de BingoCommand en BingoManager
        bingoManager.setBingoCommand(bingoCommand);

        // Continuar con otros managers
        materialList = new MaterialList(this);
        bingoGameGUIManager = new BingoGameGUIManager(this);
        bingoPlayerGUIManager = new BingoPlayerGUIManager(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);
        configFile = new ConfigFile(this);
        leaderboard = new Leaderboard(this);
        inGameConfigManager = new InGameConfigManager(this);

        // Registrar comandos
        getCommand("bingo").setExecutor(bingoCommand);
        getCommand("bingo").setTabCompleter(new BingoCompleter());

        // Comprobar si PlaceholderAPI está instalado y registrar placeholders
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BingoPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI detectado, registrando placeholders.");
        } else {
            getLogger().info("PlaceholderAPI no encontrado, omitiendo registro de placeholders.");
        }

        registerEventListeners();

        // Asegurar que existan los ajustes del juego
        configFile.checkforDataFolder();
        configFile.reloadConfigFile();

        // Registrar bStats
        int pluginId = 21982;
        Metrics metrics = new Metrics(this, pluginId);

        // Establecer los letreros con los valores correctos
        bingoFunctions.updateAllSigns();
    }

    private void registerEventListeners() {
        // Registrar cada listener con el plugin manager de Bukkit
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(this), this);
        SettingsManager settingsManager = new SettingsManager(this);
        Bukkit.getPluginManager().registerEvents(new SettingsListener(materialList, settingsManager, bingoGameGUIManager, this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerGUIListener(materialList, bingoPlayerGUIManager, this), this);
        Bukkit.getPluginManager().registerEvents(new BingoSignListener(this, inGameConfigManager), this);
    }

    public BingoManager getBingoManager() {
        return bingoManager;
    }

    public MaterialList getMaterialList(){
        return materialList;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public BingoFunctions getBingoFunctions(){
        return bingoFunctions;
    }

    @Override
    public void onDisable() {
        if (bingoManager != null) {
            bingoManager.clearData();
        }
        bingoStarted = false;
        instance = null;
    }

    public static UltimateBingo getInstance() {
        return instance;
    }

}
