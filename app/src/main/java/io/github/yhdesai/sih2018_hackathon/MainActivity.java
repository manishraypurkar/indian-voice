package io.github.yhdesai.sih2018_hackathon;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.sun.xml.internal.ws.api.message.Packet;

import java.util.ArrayList;

import javax.xml.transform.Result;

import static android.content.ContentValues.TAG;
import static java.lang.System.out;

public class MainActivity extends Activity {


    private TextView txtSpeechInput;
    private TextView txtSpeechOutput;
    private ImageButton btnSpeak;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String input = "";
    private String sourceLang;
    private String targetLang;
    private String translatedText;
    private Translate translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        txtSpeechOutput = (TextView)findViewById(R.id.txtSpeechOutput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


         // hack@yashdesai.me

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    input = result.get(0);
                    //Here, input string contains the text of what the user spoke

                    sourceLang = "hi";
                    targetLang = "en";
                    Translate translate = createTranslateService();
                    Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage(sourceLang);
                    Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage(targetLang);
                    String sourceText = input;



                  //  translate.translateTextWithOptions(input, sourceLang, targetLang, translatedText);


                  //  executeNLP(translatedText);
                  //  String action = result.getString

                    // continue translating here
                    translate = createTranslateService();
                    Translation translation = translate.translate(input);
                    txtSpeechOutput.setText(translation.getTranslatedText());
                    Translate.TranslateOption model = Translate.TranslateOption.model("nmt");
                    translation = translate.translate(sourceText, srcLang, tgtLang, model);
                    out.printf("Source Text:\n\tLang: %s, Text: %s\n", sourceLang, sourceText);
                    out.printf("TranslatedText:\n\tLang: %s, Text: %s\n", targetLang,
                            translation.getTranslatedText());
                    txtSpeechOutput.setText( translation.getTranslatedText());

                    final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN,
                            AIConfiguration.SupportedLanguages.English,
                            AIConfiguration.RecognitionEngine.System);

                    final AIDataService aiDataService = new AIDataService(config);

                    final AIRequest aiRequest = new AIRequest();
                    aiRequest.setQuery("Hello");


                    // part 2

                    new AsyncTask<AIRequest, Void, AIResponse>() {
                        @Override
                        protected AIResponse doInBackground(AIRequest... requests) {
                            final AIRequest request = requests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {
                            if (aiResponse != null) {
                                // process aiResponse here
                            }
                        }
                    }.execute(aiRequest);


                    //part 3
                    public void onResult(final AIResponse response) {
                        final Packet.Status status = response.getStatus();
                        Log.i(TAG, "Status code: " + status.getCode());
                        Log.i(TAG, "Status type: " + status.getErrorType());

                        final Result result = response.getResult();
                        Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                        final Result result = response.getResult();
                        Log.i(TAG, "Action: " + result.getAction());

                        final Result result = response.getResult();
                        final String speech = result.getFulfillment().getSpeech();
                        Log.i(TAG, "Speech: " + speech);


                        final Result result = response.getResult();
                        final Metadata metadata = result.getMetadata();
                        if (metadata != null) {
                            Log.i(TAG, "Intent id: " + metadata.getIntentId());
                            Log.i(TAG, "Intent name: " + metadata.getIntentName());
                        }


                        final Result result = response.getResult();
                        final HashMap<String, JsonElement> params = result.getParameters();
                        if (params != null && !params.isEmpty()) {
                            Log.i(TAG, "Parameters: ");
                            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                                Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                            }
                        }
                    }



                }
                break;
            }

        }
    }

    public static Translate createTranslateService() {
        return TranslateOptions.newBuilder().build().getService();
    }



}
