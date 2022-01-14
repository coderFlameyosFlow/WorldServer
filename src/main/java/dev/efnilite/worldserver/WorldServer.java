package dev.efnilite.worldserver;

import dev.efnilite.worldserver.config.Configuration;
import dev.efnilite.worldserver.config.Option;
import dev.efnilite.worldserver.config.Verbose;
import dev.efnilite.worldserver.toggleable.GeneralHandler;
import dev.efnilite.worldserver.toggleable.WorldChatListener;
import dev.efnilite.worldserver.toggleable.WorldSwitchListener;
import dev.efnilite.worldserver.util.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldServer extends JavaPlugin {

    public static boolean IS_OUTDATED;
    private static WorldServer instance;
    private static Configuration configuration;
    private static VisibilityHandler visibilityHandler;

    @Override
    public void onEnable() {
        instance = this;

        Tasks.init(this);
        Tasks.time("startup");
        Verbose.init(this);
        configuration = new Configuration(this);
        Option.init();

        String version = Util.getVersion();
        switch (version.substring(0, 5)) {
            case "v1_18":
                Verbose.info("Registered under server version 1.17");
                Version.VERSION = Version.V1_18;
                break;
            case "v1_17":
                Verbose.info("Registered under server version 1.17");
                Version.VERSION = Version.V1_17;
                break;
            case "v1_16":
                Verbose.info("Registered under server version 1.16");
                Version.VERSION = Version.V1_16;
                break;
            case "v1_15":
                Verbose.info("Registered under server version 1.15");
                Version.VERSION = Version.V1_16;
                break;
            case "v1_14":
                Verbose.info("Registered under server version 1.14");
                Version.VERSION = Version.V1_16;
                break;
            case "v1_13":
                Verbose.info("Registered under server version 1.13");
                Version.VERSION = Version.V1_16;
                break;
            case "v1_12":
                Verbose.info("Registered under server version 1.12");
                Version.VERSION = Version.V1_12;
                break;
            case "v1_11":
                Verbose.info("Registered under server version 1.11");
                Version.VERSION = Version.V1_11;
                break;
            case "v1_10":
                Verbose.info("Registered under server version 1.10");
                Version.VERSION = Version.V1_10;
                break;
            case "v1_9_":
                Verbose.info("Registered under server version 1.9");
                Version.VERSION = Version.V1_9;
                break;
            case "v1_8_":
                Verbose.info("Registered under server version 1.8");
                Version.VERSION = Version.V1_8;
                break;
        }

        switch (version) {
            case "v1_18":
            case "v1_17":
            case "v1_16":
            case "v1_15":
            case "v1_14":
            case "v1_13":
                visibilityHandler = new VisibilityHandler_v1_13();
                break;
            case "v1_12":
            case "v1_11":
            case "v1_10":
            case "v1_9_":
            case "v1_8_":
                visibilityHandler = new VisibilityHandler_v1_8();
                break;
            default:
                Verbose.error("Unsupported version! Please upgrade your server :(");
                Bukkit.getPluginManager().disablePlugin(this);
        }

        Metrics metrics = new Metrics(this, 13856);
        metrics.addCustomChart(new SimplePie("chat_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));
        metrics.addCustomChart(new SimplePie("tab_enabled", () -> Boolean.toString(Option.CHAT_ENABLED)));

        SimpleCommand.register("worldserver",  new WorldServerCommand());


        Bukkit.getPluginManager().registerEvents(new GeneralHandler(), this);
        Bukkit.getPluginManager().registerEvents(new WorldChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldSwitchListener(), this);

        UpdateChecker checker = new UpdateChecker();
        Tasks.syncRepeat(checker::check, 8 * 72000); // 8 hours

        Verbose.info("Loaded WorldServer " + getDescription().getVersion() + " by Efnilite in " + Tasks.end("startup")  + "ms!");
    }

    public static VisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static WorldServer getInstance() {
        return instance;
    }
}