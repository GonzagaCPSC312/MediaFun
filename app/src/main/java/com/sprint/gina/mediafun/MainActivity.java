package com.sprint.gina.mediafun;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    static final int TAKE_PICTURE_REQUEST = 1;
    // a MediaPlayer for playing our audio clip
    MediaPlayer mp = null;

    VideoView videoView;
    ImageView imageView;

    ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.videoView);
        imageView = (ImageView) findViewById(R.id.imageView);

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // what do we do here?
                            // 1. show picture the user just took in an ImageView
                            // 2. save picture the user just took to external storage
                            // here is an example of (1.)
                            // Intent data stores the result from our camera app
                            // there is a bitmap thumbnail of the image the user just took
                            // we need an ImageView to display it
                            Intent data = result.getData();
                            if (data != null) {
                                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                });

        Button soundButton = findViewById(R.id.buttonSound);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                // create a MediaPlayer object for our sound file
                // and start it!
                mp = MediaPlayer.create(MainActivity.this, R.raw.bell);
                mp.start();
                // when our mediaplayer is done playing its media
                // we should release
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // this method executes when the mediaplayer is done
                        Log.d(TAG, "onCompletion: ");
                        mp.release();
                        MainActivity.this.mp = null;
                    }
                });

                // we need to be aware of the state of our mediaplayer
                // override onPause() and onResume()
            }
        });

        Button videoButton = findViewById(R.id.buttonVideo);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);

                // let's add controls to our videoview
                MediaController mc = new MediaController(MainActivity.this);
                // let mc know about its videoview
                // let the videoview know about its mediacontroller
                mc.setAnchorView(videoView);
                videoView.setMediaController(mc);
                // save state
                // 1. onPause()/onResume(): the app loses focus. save position with a field
                // 2. onSaveInstanceState()/onCreate(): rotate device, change config etc.
                // 3. SharedPreferences: save state across app sessions

                // we can play videos
                // 1. in our project (res/raw) (see notes on Google Drive for an example)
                //videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.sample_video);
                // 2. already on the user's device
                // 3. that we stream from a url (in this example)
                String urlToStream = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
                // we need a Uri object to represent the url
                // when we learned about implicit intents we used Uri for a webpage
                Uri uri = Uri.parse(urlToStream);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        });

        Button pictureButton = findViewById(R.id.buttonTakePicture);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                // we are going start an implicit intent to take a picture
                // MediaStore.ACTION_IMAGE_CAPTURE
                // task: start an implicit intent for a result
                // using this action code
                // if the request code and the result code are all good
                // in onActivityResult(), write a log message
                // test your app
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureLauncher.launch(intent);
            }
        });

        Button youTubeButton = findViewById(R.id.buttonYouTube);
        youTubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YouTubeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null) {
            mp.start();
        }
    }
}
