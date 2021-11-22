package net.royalmind.minecraft.limbo.packets;

import lombok.AllArgsConstructor;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Title;
import net.royalmind.minecraft.limbo.bridges.KeepConnectionBridge;
import net.royalmind.minecraft.limbo.utils.Utils;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class DisconnectPacket implements Listener {

    private final Plugin plugin;

    @EventHandler
    public void onServerSwitch(final ServerSwitchEvent event) {
        final ProxyServer bungee = this.plugin.getProxy();
        final UserConnection userConnection = (UserConnection) event.getPlayer();
        final ServerConnection serverConnection = userConnection.getServer();
        final ChannelWrapper channelWrapper = serverConnection.getCh();

        final KeepConnectionBridge bridge = new KeepConnectionBridge(this.plugin, bungee, userConnection, serverConnection);
        channelWrapper.getHandle().pipeline().get(HandlerBoss.class).setHandler(bridge);
    }

    @EventHandler
    public void a(final ChatEvent event) {
        final Connection connection = event.getSender();
        final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) connection;
        proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("limbo"));
        this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
            final KeepAlive keepAlive = new KeepAlive();
            final Title title = new Title(Title.Action.TITLE);
            title.setText(Utils.toJSON(ChatColor.translateAlternateColorCodes('&', "&5-= &dLIMBO &5=-")));
            connection.unsafe().sendPacket(keepAlive);
            connection.unsafe().sendPacket(title);
        }, 0, 15, TimeUnit.SECONDS);
    }
}
