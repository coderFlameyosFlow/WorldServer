package dev.efnilite.worldserver;

import dev.efnilite.vilib.ViPlugin;

import dev.efnilite.vilib.lib.bstats.bukkit.Metrics;
import dev.efnilite.vilib.lib.bstats.charts.SimplePie;
import dev.efnilite.vilib.util.Logging;
import dev.efnilite.vilib.util.Task;
import dev.efnilite.vilib.util.Time;
import dev.efnilite.vilib.util.Version;
import dev.efnilite.vilib.util.elevator.GitElevator;
import dev.efnilite.vilib.util.elevator.VersionComparator;
import dev.efnilite.worldserver.util.VisibilityHandler;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_13;
import dev.efnilite.worldserver.util.VisibilityHandler_v1_8;
import static dev.efnilite.worldserver.util.SemiPlugin.*;
import dev.efnilite.worldserver.config.ConfigValue;
import dev.efnilite.worldserver.config.Configuration;
import dev.efnilite.worldserver.eco.*;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import dev.efnilite.worldserver.hook.VaultHook;
import dev.efnilite.worldserver.toggleable.GeneralHandler;
import dev.efnilite.worldserver.toggleable.WorldChatListener;
import dev.efnilite.worldserver.toggleable.WorldEconomyListener;
import dev.efnilite.worldserver.toggleable.WorldTabListener;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WorldServer extends ViPlugin {

    public static final String NAME = "<#3D626F>WorldServer";
    public static final String MESSAGE_PREFIX = NAME + " <#7B7B7B>» <gray>";
    public static final String REQUIRED_VILIB_VERSION = "v1.1.0";

    private static WorldServer instance;
    private static Configuration configuration;
    private static VisibilityHandler visibilityHandler;

    @Override
    public void onLoad() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        if (config.getBoolean("economy-enabled") && getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
                getServer().getServicesManager().register(Economy.class, new EconomyProvider(), this, ServicePriority.High);
                getLogger().info("Hooked into vault and registered vault successfully!");
            } catch (NoClassDefFoundError | ClassNotFoundException ignored) {

            }
        }
    }

    @Override
    public void enable() {
        instance = this;

        // ----- Check vilib -----

        if (!checkVilibExisting(getServer()) || !checkVilibVersion(getServer())) {
            getServer().getPluginManager().disablePlugin(this);
        }

        // ----- Start time -----

        Time.timerStart("enableWS");

        // ----- Configurations -----

        configuration = new Configuration(this);
        ConfigValue.init();

        logging.info("Registered under version " + Version.getPrettyVersion());

        switch (version) {
            case V1_19:
            case V1_18:
            case V1_17:
            case V1_16:
            case V1_15:
            case V1_14:
            case V1_13:
                visibilityHandler = new VisibilityHandler_v1_13();
                break;
            case V1_12:
            case V1_11:
            case V1_10:
            case V1_9:
            case V1_8:
                visibilityHandler = new VisibilityHandler_v1_8();
                break;
            default:
                logging.error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(this);
        }

        // Register all commands and listeners.
        Registrator.registerAllCommands(this);
        Registrator.registerAllListeners(this);

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(ConfigValue.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(ConfigValue.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("eco_enabled", () -> Boolean.toString(ConfigValue.ECONOMY_ENABLED)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            WorldPlayer.register(player);
        }

        Task.create(this) // save data every 5 minutes
            .delay(5 * 60 * 20)
            .repeat(5 * 60 * 20)
            .execute(() -> {
                for (WorldPlayer player : WorldPlayer.getPlayers().values()) {
                    player.save(true);
                }
            }).run();

        Task.create(this) // read existing balance caches
            .async()
            .execute(BalCache::read)
            .run();

        // Vault setups
        VaultHook.register();
        PlaceholderHook.register();

        logging.info("Loaded WorldServer v" + getDescription().getVersion() + " in " + Time.timerEnd("enableWS")  + "ms!");
    }

    @Override
    public void disable() {
        for (WorldPlayer wp : WorldPlayer.getPlayers().values()) {
            WorldPlayer.unregister(wp.getPlayer(), false);
        }
    }

    @Override
    @NotNull
    public GitElevator getElevator() {
        return new GitElevator("Efnilite/WorldServer", this, VersionComparator.EQUAL, ConfigValue.AUTO_UPDATER);
    }

    @NotNull
    public GitElevator getElevatorInstance() {
        if (elevator == null) {
            elevator = getElevator();
        }

        return elevator;
    }

    /**
     * Returns the {@link Logging} belonging to this plugin.
     *
     * @return this plugin's {@link Logging} instance.
     */
    public static Logging logging() {
        return getPlugin().logging;
    }

    public static VisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static WorldServer getPlugin() {
        return instance;
    }
}
