package ads.movies.npnlab.ads;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {



    VideoView mVideoView;


    ImageView imgAdvertisement;


    private int currentAdFileIndex = -1;

    private int currentImageFileIndex = 0;

    public String roothPath = "";
    public String match_folder = "quang_cao";


    @Override
    protected void onPause() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoViewAdvertisement);
        imgAdvertisement = findViewById(R.id.imgAdvertisement);

        roothPath = getUSB();
        Log.d("DCAR", "Root path USB: " + roothPath);

        roothPath = findAdvertisementFolder(new File(roothPath));
        Log.d("DCAR", "Root path QUANG CAO: " + roothPath);

        if(roothPath.length() > 3){
            load_ad_files(new File(roothPath));
            load_img_files(new File(roothPath));


            if(name_path_list.size() > 0){
                currentAdFileIndex = 0;
                playVideoFromUSB(name_path_list.get(currentAdFileIndex));
                imgAdvertisement.setVisibility(View.GONE);
                mVideoView.setVisibility(View.VISIBLE);

            } else if(name_img_path_list.size() > 0){
                imgAdvertisement.setVisibility(View.VISIBLE);
                mVideoView.pause();
                mVideoView.setVisibility(View.GONE);

            }
            else{

            }
        }else{

        }



        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //TODO: move to next video

                if (name_path_list.size() > 0) {
                    currentAdFileIndex++;
                    if (currentAdFileIndex >= name_path_list.size()) currentAdFileIndex = 0;
                    playVideoFromUSB(name_path_list.get(currentAdFileIndex));

                }
            }
        });
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

    public String findAdvertisementFolder(File dir){
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    if (listFile[i].getAbsolutePath().toLowerCase().indexOf(match_folder) >= 0) {
                        return listFile[i].getAbsolutePath();
                    }
                }
            }
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    return  findAdvertisementFolder(listFile[i]);
                }
            }
            return "";
        }else{
            return "";
        }
    }


    ArrayList<String> name_list = new ArrayList<>();
    ArrayList<String> name_path_list = new ArrayList<>();

    ArrayList<String> name_img_list = new ArrayList<>();
    ArrayList<String> name_img_path_list = new ArrayList<>();

    private void load_img_files(File dir){
        name_img_list.clear();
        name_img_path_list.clear();

        File[] listFile = dir.listFiles();


        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    load_img_files(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith("jpg") || listFile[i].getName().endsWith("bmp")
                            || listFile[i].getName().endsWith("png")) {
                        name_img_list.add(listFile[i].getName());
                        name_img_path_list.add(listFile[i].getAbsolutePath());
                        currentImageFileIndex = 0;
                    }
                }
            }
        }
    }

    private void load_ad_files(File dir) {

        name_list.clear();
        name_path_list.clear();
        String extention = ".mp4";
        File[] listFile = dir.listFiles();


        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    load_ad_files(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(extention)) {
                        name_list.add(listFile[i].getName());
                        name_path_list.add(listFile[i].getAbsolutePath());

                    }
                }
            }
        }
    }

    public void playVideoFromUSB(String path){
        //isActivatedImageAnimation = false;
        File aFile = new File(path);
        if(aFile.exists()){
            imgAdvertisement.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.start();
        }else{
            Log.d("DCAR", "Video is not existed");
            //((MainActivity)getActivity()).fullScreenMode(false);
            //((MainActivity)getActivity()).selectFragment(HOME_FRAGMENT_INDEX);
        }
    }
}
