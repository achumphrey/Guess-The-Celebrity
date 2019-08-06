package com.example.zappycode.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    String[] namesOfCeleb = new String[4];
    int locationOfCorrectAnswer = 0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void chooseACeleb(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Worng! It was " + namesOfCeleb[locationOfCorrectAnswer],Toast.LENGTH_SHORT).show();
      //      Toast.makeText(getApplicationContext(),"Worng! It was " + celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
            //both toasts work very well. Just tweaked to try something different.
        }

        displayNewCeleb();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]); //creates a url object to connect to a given url.

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);// reader using read() method
                // will put the str in the stream till it is read by the reader

                int data = reader.read(); //This method returns an integer value
                // (which is the integer value of the character in the ascii table)
                // read from the stream. It can range from 0 to 65535.
                // Else it returns -1 if no character has been read.

                while (data != -1) { //Everything in the ascii table has its
                                    // decimal (number) value and the
                                    // character (letter) value. Computers can only
                                    // understand numbers, so an ASCII code is the
                                    // numerical representation of a character
                                    // such as 'a' or '@' or an action of some sort.
                    char current = (char) data; //converts from number to letter
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void displayNewCeleb() {
        try {
            Random rand = new Random();

            chosenCeleb = rand.nextInt(celebURLs.size()); //randomly choose a celeb

            //note that a given celeb's url and name will appear at the same index position
            //on the celebURLS and celebNames arraylist. So, the chosenCeleb index is same on
            //both lists, referring to the same celeb.

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage); //get the chosen celeb's image

            locationOfCorrectAnswer = rand.nextInt(4); //randomly choose one of four buttons

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    namesOfCeleb[i] = celebNames.get(chosenCeleb); //put the celeb name in the chosen location
                } else {
                    incorrectAnswerLocation = rand.nextInt(celebURLs.size());

                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    }

                    namesOfCeleb[i] = celebNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(namesOfCeleb[0]);
            button1.setText(namesOfCeleb[1]);
            button2.setText(namesOfCeleb[2]);
            button3.setText(namesOfCeleb[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupCeleb(DownloadTask task){

        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebNames.add(m.group(1));
            }

        //    displayNewCeleb();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        setupCeleb(task);
        displayNewCeleb();

        //I decided to put all the below set codes in a method to clean up the onCreate() method

        /*
         String result = null;


        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"listedArticles\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebNames.add(m.group(1));
            }

            displayNewCeleb();

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
