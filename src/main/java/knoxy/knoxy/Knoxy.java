package knoxy.knoxy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Knoxy implements ModInitializer {
    public static final String MOD_ID = "knoxy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        NoFall.init();
        NoKnockback.init();
    }
}

class NoFall {
    public static void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.START_FALL_FLYING));
                }
            });
        });
    }
}

class NoKnockback {
    public static void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            client.execute(() -> {
                ClientPlayNetworking.registerGlobalReceiver(EntityVelocityUpdateS2CPacket.ID, (client1, packet) -> {
                    if (packet.getEntityId() == client1.player.getId()) {
                        // Cancel the knockback by sending a new packet with zero velocity
                        EntityVelocityUpdateS2CPacket newPacket = new EntityVelocityUpdateS2CPacket(packet.getEntityId(), 0, 0, 0, false);
                        client1.player.networkHandler.sendPacket(newPacket);
                    }
                });
            });
        });
    }
}