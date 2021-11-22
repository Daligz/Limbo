package net.royalmind.minecraft.limbo.packets;

import net.md_5.bungee.protocol.packet.KeepAlive;

public class KeepAlivePacket extends KeepAlive implements Packet {

    public KeepAlivePacket(final long randomId) {
        super(randomId);
    }

    @Override
    public void send() {

    }
}
