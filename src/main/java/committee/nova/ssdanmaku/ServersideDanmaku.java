package committee.nova.ssdanmaku;

import committee.nova.ssdanmaku.websocket.WebSocketClient;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledFuture;

@Mod(ServersideDanmaku.MOD_ID)
public class ServersideDanmaku {
    public ServersideDanmaku() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public static final String MOD_ID = "ssdanmaku";
    public static final Logger LOGGER = LogManager.getLogger();
    public static ScheduledFuture<?> HEART_BEAT_TASK = null;
    public static WebSocketClient WEBSOCKET_CLIENT = null;

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
