package com.mateus.onlineuuidfix.mixin;

import com.mateus.onlineuuidfix.MojangApiHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerLoginPacketListenerImpl.class)
public class PlayerManagerMixin {

    @ModifyVariable(method = "startClientVerification", at = @At("HEAD"), argsOnly = true)
    private GameProfile injectOfflineUuidFix(GameProfile profile) {
        // Skip if the profile already has a proper online-mode UUID.
        // Online-verified profiles come from Yggdrasil and already have the real UUID.
        // Offline profiles are created by UUIDUtil.createOfflineProfile and have a
        // name-derived UUID (version 3). We replace those with the real Mojang UUID.
        UUID onlineUuid = MojangApiHelper.fetchOnlineUuid(profile.name());
        if (onlineUuid != null && !onlineUuid.equals(profile.id())) {
            return new GameProfile(onlineUuid, profile.name());
        }
        return profile;
    }
}