package ortega.tomas.hlstream;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return true; //Afegim aixo per no parar la reproduccio en cas d'un fragment espatllat
            }
        });
        String uri = intent.getStringExtra("uri");
        builder = new AlertDialog.Builder(this);
        new AsyncDownload().execute(uri);
    }

    private class AsyncDownload extends AsyncTask<String, Void, Integer> {

        final Pattern modeMedia = Pattern.compile("#EXTINF:\\d+,");
        final Pattern modeFixed = Pattern.compile("#EXT-X-MEDIA:.+");
        final Pattern modeAdapt = Pattern.compile("#EXT-X-STREAM-INF:.+");
        final String gIdString = "GROUP-ID=\"";
        final String uString = "URI=\"";
        final String bwString = "BANDWIDTH=";
        ArrayList<Uri> uris;
        ArrayList<String> groupId;
        ArrayList<Integer> BW;
        ArrayList<ArrayList<Uri>> chunks;
        Uri currentUri;
        int idx, bwIdx;
        long startTime;

        String getTag(String source, String tag, char end) {
            int start = source.indexOf(tag) + tag.length();
            return source.substring(start, source.indexOf(end, start));
        }

        private void getUris(Scanner scan, int m, ArrayList<Uri> urisList) {
            String line;
            while (scan.hasNext()) {
                line = scan.nextLine();
                if (m == 1 && line.contains(gIdString)) {
                    groupId.add(getTag(line, gIdString, '"'));
                } else if (m == 1 && line.contains(uString)) {
                    String auxUri = currentUri.toString().replace(currentUri.getLastPathSegment(), getTag(line, uString, '"'));
                    urisList.add(Uri.parse(auxUri));
                } else if (m == 2 && line.contains(bwString)) {
                    BW.add(Integer.valueOf(getTag(line, bwString, ','))); //Assumim que les BW son donades en ordre creixent
                } else if (m != 1 && line.contains("http")) {
                    urisList.add(Uri.parse(line));
                }
            }
        }

        private int determineMode(Scanner inScanner) {
            while (inScanner.hasNext()) {
                if (inScanner.hasNext(modeMedia)) {
                    return 0;
                } else if (inScanner.hasNext(modeFixed)) {
                    return 1;
                } else if (inScanner.hasNext(modeAdapt)) {
                    return 2;
                } else inScanner.next();
            }
            return -1;
        }

        private int fillData(String uri, int mode, ArrayList<Uri> uriList) throws Exception {
            URL url = new URL(uri);
            currentUri = Uri.parse(uri);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Scanner inScanner = new Scanner(urlConnection.getInputStream());
            if (mode == -1) mode = determineMode(inScanner); //comprovem que es la primera crida
            getUris(inScanner, mode, uriList);
            inScanner.close();
            urlConnection.disconnect();
            if (mode == 2) {
                chunks = new ArrayList<>(BW.size());
                for (int i = 0; i < BW.size(); ++i) {
                    chunks.add(i, new ArrayList<Uri>());
                    fillData(uris.get(i).toString(), 0, chunks.get(i));
                }
            }
            return mode;
        }

        private void playFixed() {
            idx = 0;
            playTs();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playTs();
                }
            });
        }

        private void playTs() {
            if (idx == uris.size()) {
                onBackPressed();
                return;
            }
            videoView.setVideoURI(uris.get(idx));
            idx++;
            videoView.start();
        }

        private void playAdaptive() {
            idx = 0;
            bwIdx = chunks.size() / 2;
            uris = chunks.get(bwIdx);
            playTs();
            startTime = System.currentTimeMillis();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //canviar uris segons convingui
                    long endTime = System.currentTimeMillis();
                    long duration = videoView.getDuration();
                    long loadTime = endTime - startTime - duration;
                    System.out.println("loadTime: " + (endTime - startTime - duration));
                    if (loadTime > 2000) {
                        if (bwIdx > 0) bwIdx--;
                    } else if (loadTime < 1000){
                        if (bwIdx < BW.size()) bwIdx++;
                    }
                    System.out.println("bwIdx " + bwIdx);
                    uris = chunks.get(bwIdx);
                    startTime = endTime;
                    playTs();
                }
            });

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
                mode = fillData(uri, mode, uris);
            } catch (Exception exception) {
                System.out.println("Exception: " + exception.getMessage());
            }
            return mode;
        }

        protected void onPostExecute(Integer mode) {
            if (mode == 0) playFixed();
            else if (mode == 1) {
                String[] items = groupId.toArray(new String[0]);
                builder.setTitle("Escull cualitat");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncDownload().execute(uris.get(i).toString());
                    }
                });
                builder.create().show();
            } else if (mode == 2) playAdaptive();
            else onBackPressed();
        }
    }

}