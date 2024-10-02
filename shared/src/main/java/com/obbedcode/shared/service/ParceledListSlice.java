package com.obbedcode.shared.service;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.obbedcode.shared.BuildConfig;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


//    private static final int MAX_IPC_SIZE = 64 * 1024; // Reduced to 64KB to be safe
//    private static final int MAX_IPC_SIZE = 1 * 1024 * 1024; // 1MB

/*
    Did somebody say IPC Transaction too big error code 3 ? No problem Let me Handle it!
    Ps ".query" and "onTransact" Max Sizes are "1mb or 1 * 1024 * 1024" around some its "64 * 1024"
    Don't even get me started with ".call"
    Super Safe, No Malformations, Get Data exceeding 1mb over IPC via Slicing
 */
public class ParceledListSlice<T extends Parcelable> implements Parcelable {
    private static final String TAG = "ObbedCode.XP.ParceledListSlice";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private int MAX_IPC_SIZE = 64 * 1024;
    private static final int BUFFER_SIZE = 8;

    private final List<T> mList;
    private int mInlineCountLimit = Integer.MAX_VALUE;
    private static final Map<String, Creator<?>> sCreatorCache = new ConcurrentHashMap<>();

    public ParceledListSlice(List<T> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();

        if (DEBUG) Log.d(TAG, "MAX_IPC_SIZE set to: " + MAX_IPC_SIZE);
        mList = new ArrayList<>(list); // Create a copy to prevent external modifications
    }

    @SuppressWarnings("unchecked")
    private ParceledListSlice(Parcel p, ClassLoader loader) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();

        if (DEBUG) Log.d(TAG, "MAX_IPC_SIZE set to: " + MAX_IPC_SIZE);
        final int N = p.readInt();
        mList = new ArrayList<>(N);
        if (DEBUG) Log.d(TAG, "Retrieving " + N + " items");
        if (N <= 0) {
            return;
        }

        Creator<T> creator = (Creator<T>) readCreator(p, loader);
        if (creator == null) {
            Log.e(TAG, "Failed to read creator, list will be empty");
            return;
        }

