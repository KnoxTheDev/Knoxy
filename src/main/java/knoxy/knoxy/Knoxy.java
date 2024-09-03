package knoxy.knoxy;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

public class Knoxy {

    @Environment(EnvType.CLIENT)
    public void onEnable() {
        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the client play network handler
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        // Create a new thread to send the packets
        new Thread(() -> {
            while (true) {
                // Check if the player is falling
                if (client.player.isFallFlying() || client.player.getVelocity().y < 0) {
                    // Send the "OnGround" packet to the server
                    Packet<?> packet = new PlayerMoveC2SPacket.OnGroundPacket(true);
                    networkHandler.sendPacket(packet);
                }

                // Sleep for a short period of time to avoid spamming the server
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}