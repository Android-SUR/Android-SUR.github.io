package com.server.smartpower;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.text.format.DateUtils;
import android.util.EventLog;
import android.util.Slog;
import com.android.server.NetworkManagementService;

import java.util.ArrayList;
import java.util.HashMap;

class AppNetworkResource extends AppPowerResource {
    private static final int NET_KB = 1024;
    private static final int NET_DOWNLOAD_SCENE_THRESHOLD = 4;
    private Handler mHandler;
    private NetworkStatsManager mNetworkStatsManager;

    private static final int MSG_NETWORK_CHECK = 1;

    /**
     * network speed monitor by uid
     */
    private final HashMap<Integer, NetworkMonitor> mNetworkMonitorMap = new HashMap<>();

    AppNetworkResource(Context context, Looper looper) {
        mHandler = new MyHandler(looper);
        mType = AppPowerResourceManager.RES_TYPE_NET;
    }

    @Override
    public ArrayList getActiveUids() {
        return null;
    }

    @Override
    public boolean isAppResourceActive(int uid) {
        return false;
    }

    @Override
    public boolean isAppResourceActive(int uid, int pid) {
        return false;
    }

    @Override
    public void releaseAppPowerResource(int uid) {
        String event = "network u:" + uid + " s:release";
        Trace.traceBegin(Trace.TRACE_TAG_POWER, event);
        updateNetworkRule(uid, true);
        EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS, event);
        Trace.traceEnd(Trace.TRACE_TAG_POWER);
    }

    @Override
    public void resumeAppPowerResource(int uid) {
        String event = "network u:" + uid + " s:resume";
        updateNetworkRule(uid, false);
        EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS, event);
        Trace.traceEnd(Trace.TRACE_TAG_POWER);
    }

    private void updateNetworkRule(int uid, boolean allow) {
        try {
            NetworkManagementService.getInstance().updateAurogonUidRule(uid, allow);
            if (DEBUG) Slog.d(TAG, "setFirewall received: " + uid);
        } catch (Exception e) {
            Slog.i(TAG, "setFirewall failed " + uid);
        }
    }

    @Override
    public void registerCallback(IAppPowerResourceCallback callback, int uid) {
        super.registerCallback(callback, uid);
        synchronized (mNetworkMonitorMap) {
            if (mNetworkMonitorMap.get(uid) == null) {
                mNetworkMonitorMap.put(uid, new NetworkMonitor(uid));
            }
        }
    }

    @Override
    public void unRegisterCallback(IAppPowerResourceCallback callback, int uid) {
        super.unRegisterCallback(callback, uid);
        synchronized (mNetworkMonitorMap) {
            if (mResourceCallbacksByUid.get(uid) == null) {
                mNetworkMonitorMap.remove(uid);
            }
        }
    }

    class NetworkMonitor {
        int mUid;
        long mlastTotalKiloBytes;
        long mLastTimeStamp;
        int mActiveSeconds = 0;
        int mInactiveSeconds = 0;
        boolean mIsActive = false;

        NetworkMonitor(int uid) {
            mUid = uid;
            mLastTimeStamp = System.currentTimeMillis();
            mlastTotalKiloBytes = getUidTxBytes() / NET_KB;
            Message nextMsg = mHandler.obtainMessage(MSG_NETWORK_CHECK, this);
            mHandler.sendMessageDelayed(nextMsg, SmartPowerSettings.DEF_RES_NET_MONITOR_PERIOD);
        }

        private void updateNetworkStatus() {
            long currentTotalKiloBytes = getUidTxBytes() / NET_KB;
            long now = System.currentTimeMillis();
            long duration = now - mLastTimeStamp;
            if (duration <= 0) return;
            long speed = (currentTotalKiloBytes - mlastTotalKiloBytes) *
                    DateUtils.SECOND_IN_MILLIS / duration;
            if (DEBUG) Slog.d(TAG, mUid + " speed " + speed + "kb/s");
            if (speed > SmartPowerSettings.DEF_RES_NET_ACTIVE_SPEED) {
                mActiveSeconds++;
                mInactiveSeconds = 0;
                if (mActiveSeconds++ > NET_DOWNLOAD_SCENE_THRESHOLD) {
                    mIsActive = true;
                    reportResourceStatus(mUid, true,
                            AppPowerResourceManager.RESOURCE_BEHAVIOR_NETWORK);
                }
            } else {
                mInactiveSeconds++;
                mActiveSeconds = 0;
                if (mInactiveSeconds++ > NET_DOWNLOAD_SCENE_THRESHOLD) {
                    mIsActive = false;
                    reportResourceStatus(mUid, false,
                            AppPowerResourceManager.RESOURCE_BEHAVIOR_NETWORK);
                }
            }
            mlastTotalKiloBytes = currentTotalKiloBytes;
            mLastTimeStamp = now;
        }

        private long getUidTxBytes() {
            return TrafficStats.getUidTxBytes(mUid) + TrafficStats.getUidRxBytes(mUid);
        }
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_NETWORK_CHECK) {
                NetworkMonitor monitor = (NetworkMonitor) msg.obj;
                synchronized (mNetworkMonitorMap) {
                    if (!mNetworkMonitorMap.containsValue(monitor)) {
                        return;
                    }
                    monitor.updateNetworkStatus();
                    Message message = mHandler.obtainMessage(MSG_NETWORK_CHECK, monitor);
                    mHandler.sendMessageDelayed(message, DateUtils.SECOND_IN_MILLIS);
                }
            }
        }
    }
}