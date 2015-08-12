package com.anstar.fieldwork;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.anstar.models.AppointmentInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.list.AppointmentModelList;

import java.util.ArrayList;

public class SyncService extends Service {
    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppointmentModelList.Instance().ClearDB();
        loadAppointmentModelList();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public void loadAppointmentModelList() {
        try {
            AppointmentModelList.Instance().load(new ModelDelegates.ModelDelegate<AppointmentInfo>() {

                @Override
                public void ModelLoaded(ArrayList<AppointmentInfo> list) {
                    stopSelf();
                }

                @Override
                public void ModelLoadFailedWithError(
                        String error) {
                    stopSelf();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }
}
