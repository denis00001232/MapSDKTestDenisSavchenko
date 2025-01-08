package com.denissavchenko.mapsdktest;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;

//Написал сервис в качестве примера, если приоложению потребуется собирать локацию фоном, когда акти
//вити не работает, нужно создавать сервис с постоянным уведомлением в шторке(какая-то там политика)
//чтобы мы не могли создавать приложений следилок.
public class LocationService extends Service {
    Handler mainHandler;
    FusedLocationProviderClient fusedLocationProviderClient;

    public LocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Хэндлер для работы с основным потоком приложения
        mainHandler = new Handler(Looper.getMainLooper());
        //клиент для работы с локацией
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        makeLoopWithNotification(); //Тут цикл поэтому внизу
        return super.onStartCommand(intent, flags, startId);
    }


    public void makeLoopWithNotification() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mainHandler.post(() -> {
                    findLocation();
                });
            }
        });
        thread.start();
    }

    public void findLocation() {
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, new CancellationTokenSource().getToken()).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(getApplicationContext(), "Ваши координаты: " + location.getLatitude() + " " + location.getLongitude(), 1).show();
            }
        });

    }
}