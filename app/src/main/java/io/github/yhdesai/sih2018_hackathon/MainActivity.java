package io.github.yhdesai.sih2018_hackathon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private TextView txtSpeechOutput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String input = "";
    private String sourceLang;
    private String targetLang;
    private String translatedText;

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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
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
                    sourceLang = "en";
                    targetLang = "hi";

                    translate.translateTextWithOptions(input, sourceLang, targetLang, translatedText);
                    txtSpeechOutput.setText(translatedText);

/*
                    // continue translating here
                    Translate translate = createTranslateService();
                    Translation translation = translate.translate(input);
                    txtSpeechOutput.setText(translation.getTranslatedText());
*/

                }
                break;
            }

        }
    }


    public static Translate createTranslateService() {
        return TranslateOptions.newBuilder().build().getService();
    }



    public static void main(String[] args) {
        String command = args[0];
        String text;

        if (command.equals("detect")) {
            text = args[1];
            translate.detectLanguage(text, System.out);
        } else if (command.equals("translate")) {
            text = args[1];
            try {
                String sourceLang = args[2];
                String targetLang = args[3];
                translate.translateTextWithOptions(text, sourceLang, targetLang, translatedText);
            } catch (ArrayIndexOutOfBoundsException ex) {
                translate.translateText(text, System.out);
            }
        } else if (command.equals("langsupport")) {
            try {
                String target = args[1];
                //TranslateText.displaySupportedLanguages(System.out, Optional.of(target));
                // TranslateText.displaySupportedLanguages(System.out, Optional.of(target));
            } catch (ArrayIndexOutOfBoundsException ex) {
                // TranslateText.displaySupportedLanguages(System.out, Optional.empty());
            }
        }
    }


}