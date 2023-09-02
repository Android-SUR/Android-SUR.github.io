package com.server.smartpower;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioSystem;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;

import java.util.ArrayList;

class AppAudioResource extends AppPowerResource {
    private final SparseArray<AudioRecord> mActivePidsMap = new SparseArray<>();
    public static final int PLAYER_STATE_ZERO_PLAYER = 100;
    private AudioManager mAudioManager;
    private Handler mHandler;

    private long mLastMusicPlayTimeStamp;
    private long mLastMusicPlayPid;

    private static final long AUDIO_INACTIVE_DELAY_TIME = 2 * DateUtils.SECOND_IN_MILLIS;

    public AppAudioResource(Context context, Looper looper) {
        mType = AppPowerResourceManager.RES_TYPE_AUDIO;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler(looper);
    }

    private AudioRecord getAudioRecord(int pid) {
        synchronized (mActivePidsMap) {
            return mActivePidsMap.get(pid);
        }
    }

    private AudioRecord getAudioRecord(int uid, String clientId) {
        synchronized (mActivePidsMap) {
            for (int i = 0; i < mActivePidsMap.size(); i++) {
                AudioRecord record = mActivePidsMap.valueAt(i);
                if (record.mOwnerUid == uid && record.containsClientId(clientId)) {
                    return record;
                }
            }
        }
        return null;
    }

    private AudioRecord getAudioRecord(int uid, int riid) {
        synchronized (mActivePidsMap) {
            for (int i = 0; i < mActivePidsMap.size(); i++) {
                AudioRecord record = mActivePidsMap.valueAt(i);
                if (record.mOwnerUid == uid && record.containsRecorder(riid)) {
                    return record;
                }
            }
        }
        return null;
    }

    private AudioRecord getOrCreateAudioRecord(int uid, int pid) {
        synchronized (mActivePidsMap) {
            AudioRecord record = getAudioRecord(pid);
            if (record == null) {
                record = new AudioRecord(uid, pid);
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
                AudioRecord record = mActivePidsMap.valueAt(i);
                if (record != null && record.mOwnerUid == uid && record.mActive) {
                    return true;
                }
            }
        }
        return active;
    }

    @Override
    public boolean isAppResourceActive(int uid, int pid) {
        AudioRecord record = getAudioRecord(pid);
        if (record != null) {
            return record.mActive;
        }
        return false;
    }

    public long getLastMusicPlayTimeStamp(int pid) {
        if (mLastMusicPlayPid == pid) {
            return mLastMusicPlayTimeStamp;
        }
        return 0;
    }

