package net.royalmind.minecraft.limbo.bridges;

import com.google.common.base.Objects;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.packet.Kick;

public class KeepConnectionBridge extends DownstreamBridge {

    private final Plugin plugin;
    private final ProxyServer proxyServer;
    private final UserConnection userConnection;
    private final ServerConnection serverConnection;

    private static final String KICK_ID = "KICK_LIMBO_ID";

    public KeepConnectionBridge(final Plugin plugin, final ProxyServer proxyServer, final UserConnection userConnection, final ServerConnection serverConnection) {
        super(proxyServer, userConnection, serverConnection);
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.userConnection = userConnection;
        this.serverConnection = serverConnection;
    }

    @Override
    public void exception(final Throwable throwable) {
        if (serverConnection.isObsolete()) return;
        serverConnection.setObsolete(true);
    }

    @Override
    public void handle(final Kick kick) {
        ServerInfo serverInfo = proxyServer.getServerInfo(userConnection.getPendingConnection().getListener().getFallbackServer());
        if (Objects.equal(serverConnection.getInfo(), serverInfo)) serverInfo = null;
        final ServerKickEvent event = proxyServer.getPluginManager().callEvent(new ServerKickEvent(userConnection, serverConnection.getInfo(), ComponentSerializer.parse(kick.getMessage()), serverInfo, ServerKickEvent.State.CONNECTED));
        if (event.isCancelled() && event.getCancelServer() != null) {
            userConnection.connectNow(event.getCancelServer());
        } else {
            final String kickMessage = ChatColor.stripColor(BaseComponent.toLegacyText(ComponentSerializer.parse(kick.getMessage())));
            final boolean doReconnect = kickMessage.equalsIgnoreCase(KICK_ID);
            if (!doReconnect) userConnection.disconnect0(event.getKickReasonComponent());
        }
        serverConnection.setObsolete(true);
        throw CancelSendSignal.INSTANCE;
    }
}