package com.inonitylab.helloandroidthings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.inonitylab.helloandroidthings.network.APIService;
import com.inonitylab.helloandroidthings.network.ApiClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private APIService apiService;

    private static final String TOUCH_BUTTON_A_PIN = "BCM21";

    private Gpio bus;
    int callCounter = 0;
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = new ApiClient().getClient().create(APIService.class);

        PeripheralManagerService service = new PeripheralManagerService();
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(view -> {
            onStop();
            onDestroy();
            MainActivity.this.finish();
        });
       // bus = null;

        try {

            bus = service.openGpio(TOUCH_BUTTON_A_PIN);
        } catch (IOException e) {
            throw new IllegalStateException(TOUCH_BUTTON_A_PIN + " bus cannot be opened.", e);
        }

        try {
            bus.setDirection(Gpio.DIRECTION_IN);
            bus.setActiveType(Gpio.ACTIVE_LOW);
        } catch (IOException e) {
            throw new IllegalStateException(TOUCH_BUTTON_A_PIN + " bus cannot be configured.", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            bus.setEdgeTriggerType(Gpio.EDGE_BOTH);
            bus.registerGpioCallback(touchButtonACallback);
        } catch (IOException e) {
            throw new IllegalStateException(TOUCH_BUTTON_A_PIN + " bus cannot be monitored.", e);
        }
    }

    private final GpioCallback touchButtonACallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (gpio.getValue()) {
                    sendNetworkCall();
                    Log.i(TAG, "............... ON PRESSED DOWN");
                } else {
                    Log.i(TAG, "............... ON PRESSED UP");
                }
            } catch (IOException e) {
                throw new IllegalStateException(TOUCH_BUTTON_A_PIN + " cannot be read.", e);
            }
            return true;
        }
    };

    private void sendNetworkCall() {
        callCounter++;
        Call<String> call = apiService.setCountRequest(String.valueOf(callCounter));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "onResponse: .................................. success ");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "onFailure: ........................ failed " +t);
            }
        });

    }

    @Override
    protected void onStop() {
        bus.unregisterGpioCallback(touchButtonACallback);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregisterGpioCallback(touchButtonACallback);
        try {
            bus.close();
            bus = null;
        } catch (IOException e) {
            Log.e("TUT", TOUCH_BUTTON_A_PIN + " bus cannot be closed, you may experience errors on next launch.", e);
        }
    }
}
