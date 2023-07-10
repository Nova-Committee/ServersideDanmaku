package committee.nova.ssdanmaku.utils;

import committee.nova.ssdanmaku.ServersideDanmaku;
import committee.nova.ssdanmaku.site.bilibili.BilibiliSite;
import committee.nova.ssdanmaku.websocket.WebSocketClient;

import static committee.nova.ssdanmaku.config.ConfigManger.getBilibiliConfig;

public class DanmakuManager {
    public static void start() {
        BilibiliSite site = new BilibiliSite(getBilibiliConfig());
        if (site.getConfig().getRoom().isEnable()) {
            ServersideDanmaku.WEBSOCKET_CLIENT = new WebSocketClient(site);
            try {
                ServersideDanmaku.WEBSOCKET_CLIENT.open();
            } catch (Exception e) {
                ServersideDanmaku.WEBSOCKET_CLIENT = null;
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        if (ServersideDanmaku.WEBSOCKET_CLIENT == null) {
            return;
        }
        try {
            ServersideDanmaku.WEBSOCKET_CLIENT.close();
        } catch (Exception ignore) {
        } finally {
            ServersideDanmaku.WEBSOCKET_CLIENT = null;
        }
    }
}
