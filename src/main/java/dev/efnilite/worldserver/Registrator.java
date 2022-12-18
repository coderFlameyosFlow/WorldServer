package dev.efnilite.worldserver.commands;

import dev.efnilite.worldserver.util.Util;
import dev.efnilite.worldserver.config.ConfigValue;

public class Registrator {
    public void registerAllCommands(WorldServer worldServer) {
        worldServer.registerCommand("worldserver", new WorldServerCommand());
        if (ConfigValue.ECONOMY_OVERRIDE_BALANCE_COMMAND) {
            Util.registerToMap("bal", new BalCommand());
            Util.registerToMap("balance", new BalCommand());
        }
        if (ConfigValue.ECONOMY_OVERRIDE_PAY_COMMAND) {
            Util.registerToMap("pay", new PayCommand());
            Util.registerToMap("transfer", new PayCommand());
        }
        if (ConfigValue.ECONOMY_OVERRIDE_BALTOP_COMMAND) {
            Util.registerToMap("baltop", new BaltopCommand());
            Util.registerToMap("balancetop", new BaltopCommand());
        }
    }
    
    public void registerAllListeners(WorldServer worldServer) {
        worldServer.registerListener(new GeneralHandler());
        worldServer.registerListener(new WorldChatListener());
        worldServer.registerListener(new WorldTabListener());
        worldServer.registerListener(new WorldEconomyListener());
    }
}
