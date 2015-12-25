package tw.com.yxj.testble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

  private Button mButtonStart, mButtonStop;

  private BluetoothLeAdvertiser mLeAdvertiser;

  private AdvertiseData mData;

  private AdvertiseSettings mSettings;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BluetoothManager manager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter adapter = manager.getAdapter();

    mLeAdvertiser = adapter.getBluetoothLeAdvertiser();

    setAdvertiseSettings();
    setAdvertiseData();

    mButtonStart = (Button) findViewById(R.id.buttonStart);
    mButtonStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mLeAdvertiser.startAdvertising(mSettings, mData, mCallback);
      }
    });
    mButtonStop = (Button) findViewById(R.id.buttonStop);
    mButtonStop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mLeAdvertiser.stopAdvertising(mCallback);
      }
    });
  }

  private AdvertiseCallback mCallback = new AdvertiseCallback() {
    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
      super.onStartSuccess(settingsInEffect);
    }

    @Override
    public void onStartFailure(int errorCode) {
      super.onStartFailure(errorCode);
    }
  };

  protected void setAdvertiseData() {
    AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
    ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
    byte[] uuid = getIdAsByte(UUID.randomUUID());
    mManufacturerData.put(0, (byte)0xBE); // Beacon Identifier
    mManufacturerData.put(1, (byte)0xAC); // Beacon Identifier
    for (int i=2; i<=17; i++) {
      mManufacturerData.put(i, uuid[i-2]); // adding the UUID
    }
    mManufacturerData.put(18, (byte)0x00); // first byte of Major
    mManufacturerData.put(19, (byte)0x09); // second byte of Major
    mManufacturerData.put(20, (byte)0x00); // first minor
    mManufacturerData.put(21, (byte)0x06); // second minor
    mManufacturerData.put(22, (byte)0xB5); // txPower
    mBuilder.addManufacturerData(224, mManufacturerData.array()); // using google's company ID
    mData = mBuilder.build();
  }

  protected void setAdvertiseSettings() {
    AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
    mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
    mBuilder.setConnectable(false);
    mBuilder.setTimeout(0);
    mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
    mSettings = mBuilder.build();
  }

  private byte[] getIdAsByte(UUID uuid)
  {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }
}
