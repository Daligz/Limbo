package net.royalmind.minecraft.limbo;

import net.md_5.bungee.api.plugin.Plugin;
import net.royalmind.minecraft.limbo.packets.DisconnectPacket;

public final class Limbo extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getProxy().getPluginManager().registerListener(this, new DisconnectPacket(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
