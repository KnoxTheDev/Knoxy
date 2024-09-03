package knoxy.knoxy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketConsumer;
import net.fabricmc.fabric.api.networking.v1.PacketContext;
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
        FabricClient.getInstance().getNetworkManager().registerReceiver(
            ServerboundMovePlayerPacket.class,
            new PacketConsumer<ServerboundMovePlayerPacket>() {
                @Override
                public void accept(PacketContext context, ServerboundMovePlayerPacket packet) {
                    if (packet.getY() < packet.getPlayer().getY()) {
                        packet.getPlayer().fallDistance = 0.0F;
                    }
                }
            }
        );

        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.START_FALL_FLYING));
            }
        });
    }
}

class NoKnockback {
    public static void init() {
        FabricClient.getInstance().getNetworkManager().registerReceiver(
            EntityVelocityUpdateS2CPacket.class,
            new PacketConsumer<EntityVelocityUpdateS2CPacket>() {
                @Override
                public void accept(PacketContext context, EntityVelocityUpdateS2CPacket packet) {
                    if (packet.getEntityId() == MinecraftClient.getInstance().player.getEntityId()) {
                        packet.setVelocity(0, 0, 0);
                    }
                }
            }
        );
    }
}