package grup4.pbe.hlsparser;

import android.content.Intent;
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

public class PlayActivity extends AppCompatActivity {
    private VideoView mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();

        mContentView = findViewById(R.id.videoView);

        String uri = intent.getStringExtra("uri");
        AsyncTask asyncDownload = new AsyncDownload().execute(uri);

    }

    private class AsyncDownload extends AsyncTask<String, Void, Integer> {
        ArrayList<Uri> uris;
        ArrayList<String> groupId;
        ArrayList<Integer> BW;

        private void getUris(Scanner scan, int m) {
            //scan fins groupid si mode = 1
            //scan fins bandwidth si mode = 2
            //scan fins extinf si mode = 0
            Pattern patHttp = Pattern.compile("http:.+\\.ts");
            if (m == 0) {
                while (scan.hasNext()) {
                    if (scan.hasNext(patHttp)) uris.add(Uri.parse(scan.next(patHttp)));
                    else scan.next();
                }
            } else if (m == 1) {
                groupId = new ArrayList<>();
                Pattern patGroup = Pattern.compile("GROUP-ID:\".+\"");
                while (scan.hasNext()) {
                    if (scan.hasNext(patGroup)) groupId.add(scan.next(patGroup));
                    else if (scan.hasNext(patHttp)) uris.add(Uri.parse(scan.next(patHttp)));
                    else scan.next();
                }
            } else if (m == 2) {
                BW = new ArrayList<>();
                Pattern patBW = Pattern.compile("BANDWIDTH=\\d+");
                while (scan.hasNext()) {
                    if (scan.hasNext(patBW)) BW.add(Integer.valueOf(scan.next(patBW).replace("BANDWIDTH=","")));
                    else if (scan.hasNext(patHttp)) uris.add(Uri.parse(scan.next(patHttp)));
                    else scan.next();
                }
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            uris = new ArrayList<>();
            String uri = strings[0];
            HttpURLConnection urlConnection = null;
            Integer mode = -1; // ens indica el mode de reproduccio:
            //0 es media
            //1 hem de guardar les adreces de cada cualitat i demanar una fixa
            //2 hem de guardar cada cualitat i reproduir HLS
            try {
                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Scanner inScanner = new Scanner(inputStream);
                String line;
                while (inScanner.hasNext()) {
                    line = inScanner.nextLine();
                    System.out.println(line);
                    if (line.contains("EXTINF")) {
                        //MEDIA, reproduir els fragments en seq.
                        mode = 0;
                    } else if (line.contains("#EXT-X-MEDIA:")) {
                        //FIXED, lusuari haura de triar cualitat i reprod frag.
                        mode = 1;
                    } else if (line.contains("EXT-X-STREAM-INF")){
                        //executar HLS
                        mode = 2;
                    }
                    if (mode != -1) getUris(inScanner, mode);
                }
                inScanner.close();
                urlConnection.disconnect();
                //si mode 2, per cada uri fer getUris(fer altre inscanner, 0)
                //si mode 1, enviar dialog, escollir inscanner, fer geturis(inscanner, 0)
                //si mode 0 play
                if (mode == 1) {
                    //preguntar al usuari
                    //suposem cali baixa
                    //ho deixem
                    mode = 0;
                    url = new URL(uris.get(0).toString());


                }
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), "Error in establishing connection!", Toast.LENGTH_SHORT).show();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return mode;
        }

        protected void onPostExecute(Integer mode) {
            if (mode == 0) {
                for (Uri uri : uris) {
                    System.out.println("Reproduint el uri " + uri.toString());
                    mContentView.setVideoURI(uri);
                    mContentView.start();
                }
            } else if (mode == 2) {
                //Fer HLS
            }
        }
    }

}