    @Override
    public void releaseAppPowerResource(int uid) {
        synchronized (mActivePidsMap) {
            for (int i = 0; i < mActivePidsMap.size(); i++) {
                AudioRecord record = mActivePidsMap.valueAt(i);
                if (record != null && record.mOwnerUid == uid && !record.mActive
                        && record.isZeroAudioRecord()) {
                    EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                      "power zero audio " + record.mOwnerUid + " " + record.mOwnerPid);
                    /*EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                   "power release zero audio " + record.mOwnerUid + " " + record.mOwnerPid);
                    AudioSystem.pauseAudioTracks(record.mOwnerUid, record.mOwnerPid);*/
                }
            }
        }
    }

    @Override
    public void resumeAppPowerResource(int uid) {
        // TODO
    }

    public void playbackStateChanged(int uid, int pid, int oldState, int newState) {
        AudioRecord record = getAudioRecord(pid);
        if (record != null) {
            record.playbackStateChanged(oldState, newState);
        }
    }

    public void recordAudioFocus(int uid, int pid, String clientId, boolean request) {
        AudioRecord record = getOrCreateAudioRecord(uid, pid);
        record.recordAudioFocus(request, clientId);
    }

    public void recordAudioFocusLoss(int uid, String clientId, int focusLoss) {
        AudioRecord record = getAudioRecord(uid, clientId);
        if (record != null) {
            record.recordAudioFocusLoss(clientId, focusLoss);
        }
    }

    public void onPlayerTrack(int uid, int pid, int piid, int sessionId) {
        AudioRecord record = getOrCreateAudioRecord(uid, pid);
        record.onPlayerTrack(piid, sessionId);
    }

    public void onPlayerRlease(int uid, int pid, int piid) {
        AudioRecord record = getAudioRecord(pid);
        if (record != null) {
            record.onPlayerRlease(piid);
        }
    }

    public void onPlayerEvent(int uid, int pid, int piid, int event) {
        AudioRecord record = getAudioRecord(pid);
        if (record != null) {
            record.onPlayerEvent(piid, event);
        }
    }

    public void onRecorderTrack(int uid, int pid, int riid) {
        AudioRecord record = getOrCreateAudioRecord(uid, pid);
        record.onRecorderTrack(riid);
    }

    public void onRecorderRlease(int uid, int riid) {
        AudioRecord record = getAudioRecord(uid, riid);
        if (record != null) {
            record.onRecorderRlease(riid);
        }
    }

    public void onRecorderEvent(int uid, int riid, int event) {
        AudioRecord record = getAudioRecord(uid, riid);
        if (record != null) {
            record.onRecorderEvent(riid, event);
        }
    }

    public void reportTrackStatus(int uid, int pid, int sessionId, boolean isMuted) {
        AudioRecord record = getAudioRecord(pid);
        if (record != null) {
            record.reportTrackStatus(sessionId, isMuted);
        }
    }

    public void uidAudioStatusChanged(int uid, boolean active) {
        if (!active) {
            synchronized (mActivePidsMap) {
                for (int i = 0; i < mActivePidsMap.size(); i++) {
                    AudioRecord record = mActivePidsMap.valueAt(i);
                    if (record != null && record.mOwnerUid == uid) {
                        record.uidAudioStatusChanged(active);
                    }
                }
            }
        }
    }

    public void uidVideoStatusChanged(int uid, boolean active) {}

    private void updateUidStatus(int uid, int behavier) {
        boolean active = isAppResourceActive(uid);
        reportResourceStatus(uid, active, behavier);
    }

    private class AudioRecord {
        int mOwnerUid;
        int mOwnerPid;
        boolean mActive = false;
        boolean mCurrentActive = false;
        int mBehavier = 0;
        int mCurrentBehavier = 0;
        int mPlaybackState = -1;
        final ArrayMap<String, Integer> mFocusedClientIds = new ArrayMap<>();
        int mFocusLoss = AudioManager.AUDIOFOCUS_NONE;

        final ArrayMap<Integer, PlayerRecord> mPlayerRecords = new ArrayMap<>();
        final ArrayMap<Integer, RecorderRecord> mRecorderRecords = new ArrayMap<>();
        Runnable mInactiveDelayTask;

        AudioRecord(int uid, int pid) {
            mOwnerUid = uid;
            mOwnerPid = pid;
        }

        boolean containsRecorder(int riid) {
            synchronized (mRecorderRecords) {
                for (RecorderRecord recorder : mRecorderRecords.values()) {
                    if (recorder.mRiid == riid) {
                        return true;
                    }
                }
            }
            return false;
        }

        boolean containsClientId(String clientId) {
            synchronized (mFocusedClientIds) {
                return mFocusedClientIds.containsKey(clientId);
            }
        }

        void playbackStateChanged(int oldState, int newState) {
            if (mPlaybackState != newState) {
                mPlaybackState = newState;
                sendUpdateAudioStatusMsg();
                if (newState == PlaybackState.STATE_PAUSED) {
                    mLastMusicPlayPid = mOwnerPid;
                    mLastMusicPlayTimeStamp = SystemClock.uptimeMillis();
                }
            }
        }

        void recordAudioFocus(boolean request, String clientId) {
            synchronized (mFocusedClientIds) {
                if (request) {
                    mFocusedClientIds.put(clientId, AudioManager.AUDIOFOCUS_NONE);
                } else {
                    mFocusedClientIds.remove(clientId);
                }
                if (mFocusedClientIds.size() > 0) {
                    return;
                }
            }
            sendUpdateAudioStatusMsg();
        }

        void recordAudioFocusLoss(String clientId, int focusLoss) {
            synchronized (mFocusedClientIds) {
                mFocusedClientIds.put(clientId, focusLoss);
            }
        }

        void onPlayerTrack(int piid, int sessionId) {
            synchronized (mPlayerRecords) {
                if (!mPlayerRecords.containsKey(piid)) {
                    PlayerRecord record = new PlayerRecord(piid, sessionId);
                    mPlayerRecords.put(piid, record);
                    sendUpdateAudioStatusMsg();
                }
            }
        }

        void onPlayerRlease(int piid) {
            synchronized (mPlayerRecords) {
                mPlayerRecords.remove(piid);
            }
            sendUpdateAudioStatusMsg();
        }

        void onPlayerEvent(int piid, int event) {
            PlayerRecord record = getPlayerRecord(piid);
            if (record != null) {
                record.mEvent = event;
                if (mPlaybackState < 0) {
                    sendUpdateAudioStatusMsg();
                }
            }
        }

        void reportTrackStatus(int sessionId, boolean isMuted) {
            synchronized (mPlayerRecords) {
                for (PlayerRecord r : mPlayerRecords.values()) {
                    if (sessionId == r.mSessionId) {
                        r.mEvent = isMuted ? PLAYER_STATE_ZERO_PLAYER
                                : AudioPlaybackConfiguration.PLAYER_UPDATE_DEVICE_ID;
                        sendUpdateAudioStatusMsg();
                        return;
                    }
                }
            }
        }

        void onRecorderTrack(int riid) {
            synchronized (mRecorderRecords) {
                if (!mRecorderRecords.containsKey(riid)) {
                    RecorderRecord record = new RecorderRecord(riid);
                    mRecorderRecords.put(riid, record);
                }
            }
        }

        void onRecorderRlease(int riid) {
            synchronized (mRecorderRecords) {
                mRecorderRecords.remove(riid);
            }
        }

        void onRecorderEvent(int riid, int event) {
            RecorderRecord record = getRecorderRecord(riid);
            if (record != null) {
                record.mEvent = event;
                sendUpdateAudioStatusMsg();
            }
        }

        void uidAudioStatusChanged(boolean active) {
            if (!active) {
                synchronized (mPlayerRecords) {
                    for (PlayerRecord player : mPlayerRecords.values()) {
                        if (player.mEvent == AudioPlaybackConfiguration.PLAYER_UPDATE_DEVICE_ID ||
                                player.mEvent == AudioPlaybackConfiguration.PLAYER_STATE_STARTED) {
                            player.mEvent = AudioPlaybackConfiguration.PLAYER_STATE_PAUSED;
                        }
                    }
                }
                sendUpdateAudioStatusMsg();
            }
        }

        private void sendUpdateAudioStatusMsg() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateAudioStatus();
                }
            });
        }

        private void updateAudioStatus() {
            boolean active = false;
            int behavier = 0;
            boolean inactiveDelay = false;

            if (mPlaybackState < 0) {
                synchronized (mPlayerRecords) {
                    if (mPlayerRecords.size() > 0) {
                        behavier |= AppPowerResourceManager.RESOURCE_BEHAVIOR_PLAYER;
                    }
                    for (PlayerRecord player : mPlayerRecords.values()) {
                        if (player.mEvent == AudioPlaybackConfiguration.PLAYER_UPDATE_DEVICE_ID ||
                                player.mEvent == AudioPlaybackConfiguration.PLAYER_STATE_STARTED) {
                            active = true;
                            break;
                        }
                    }
                    inactiveDelay = !active;
                }
            } else {
                behavier |= AppPowerResourceManager.RESOURCE_BEHAVIOR_PLAYER;
                behavier |= AppPowerResourceManager.RESOURCE_BEHAVIOR_PLAYER_PLAYBACK;
                if (mPlaybackState != PlaybackState.STATE_PAUSED) {
                    active = true;
                }
            }
            if (!active) {
                synchronized (mFocusedClientIds) {
                    if (mFocusedClientIds.size() > 0) {
                        for (int focusLoss : mFocusedClientIds.values()) {
                            if (focusLoss == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                                active = true;
                                break;
                            }
                        }
                    }
                }
            }

            synchronized (mRecorderRecords) {
                if (mRecorderRecords.size() > 0) {
                    behavier |= AppPowerResourceManager.RESOURCE_BEHAVIOR_RECORDER;
                }
                for (RecorderRecord recorder : mRecorderRecords.values()) {
                    if (recorder.mEvent == AudioManager.RECORDER_STATE_STARTED) {
                        active = true;
                    }
                }
            }

            if ((behavier & AppPowerResourceManager.RESOURCE_BEHAVIOR_PLAYER) != 0
                    && (behavier & AppPowerResourceManager.RESOURCE_BEHAVIOR_RECORDER) != 0) {
                int mode = mAudioManager.getMode();
                if (mode == AudioManager.MODE_IN_CALL
                        || mode == AudioManager.MODE_IN_COMMUNICATION) {
                    behavier |= AppPowerResourceManager.RESOURCE_BEHAVIOR_INCALL;
                }
            }

            if (active != mCurrentActive || behavier != mCurrentBehavier) {
                mCurrentActive = active;
                mCurrentBehavier = behavier;
                if (inactiveDelay) {
                    if (mInactiveDelayTask == null) {
                        mInactiveDelayTask = new Runnable() {
                            @Override
                            public void run() {
                                realUpdateAudioStatus();
                                mInactiveDelayTask = null;
                            }
                        };
                        mHandler.postDelayed(mInactiveDelayTask, AUDIO_INACTIVE_DELAY_TIME);
                    }
                } else {
                    if (mInactiveDelayTask != null) {
                        mHandler.removeCallbacks(mInactiveDelayTask);
                        mInactiveDelayTask = null;
                    }
                    realUpdateAudioStatus();
                }
            }
        }

        private void realUpdateAudioStatus() {
            if (mActive != mCurrentActive || mBehavier != mCurrentBehavier) {
                mActive = mCurrentActive;
                mBehavier = mCurrentBehavier;
                updateUidStatus(mOwnerUid, mBehavier);
                reportResourceStatus(mOwnerUid, mOwnerPid, mActive, mBehavier);
                if (DEBUG) {
                    Slog.d(TAG, mOwnerUid + " " + mOwnerPid
                            + " audio active " + mActive + " " + mBehavier);
                }
                EventLog.writeEvent(SmartPowerSettings.EVENT_TAGS,
                        "audio u:" + mOwnerUid + " p:" + mOwnerPid + " s:" + mActive);
            }
        }

        private PlayerRecord getPlayerRecord(int piid) {
            synchronized (mPlayerRecords) {
                return mPlayerRecords.get(piid);
            }
        }

        private RecorderRecord getRecorderRecord(int riid) {
            synchronized (mRecorderRecords) {
                return mRecorderRecords.get(riid);
            }
        }

        private boolean isZeroAudioRecord() {
            synchronized (mPlayerRecords) {
                for (AudioRecord.PlayerRecord r : mPlayerRecords.values()) {
                    if (r.mEvent == PLAYER_STATE_ZERO_PLAYER) {
                        return true;
                    }
                }
            }
            return false;
        }

        class PlayerRecord {
            int mPiid;
            int mEvent;
            int mSessionId;

            PlayerRecord(int piid, int sessionId) {
                mPiid = piid;
                mSessionId = sessionId;
            }
        }

        class RecorderRecord {
            int mRiid;
            int mEvent;

            RecorderRecord(int piid) {
                mRiid = piid;
            }
        }
    }
}
