package galaxyspace.core.mixins;

import java.util.List;

import asmodeuscore.core.network.packet.ACPacketSimple;
import galaxyspace.GalaxySpace;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ACPacketSimple.class, remap = false)
public abstract class MixinACPacketSimple {

    @Shadow
    private ACPacketSimple.ACEnumSimplePacket type;
    @Shadow
    private List<Object> data;

    @Inject(method = "handleServerSide", at = @At("HEAD"), cancellable = true, remap = false)
    private void galaxyspace$guardTeleport(EntityPlayer player, CallbackInfo ci) {
        if (this.type != ACPacketSimple.ACEnumSimplePacket.S_TELEPORT_ENTITY) return;
        if (player == null || player.world == null || player.world.isRemote) return;
        if (player.capabilities.isCreativeMode) return;

        Object raw = (this.data != null && !this.data.isEmpty()) ? this.data.get(0) : null;
        if (!(raw instanceof Integer)) {
            galaxyspace$reject(player, "malformed teleport packet", ci);
            return;
        }
        int dimID = (Integer) raw;

        if (!DimensionManager.isDimensionRegistered(dimID)) {
            galaxyspace$reject(player, "unregistered destination dim " + dimID, ci);
            return;
        }

        CelestialBody body = galaxyspace$bodyForDim(dimID);
        if (body != null) {
            if (!body.getReachable()) {
                galaxyspace$reject(player, "unreachable body " + body.getName(), ci);
                return;
            }
            int need = body.getTierRequirement();
            int have = galaxyspace$rocketTier(player);
            if (have >= 0 && need > 0 && have < need) {
                galaxyspace$reject(player, "rocket tier " + have + " < required " + need
                        + " for " + body.getName(), ci);
                return;
            }
        }
    }

    @Unique
    private void galaxyspace$reject(EntityPlayer player, String why, CallbackInfo ci) {
        GalaxySpace.info("[GS-AntiExploit] Blocked rocket teleport from "
                + player.getName() + ": " + why);
        ci.cancel();
    }

    @Unique
    private CelestialBody galaxyspace$bodyForDim(int dimID) {
        for (Planet p : GalaxyRegistry.getRegisteredPlanets().values()) {
            if (p.getDimensionID() == dimID) return p;
        }
        for (Moon m : GalaxyRegistry.getRegisteredMoons().values()) {
            if (m.getDimensionID() == dimID) return m;
        }
        return null;
    }

    @Unique
    private int galaxyspace$rocketTier(EntityPlayer player) {
        Entity ridden = player.getRidingEntity();
        if (ridden == null) return -1;
        try {
            Object v = ridden.getClass().getMethod("getRocketTier").invoke(ridden);
            if (v instanceof Integer) return (Integer) v;
        } catch (Throwable ignored) {
        }
        return -1;
    }
}