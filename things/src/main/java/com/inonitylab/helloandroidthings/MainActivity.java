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

import java.io.IOException;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mGpio = service.openGpio("BCM6");
 * mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private String TAG = "MainActivity";

    Button button;
    Button buttonClose;
    TextView textView;

    int i = 1;
    private static final String GPIO_NAME = "BCM26";
    private Gpio mGpio;

    PeripheralManagerService manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.txtDemo);
        button = findViewById(R.id.btnClick);
        buttonClose = findViewById(R.id.btnClose);
        manager = new PeripheralManagerService();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("I have changed! my value is " + i);
                i++;
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
    }

    public void configureInput(Gpio gpio) throws IOException {
        // Initialize the pin as an input
        gpio.setDirection(Gpio.DIRECTION_IN);
        // Low voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_LOW);

        // Register for all state changes
        gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        gpio.registerGpioCallback(mGpioCallback);
    }

    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                // Read the active low pin state
                if (gpio.getValue()) {
                    // Pin is LOW
                } else {
                    // Pin is HIGH
                }

                // Continue listening for more interrupts
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w(TAG, gpio + ": Error event " + error);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGpio != null) {
            try {
                mGpio.close();
                mGpio = null;
                if (mGpio != null) {
                    mGpio.unregisterGpioCallback(mGpioCallback);
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPIO", e);
            }
        }
    }


    public void configureOutput(Gpio gpio) throws IOException {

        try {
                mGpio = manager.openGpio("BCM26");
                mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                mGpio.setValue(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seeRegisterList(){
            List<String> portList = manager.getGpioList();
                if (portList.isEmpty()) {
                    Log.i(TAG, "No GPIO port available on this device.");
                } else {
                    Log.i(TAG, "List of available ports: " + portList);
                    //textView.setText(portList.toString());
                }
    }
}
