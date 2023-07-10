package committee.nova.ssdanmaku.event.handler;

import com.mojang.brigadier.arguments.LongArgumentType;
import committee.nova.ssdanmaku.ServersideDanmaku;
import committee.nova.ssdanmaku.config.BilibiliConfig;
import committee.nova.ssdanmaku.config.ConfigManger;
import committee.nova.ssdanmaku.event.post.SendDanmakuEvent;
import committee.nova.ssdanmaku.utils.DanmakuManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

@Mod.EventBusSubscriber
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        DanmakuManager.start();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DanmakuManager.stop();
    }

    @SubscribeEvent
    public static void onDanmakuReceived(SendDanmakuEvent event) {
        final String msg = event.getMessage();
        if (msg.replace(" ", "").replace("\n", "").replace("\r", "").isEmpty()) return;
        final MinecraftServer server = ServersideDanmaku.getServer();
        if (server == null) return;
        server.getPlayerList().broadcastSystemMessage(Component.literal(event.getMessage()), false);
    }

    @SubscribeEvent
    public static void onRegisterCmd(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ssdanmaku")
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            DanmakuManager.stop();
                            ctx.getSource().sendSuccess(() -> Component.literal("弹幕配置重载中..."), false);
                            DanmakuManager.start();
                            return 1;
                        })
                        .requires(ServersideDanmaku::checkSSDanmakuAdminPerm))
                .then(Commands.literal("setroom")
                        .then(Commands.argument("room_id", LongArgumentType.longArg())
                                .executes(ctx -> {
                                    final long id = LongArgumentType.getLong(ctx, "room_id");
                                    final BilibiliConfig cfg = ConfigManger.getBilibiliConfig();
                                    cfg.getRoom().setId(id);
                                    cfg.getRoom().setEnable(true);
                                    ConfigManger.saveBilibiliConfig(cfg);
                                    ctx.getSource().sendSuccess(() -> Component.literal("房间号被设置为：" + id), false);
                                    return 1;
                                })
                                .requires(ServersideDanmaku::checkSSDanmakuAdminPerm))
                        .requires(ServersideDanmaku::checkSSDanmakuAdminPerm))
                .requires(ServersideDanmaku::checkSSDanmakuAdminPerm));
    }

    @SubscribeEvent
    public static void onAddNode(PermissionGatherEvent.Nodes event) {
        event.addNodes(ServersideDanmaku.SSDANMAKU_ADMIN);
    }
}
