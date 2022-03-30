package dev.uten2c.strobo.mixin.serversideitem.packet;

import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ScreenHandlerSlotUpdateS2CPacket.class)
public class MixinScreenHandlerSlotUpdateS2CPacket implements UuidHolder {
    private UUID strobo$uuid;

    @Override
    public @Nullable UUID getUuid() {
        return strobo$uuid;
    }

    @Override
    public void setUuid(@Nullable UUID uuid) {
        strobo$uuid = uuid;
    }
}