        int i = 0;
        while (i < N) {
            if (p.readInt() == 0) {
                break;
            }
            try {
                T item = creator.createFromParcel(p);
                mList.add(item);
                if (DEBUG) Log.d(TAG, "Read inline #" + i + ": " + item);
            } catch (Exception e) {
                Log.e(TAG, "Error reading item #" + i, e);
            }
            i++;
        }
        if (i >= N) {
            return;
        }
        final IBinder retriever = p.readStrongBinder();
        while (i < N) {
            if (DEBUG) Log.d(TAG, "Reading more @" + i + " of " + N + ": retriever=" + retriever);
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInt(i);
                retriever.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0);
                while (i < N && reply.readInt() != 0) {
                    T item = creator.createFromParcel(reply);
                    mList.add(item);
                    if (DEBUG) Log.d(TAG, "Read extra #" + i + ": " + item);
                    i++;
                }
            } catch (Throwable e) {
                Log.w(TAG, "Failure retrieving array; only received " + i + " of " + N, e);
                break;
            } finally {
                reply.recycle();
                data.recycle();
            }
        }
    }

    private Creator<?> readCreator(Parcel from, ClassLoader loader) {
        String parcelableCreatorName = from.readString();
        if (parcelableCreatorName == null) {
            return null;
        }

        // Check if the creator is already in the cache
        Creator<?> cachedCreator = sCreatorCache.get(parcelableCreatorName);
        if (cachedCreator != null) {
            return cachedCreator;
        }

        // If not in cache, resolve it and add to cache
        try {
            Class<?> parcelableClass = Class.forName(parcelableCreatorName, false, loader);
            Field creatorField = parcelableClass.getField("CREATOR");
            Creator<?> creator = (Creator<?>) creatorField.get(null);
            sCreatorCache.put(parcelableCreatorName, creator);
            return creator;
        } catch (Exception e) {
            Log.e(TAG, "Error reading Creator field", e);
            return null;
        }
    }

    private boolean isItemTooLarge(T item, int flags) {
        Parcel itemParcel = Parcel.obtain();
        try {
            item.writeToParcel(itemParcel, flags);
            int itemSize = itemParcel.dataSize();
            return itemSize > MAX_IPC_SIZE - BUFFER_SIZE;
        } finally {
            itemParcel.recycle();
        }
    }

    public static void clearCreatorCache() {
        sCreatorCache.clear();
    }

    public List<T> getList() {
        return new ArrayList<>(mList); // Return a copy to prevent external modifications
    }

    public void setInlineCountLimit(int maxCount) {
        mInlineCountLimit = Math.max(0, maxCount); // Ensure non-negative value
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final int N = mList.size();
        dest.writeInt(N);
        if (DEBUG) Log.d(TAG, "Writing " + N + " items");
        if (N > 0) {
            T first = mList.get(0);
            if (first == null) {
                Log.e(TAG, "First item in list is null");
                return;
            }
            String className = first.getClass().getName();
            if (className == null || className.isEmpty()) {
                Log.e(TAG, "Invalid class name for first item");
                return;
            }
            dest.writeString(className);

            int startPos = dest.dataPosition();
            int i = 0;
            while (i < N && i < mInlineCountLimit) {
                T item = mList.get(i);
                if (item == null) {
                    Log.e(TAG, "Null item encountered at index " + i);
                    i++;
                    continue;
                }

                if (isItemTooLarge(item, flags)) {
                    Log.w(TAG, "Item at index " + i + " exceeds maximum size, skipping.");
                    i++;
                    continue;
                }

                int itemStartPos = dest.dataPosition();
                dest.writeInt(1); // Flag to indicate an item follows
                item.writeToParcel(dest, flags);
                int itemSize = dest.dataPosition() - itemStartPos;

                if (dest.dataPosition() - startPos > MAX_IPC_SIZE - BUFFER_SIZE) {
                    Log.w(TAG, "Size limit reached at index " + i + ". Total size: " + (dest.dataPosition() - startPos));
                    dest.setDataPosition(itemStartPos);
                    break;
                }

                if (DEBUG) Log.d(TAG, "Wrote inline #" + i + ": " + item + " size: " + itemSize);
                i++;
            }

            if (i < N) {
                dest.writeInt(0); // Flag to indicate no more inline items
                Binder retriever = new Binder() {
                    @Override
                    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                        if (code != FIRST_CALL_TRANSACTION) {
                            return super.onTransact(code, data, reply, flags);
                        }
                        int i = data.readInt();
                        if (DEBUG) Log.d(TAG, "Writing more @" + i + " of " + N);
                        int startPos = reply.dataPosition();
                        while (i < N) {
                            T item = mList.get(i);
                            if (item == null) {
                                Log.e(TAG, "Null item encountered at index " + i + " during additional writing");
                                i++;
                                continue;
                            }

                            if (isItemTooLarge(item, flags)) {
                                Log.w(TAG, "Item at index " + i + " exceeds maximum size during additional writing, skipping.");
                                i++;
                                continue;
                            }

                            int itemStartPos = reply.dataPosition();
                            reply.writeInt(1); // Flag to indicate an item follows
                            item.writeToParcel(reply, flags);
                            int itemSize = reply.dataPosition() - itemStartPos;

                            if (reply.dataPosition() - startPos > MAX_IPC_SIZE - BUFFER_SIZE) {
                                Log.w(TAG, "Size limit reached at index " + i + " during additional writing. Total size: " + (reply.dataPosition() - startPos));
                                reply.setDataPosition(itemStartPos);
                                break;
                            }

                            if (DEBUG) Log.d(TAG, "Wrote extra #" + i + ": " + item + " size: " + itemSize);
                            i++;
                        }
                        reply.writeInt(0); // Flag to indicate no more items
                        return true;
                    }
                };
                if (DEBUG) Log.d(TAG, "Breaking @" + i + " of " + N + ": retriever=" + retriever);
                dest.writeStrongBinder(retriever);
            }
        }
    }

    public static final Creator<ParceledListSlice> CREATOR = new ClassLoaderCreator<ParceledListSlice>() {
        @Override
        public ParceledListSlice createFromParcel(Parcel in) {
            return new ParceledListSlice(in, getClass().getClassLoader());
        }

        @Override
        public ParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            return new ParceledListSlice(in, loader);
        }

        @Override
        public ParceledListSlice[] newArray(int size) {
            return new ParceledListSlice[size];
        }
    };
}