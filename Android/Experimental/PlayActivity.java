package ortega.tomas.hlstream;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PlayActivity extends AppCompatActivity {

    private VideoView videoView;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        videoView = (VideoView) findViewById(R.id.videoView);
        String uri = intent.getStringExtra("uri");
        builder = new AlertDialog.Builder(this);
        new AsyncDownload().execute(uri);
    }

    private class AsyncDownload extends AsyncTask<String, Void, Integer> {
        ArrayList<Uri> uris;
        ArrayList<String> groupId;
        ArrayList<Integer> BW;
        ArrayList<ArrayList<Uri>> chunks;
        final Pattern patHttp = Pattern.compile("http:.+\\.ts");
        //final Pattern patGroup = Pattern.compile("GROUP-ID=\"[\\w\\d]+\"");
        final Pattern patGroup = Pattern.compile(".+GROUP-ID=.+");
        final Pattern patBW = Pattern.compile("BANDWIDTH=\\d+");
        final Pattern modeMedia = Pattern.compile("#EXTINF:\\d+,");
        final Pattern modeFixed = Pattern.compile("#EXT-X-MEDIA:.+");
        final Pattern modeAdapt = Pattern.compile("#EXT-X-STREAM-INF:.+");
        int idx;

        private void getUris(Scanner scan, int m, ArrayList<Uri> urisList) {
            System.out.println("Got to get uris");
            //HI HA SCANS QUE NO ES FAN BE, fixed si
            while (scan.hasNext()) {
                if (m == 1 && scan.hasNext(patGroup)) {
                    groupId.add(scan.next(patGroup));
                } else if (m == 2 && scan.hasNext(patBW)) {
                    BW.add(Integer.valueOf(scan.next(patBW).replace("BANDWIDTH=", "")));
                } else if (scan.hasNext(patHttp)) {
                    urisList.add(Uri.parse(scan.next(patHttp)));
                } else System.out.println(scan.next());
            }
            //for (Uri uri: urisList) System.out.println(uri.toString());
        }

        private int determineMode(Scanner inScanner) throws Exception {
            System.out.println("Got to det mode");

            while (inScanner.hasNext()) {
                if (inScanner.hasNext(modeMedia)) {
                    return 0;
                } else if (inScanner.hasNext(modeFixed)) {
                    System.out.println("Mode fixed");
                    return 1;
                } else if (inScanner.hasNext(modeAdapt)) {
                    return 2;
                } else inScanner.next();
            }
            throw new Exception("Error in mode");
        }

        private int fillData(String uri, int mode, ArrayList<Uri> uriList) throws Exception {
            System.out.println("Got to fill data");
            URL url = new URL(uri);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Scanner inScanner = new Scanner(urlConnection.getInputStream());
            if (mode == -1) mode = determineMode(inScanner); //comprovem que es la primera crida
            getUris(inScanner, mode, uriList);
            inScanner.close();
            urlConnection.disconnect();
            if (mode == 2) {
                chunks = new ArrayList<>(BW.size());
                for (int i = 0; i < BW.size(); ++i) {
                    fillData(uris.get(i).toString(), 0, chunks.get(i));
                }
            }
            return mode;
        }

        private void playFixed() {
            idx = 0;
            playFixedTs();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playFixedTs();
                }
            });
        }

        private void playFixedTs() {
            if (uris != null && idx < uris.size()) {
                videoView.setVideoURI(uris.get(idx));
                idx++;
                videoView.start();
            }
        }

        private void playAdaptive() {
            //TO-DO
        }

        @Override
        protected Integer doInBackground(String... strings) {
            uris = new ArrayList<>();
            groupId = new ArrayList<>();
            BW = new ArrayList<>();
            String uri = strings[0];
            Integer mode = -1; // ens indica el mode de reproduccio:
            //0 es media
            //1 hem de guardar les adreces de cada cualitat i demanar una fixa
            //2 hem de guardar cada cualitat i reproduir HLS
            try {
                mode = fillData(uri, -1, uris);
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return mode;
        }

        protected void onPostExecute(Integer mode) {
            System.out.println("Got to post execute mode " + mode);
            if (mode == 0) playFixed();
            else if (mode == 1) {
                String[] items = groupId.toArray(new String[0]);
                System.out.println("Got to create builder");
                builder.setTitle("Escull cualitat");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncDownload().execute(uris.get(i).toString());
                    }
                });
                builder.create().show();
            } else if (mode == 2) playAdaptive();
        }
    }

}
