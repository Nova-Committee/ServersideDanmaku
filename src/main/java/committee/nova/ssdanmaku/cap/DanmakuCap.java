package committee.nova.ssdanmaku.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class DanmakuCap {
    public static final Capability<IDanmaku> DANMAKU = CapabilityManager.get(new CapabilityToken<>() {
    });

    public interface IDanmaku extends INBTSerializable<ByteTag> {
        boolean isEnabled();

        void setEnabled(boolean enabled);
    }

    public static class Impl implements IDanmaku {
        private boolean enabled = true;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public ByteTag serializeNBT() {
            return ByteTag.valueOf(enabled);
        }

        @Override
        public void deserializeNBT(ByteTag nbt) {
            this.enabled = nbt.getAsInt() == 1;
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<ByteTag> {
        private IDanmaku danmaku;

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == DANMAKU ? LazyOptional.of(this::getOrCreateCapability).cast() : LazyOptional.empty();
        }

        @Override
        public ByteTag serializeNBT() {
            return getOrCreateCapability().serializeNBT();
        }

        @Override
        public void deserializeNBT(ByteTag nbt) {
            getOrCreateCapability().deserializeNBT(nbt);
        }

        @Nonnull
        IDanmaku getOrCreateCapability() {
            if (danmaku == null) this.danmaku = new Impl();
            return this.danmaku;
        }
    }
}
