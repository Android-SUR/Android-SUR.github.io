package com.server.smartpower;

import android.content.Context;
import android.location.ILocationListener;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Slog;
import android.util.SparseArray;

class AppGPSResource extends AppPowerResource{
    private final ArrayMap<Integer, GpsRecord> mGpsRecordMap = new ArrayMap<>();

    public AppGPSResource(Context context, Looper looper) {
        mType = AppPowerResourceManager.RES_TYPE_GPS;
    }

    @Override
    public ArrayList getActiveUids() {
        synchronized (mGpsRecordMap) {
            return new ArrayList<>(mGpsRecordMap.keySet());
        }
    }

    @Override
    public boolean isAppResourceActive(int uid) {
        synchronized (mGpsRecordMap) {
            for (int i = 0; i < mGpsRecordMap.size(); i++) {
                GpsRecord record = mGpsRecordMap.valueAt(i);
                if (record != null && record.mUid == uid && record.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAppResourceActive(int uid, int pid) {
        synchronized (mGpsRecordMap) {
            GpsRecord record = mGpsRecordMap.get(pid);
            if (record != null) {
                return record.isActive();
            }
        }
        return false;
    }

    @Override
    public void releaseAppPowerResource(int pid) {}

    @Override
    public void resumeAppPowerResource(int pid) {}

    public void onAquireLocation(int uid, int pid, ILocationListener listener) {
        if (listener == null) return;
        synchronized (mGpsRecordMap) {
            GpsRecord gpsRecord = mGpsRecordMap.get(pid);
            if (gpsRecord == null) {
                gpsRecord = new GpsRecord(uid, pid);
                mGpsRecordMap.put(pid, gpsRecord);
                EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                        "gps u:" + uid + " p:" + pid + " s:true");
                reportResourceStatus(uid, pid, true,
                        AppPowerResourceManager.RESOURCE_BEHAVIOR_GPS);
            }
            gpsRecord.onAquireLocation(listener);
        }
    }

    public void onReleaseLocation(int uid, int pid, ILocationListener listener) {
        if (listener == null) return;
        synchronized (mGpsRecordMap) {
            GpsRecord gpsRecord = mGpsRecordMap.get(pid);
            if (gpsRecord == null) return;
            gpsRecord.onReleaseLocation(listener);
            if (!gpsRecord.isActive()) {
                mGpsRecordMap.remove(pid);
                EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                        "gps u:" + uid + " p:" + pid + " s:false");
                reportResourceStatus(uid, pid, false,
                        AppPowerResourceManager.RESOURCE_BEHAVIOR_GPS);
            }
        }
    }

    class GpsRecord {
        private final int mPid;
        private final int mUid;

        /**
         * location listeners
         */
        private ArraySet<ILocationListener> mLocationListeners = new ArraySet<>();

        GpsRecord(int uid, int pid) {
            mUid = uid;
            mPid = pid;
        }

        boolean isActive() {
            return mLocationListeners.size() > 0;
        }

        int getPid() {
            return mPid;
        }

        int getUid() {
            return mUid;
        }

        void onAquireLocation(ILocationListener listener) {
            for (int i = 0; i < mLocationListeners.size(); i++) {
                ILocationListener item = mLocationListeners.valueAt(i);
                if (item.asBinder().equals(listener.asBinder())) {
                    return;
                }
            }
            mLocationListeners.add(listener);
        }

        void onReleaseLocation(ILocationListener listener) {
            for (int i = 0; i < mLocationListeners.size(); i++) {
                ILocationListener item = mLocationListeners.valueAt(i);
                if (item.asBinder().equals(listener.asBinder())) {
                    mLocationListeners.remove(item);
                }
            }
        }
    }
}

