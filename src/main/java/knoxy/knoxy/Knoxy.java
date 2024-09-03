package knoxy.knoxy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.OnGroundOnly;
import net.minecraft.util.Identifier;

public class Knoxy implements ClientModInitializer, ModInitializer {
    private static final Identifier PACKET_ID = new Identifier("knoxy", "fall_damage_knockback");

    @Override
    public void onInitializeClient() {
        // Client-side: Disable fall damage using simple methods
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.fallDistance > 0) {
                player.fallDistance = 0;
                // Send packet to server to update onGround status
                player.networkHandler.sendPacket(new OnGroundOnly(true));
            }
        });

        // Client-side: Listen for packets to set onGround status
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, (client, handler, buf, responseSender) -> {
            if (client.player != null) {
                client.execute(() -> {
                    client.player.setOnGround(buf.readBoolean());
                });
            }
        });
    }

    @Override
    public void onInitialize() {
        // Server-side: Listen for packets to set onGround status
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (server, player, handler, buf, responseSender) -> {
            boolean onGround = buf.readBoolean();
            server.execute(() -> {
                player.setOnGround(onGround);
            });
        });
    }
}
