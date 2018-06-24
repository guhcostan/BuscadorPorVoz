package com.example.guhcostan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class MainActivity extends Activity {

    public Button youtubeB;
    public Button googleB;
    public String pesquisa;
    private SpeechToText speechService;
    private MicrophoneHelper microphoneHelper;
    private MicrophoneInputStream capture;
    private boolean listening = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        youtubeB = findViewById(R.id.button);
        googleB = findViewById(R.id.googleB);
        microphoneHelper = new MicrophoneHelper(this);
        speechService = initSpeechToTextService();
        youtubeB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Button Pressed
                    capture = microphoneHelper.getInputStream(true);
                    pesquisa = "Youtube";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                speechService.recognizeUsingWebSocket(getRecognizeOptions(),
                                        new MicrophoneRecognizeDelegate());
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    microphoneHelper.closeInputStream();
                }
                return false;
            }
        });
        googleB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Button Pressed
                    capture = microphoneHelper.getInputStream(true);
                    pesquisa = "Google";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                speechService.recognizeUsingWebSocket(getRecognizeOptions(),
                                        new MicrophoneRecognizeDelegate());
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    microphoneHelper.closeInputStream();
                }
                return false;
            }
        });

    }
    private SpeechToText initSpeechToTextService() {
        IamOptions options = new IamOptions.Builder()
                .apiKey("bVUjv4lWq7BCeDI2Mu-pVRZmd_t3vOaFOFE4vSzBJzyZ")
                .build();
        SpeechToText service = new SpeechToText(options);
        service.setEndPoint(getString(R.string.speech_text_url));
        return service;
    }
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder().audio(capture).contentType(ContentType.OPUS.toString())
                .model("pt-BR_BroadbandModel").interimResults(false).inactivityTimeout(2000).build();
    }


    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {

        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                if (pesquisa.equals("Google")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.google.com.br/search?q=" + text));
                    startActivity(i);
                }
                if (pesquisa.equals("Youtube")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.youtube.com/results?search_query=" + text));
                    startActivity(i);

                }
            }
        }
    }

}
