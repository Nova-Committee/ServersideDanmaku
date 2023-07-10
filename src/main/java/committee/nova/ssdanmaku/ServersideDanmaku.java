package committee.nova.ssdanmaku;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.ssdanmaku.websocket.WebSocketClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
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
    public static final PermissionNode<Boolean> SSDANMAKU_ADMIN = new PermissionNode<>(new ResourceLocation("ssdanmaku", "admin"), PermissionTypes.BOOLEAN,
            (p, u, c) -> p != null && p.hasPermissions(2));

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean checkSSDanmakuAdminPerm(CommandSourceStack s) {
        try {
            final ServerPlayer p = s.getPlayerOrException();
            return PermissionAPI.getPermission(p, ServersideDanmaku.SSDANMAKU_ADMIN);
        } catch (CommandSyntaxException e) {
            return s.hasPermission(2);
        }
    }
}
