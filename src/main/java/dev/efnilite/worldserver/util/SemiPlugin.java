package dev.efnilite.worldserver.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.Server;

public class SemiPlugin {
    public static void checkVilibExisting(Server server) {
        Plugin vilib = server.getPluginManager().getPlugin("vilib");
        if (vilib == null || !vilib.isEnabled()) {
            getLogger().severe("##");
            getLogger().severe("## WorldServer requires vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            server.getPluginManager().disablePlugin(this);
            return;
        }
    }
    
    public static void checkVilibVersion(Server server) {
        if (!Util.isLatest(REQUIRED_VILIB_VERSION, vilib.getDescription().getVersion())) {
            getLogger().severe("##");
            getLogger().severe("## WorldServer requires *a newer version* of vilib to work!");
            getLogger().severe("##");
            getLogger().severe("## Please download it here:");
            getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            getLogger().severe("##");

            server.getPluginManager().disablePlugin(this);
            return;
        }
    }
}
