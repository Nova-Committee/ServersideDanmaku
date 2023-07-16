package committee.nova.ssdanmaku.event.handler;

import com.mojang.brigadier.arguments.LongArgumentType;
import committee.nova.ssdanmaku.ServersideDanmaku;
import committee.nova.ssdanmaku.cap.DanmakuCap;
import committee.nova.ssdanmaku.config.BilibiliConfig;
import committee.nova.ssdanmaku.config.ConfigManger;
import committee.nova.ssdanmaku.event.post.SendDanmakuEvent;
import committee.nova.ssdanmaku.utils.DanmakuManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

import java.util.concurrent.atomic.AtomicBoolean;

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
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        server.getPlayerList().getPlayers().stream().filter(p -> {
            final AtomicBoolean b = new AtomicBoolean(true);
            p.getCapability(DanmakuCap.DANMAKU).ifPresent(d -> b.set(d.isEnabled()));
            return b.get();
        }).forEach(p -> p.sendSystemMessage(Component.literal(event.getMessage())));
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
                .then(Commands.literal("toggle")
                        .executes(ctx -> {
                            final var src = ctx.getSource();
                            src.getPlayerOrException().getCapability(DanmakuCap.DANMAKU).ifPresent(d -> {
                                final boolean newStatus = !d.isEnabled();
                                d.setEnabled(newStatus);
                                src.sendSuccess(() -> Component.literal("弹幕显示已" + (newStatus ? "开启。" : "关闭。"))
                                        .withStyle(ChatFormatting.YELLOW), false);
                            });
                            return 1;
                        })
                        .requires(p -> true))
                .requires(p -> true));
    }

    @SubscribeEvent
    public static void onAddNode(PermissionGatherEvent.Nodes event) {
        event.addNodes(ServersideDanmaku.SSDANMAKU_ADMIN);
    }

    @SubscribeEvent
    public static void onAttachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(new ResourceLocation(ServersideDanmaku.MOD_ID, ServersideDanmaku.MOD_ID), new DanmakuCap.Provider());
    }
}
