package ads.movies.npnlab.ads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUSB();
    }

    public String getUSB(){
        File storageDirectory = new File("/mnt/storage");
        if(!storageDirectory.exists()) {
            Log.e("ABC", "getUSB: '/storage' does not exist on this device");
            //return "";
            storageDirectory = new File("/storage");
            if (!storageDirectory.exists()) {
                return "";
            }
        }
        File[] files = storageDirectory.listFiles();
        if(files == null) {
            Log.e("ABC", "getUSB: Null when requesting directories inside '/storage'");
            return "";
        }

        List<String> possibleUSBStorageMounts = new ArrayList<>();
        for (File file : files) {
            String path = file.getPath();
            if (path.contains("emulated") ||
                    path.contains("sdcard") ||
                    path.contains("self")) {
                Log.d("ABC", "getUSB: Found '" + path + "' - not USB");
            } else {
                possibleUSBStorageMounts.add(path);
            }
        }

        if (possibleUSBStorageMounts.size() == 0) {
            Log.e("ABC", "getUSB: Did not find any possible USB mounts");
            return "";
        }
        if(possibleUSBStorageMounts.size() > 0) {
            Log.d("ABC", "getUSB: Found multiple possible USB mount points: " + possibleUSBStorageMounts.size());
            Log.d("ABC", "USB:" + possibleUSBStorageMounts.get(0));
            //checking_main_files(new File(possibleUSBStorageMounts.get(0)));
            //NPNGlobalMethods.saveKey(this, NPNConstants.SETTING_ROOT_PATH, possibleUSBStorageMounts.get(0));
        }

        return possibleUSBStorageMounts.get(0);
    }

    public void stateChange(String action, String data) {
        Log.d("NPN", "Something changed here: " + action);
        if (action.indexOf("MEDIA_MOUNTED") >= 0) {

            String path = data.replace("file://", "");
            Log.d("DCAR", "USB path is: " + path);

        }
        if(action.indexOf("MEDIA_REMOVED") >=0){

        }
    }
}
