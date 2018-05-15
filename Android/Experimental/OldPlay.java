package ortega.tomas.hlstream;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class OldPlay extends AppCompatActivity {

    private VideoView videoView;
    private ArrayList<Uri> uris;
    private int chunk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        videoView = (VideoView) findViewById(R.id.videoView);
        new AsyncDownload().execute(uri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playChunk();
            }
        });
    }

    private void playChunk() {
        if (uris != null && chunk < uris.size()) {
            videoView.setVideoURI(uris.get(chunk));
            chunk++;
            videoView.start();
        }
    }

    private void PlayUris(ArrayList<Uri> urisCarregats) {
        uris = urisCarregats;
        chunk = 0;
        playChunk();
    }

    private class AsyncDownload extends AsyncTask <String, Void, ArrayList<Uri>>{
        @Override
        protected ArrayList<Uri> doInBackground(String... strings) {
            ArrayList<Uri> uris = new ArrayList<>();
            String uri = strings[0];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Scanner inScanner = new Scanner(inputStream);
                Pattern pattern = Pattern.compile("http.+\\.ts");
                while (inScanner.hasNext()) { // es per testejar
                    if (inScanner.hasNext(pattern)) uris.add(Uri.parse(inScanner.next(pattern)));
                    else inScanner.next();
                }
                inScanner.close();
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), "Error in establishing connection!", Toast.LENGTH_SHORT).show();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return uris;
        }

        protected void onPostExecute(ArrayList<Uri> uris) {
            PlayUris(uris);
        }
    }
}
