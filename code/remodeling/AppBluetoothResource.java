package com.server.smartpower;

import android.content.Context;
import android.os.Looper;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;

import java.util.ArrayList;

class AppBluetoothResource extends AppPowerResource {
    /**
     * key: pid
     * value: BleRecord
     **/
    private final SparseArray<BleRecord> mActivePidsMap = new SparseArray<>();

    public AppBluetoothResource(Context context, Looper looper) {
        mType = AppPowerResourceManager.RES_TYPE_BLE;
    }

    private BleRecord getBleRecord(int pid) {
        synchronized (mActivePidsMap) {
            return mActivePidsMap.get(pid);
        }
    }

    private BleRecord getOrCreateBleRecord(int uid, int pid) {
        synchronized (mActivePidsMap) {
            BleRecord record = mActivePidsMap.get(pid);
            if (record == null) {
                record = new BleRecord(uid, pid);
                mActivePidsMap.put(pid, record);
            }
            return record;
        }
    }

    @Override
    public ArrayList<Integer> getActiveUids() {
        return null;
    }

    @Override
    public boolean isAppResourceActive(int uid) {
        boolean active = false;
        synchronized (mActivePidsMap) {
            for (int i = 0; i < mActivePidsMap.size(); i++) {
                BleRecord record = mActivePidsMap.valueAt(i);
                if (record != null && record.mOwnerUid == uid && record.isActive()) {
                    return true;
                }
            }
        }
        return active;
    }

    @Override
    public boolean isAppResourceActive(int uid, int pid) {
        BleRecord record = getBleRecord(pid);
        if (record != null) {
            return record.isActive();
        }
        return false;
    }

    @Override
    public void releaseAppPowerResource(int uid) {
        // TODO
    }

    @Override
    public void resumeAppPowerResource(int uid) {
        // TODO
    }

    @Override
    public void registerCallback(IAppPowerResourceCallback callback, int uid) {
        super.registerCallback(callback, uid);
    }

    @Override
    public void unRegisterCallback(IAppPowerResourceCallback callback, int uid) {
        super.unRegisterCallback(callback, uid);
    }

    @Override
    public void registerCallback(IAppPowerResourceCallback callback, int uid, int pid) {
        super.registerCallback(callback, uid, pid);
    }

    @Override
    public void unRegisterCallback(IAppPowerResourceCallback callback, int uid, int pid) {
        synchronized (mActivePidsMap) {
            if (mActivePidsMap.get(pid) != null) {
                mActivePidsMap.remove(pid);
            }
        }
        super.unRegisterCallback(callback, uid, pid);
    }

    public void onBluetoothEvent(boolean isConnect, int bleType, int uid, int pid, int flag) {
        if (DEBUG) {
            Slog.d(TAG, "bluttooth: connect=" + isConnect
                    + " bleType=" + bleType
                    + " uid=" + uid
                    + " pid=" + pid
                    + " bleType=" + bleType
                    + " flag=" + flag);
        }
        if (isConnect) {
            BleRecord record = getOrCreateBleRecord(uid, pid);
            record.onBluetoothConnect(bleType, flag);
        } else {
            BleRecord record = getBleRecord(pid);
            if (record != null) {
                record.onBluetoothDisconnect(bleType, flag);
                if (!record.isActive()) {
                    synchronized(mActivePidsMap) {
                        mActivePidsMap.remove(pid);
                    }
                }
            }
        }
    }

    private class BleRecord {
        int mOwnerUid;
        int mOwnerPid;

        /*
         * key:bleType
         */
        private final SparseArray<ProfileRecord> mProfileRecords = new SparseArray<>();

        BleRecord(int uid, int pid) {
            mOwnerUid = uid;
            mOwnerPid = pid;
        }

        void onBluetoothConnect(int bleType, int flag) {
            ProfileRecord profileRecord;
            synchronized (mProfileRecords) {
                profileRecord = mProfileRecords.get(bleType);
                if (profileRecord == null) {
                    profileRecord = new ProfileRecord(bleType);
                    mProfileRecords.put(bleType, profileRecord);
                }
                EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                        "bluetooth u:" + mOwnerUid + " p:" + mOwnerPid + " s:true");
                reportResourceStatus(mOwnerUid, mOwnerPid, true, 0);
            }
            profileRecord.bluetoothConnect(flag);
        }

        void onBluetoothDisconnect(int bleType, int flag) {
            synchronized (mProfileRecords) {
                ProfileRecord profileRecord = mProfileRecords.get(bleType);
                if (profileRecord != null) {
                    profileRecord.bluetoothDisconnect(flag);
                    if (!profileRecord.isActive()) {
                        mProfileRecords.remove(bleType);
                        EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                                "bluetooth u:" + mOwnerUid + " p:" + mOwnerPid + " s:false");
                        reportResourceStatus(mOwnerUid, mOwnerPid, false, 0);
                    }
                }
            }
        }

        boolean isActive() {
            return mProfileRecords.size() > 0;
        }

        class ProfileRecord {
            int mBleType;

            private final ArraySet<Integer> mFlags = new ArraySet<>();

            ProfileRecord(int bleType) {
                mBleType = bleType;
            }

            void bluetoothConnect(int flag) {
                mFlags.add(flag);
            }

            void bluetoothDisconnect(int flag) {
                mFlags.remove(flag);
            }

            boolean isActive() {
                return mFlags.size() > 0;
            }
        }
    }
}
