package dev.efnilite.worldserver.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.Server;

public class SemiPlugin {
    public static boolean checkVilibExisting(Server server) {
        Plugin vilib = server.getPluginManager().getPlugin("vilib");
        if (vilib == null || !vilib.isEnabled()) {
            server.getLogger().severe("##");
            server.getLogger().severe("## WorldServer requires vilib to work!");
            server.getLogger().severe("##");
            server.getLogger().severe("## Please download it here:");
            server.getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            server.getLogger().severe("##");

            return false;
        }
        return true;
    }
    
    public static boolean checkVilibVersion(Server server) {
        if (!Util.isLatest(REQUIRED_VILIB_VERSION, vilib.getDescription().getVersion())) {
            server.getLogger().severe("##");
            server.getLogger().severe("## WorldServer requires *a newer version* of vilib to work!");
            server.getLogger().severe("##");
            server.getLogger().severe("## Please download it here:");
            server.getLogger().severe("## https://github.com/Efnilite/vilib/releases/latest");
            server.getLogger().severe("##");

            return false;
        }
        return true;
    }
}
