package com.example.elelleedictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private String value = "";
    private TextView tvWord;
    private ImageButton btnBookmark, btnVolume;
    private WebView tvWordTranslate;
    private DBHelper mDBHelper;
    private int mDicType;
    private TextToSpeech tts;
    private Locale usLocale = Locale.US;


    public DetailFragment() {
        // Required empty public constructor
    }
    public static DetailFragment getNewInstance(String value, DBHelper dbHelper, int dicType){
        DetailFragment fragment = new DetailFragment();
        fragment.value=value;
        fragment.mDBHelper=dbHelper;
        fragment.mDicType=dicType;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvWord = (TextView) view.findViewById(R.id.tvWord);
        tvWordTranslate = (WebView) view.findViewById(R.id.tvWordTranslate);
        btnBookmark = (ImageButton) view.findViewById(R.id.btnBookmark);
        btnVolume = (ImageButton) view.findViewById(R.id.btnVolume);

        Word word = mDBHelper.getWord(value,mDicType);
        tvWord.setText(word.key);
        tvWordTranslate.loadDataWithBaseURL(null,word.value,"text/html","utf-8",null);

        Word bookmarkWord = mDBHelper.getWordFromBookmark(value);
        int isMark = bookmarkWord == null? 0 : 1;
        btnBookmark.setTag(isMark );
        int icon = bookmarkWord == null? R.drawable.baseline_bookmark_border : R.drawable.baseline_bookmark_fill;
        btnBookmark.setImageResource(icon);

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = (int)btnBookmark.getTag();
                if (i==0){
                    btnBookmark.setImageResource(R.drawable.baseline_bookmark_fill);
                    btnBookmark.setTag(1);
                    mDBHelper.addBookmark(word);
                } else if (i==1) {
                    btnBookmark.setImageResource(R.drawable.baseline_bookmark_border);
                    btnBookmark.setTag(0);
                    mDBHelper.removeBookmark(word);

                }
            }
        });

        tts = new TextToSpeech(requireContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Text-to-Speech engine initialization successful

                    // Check if the US English language is available
                    if (tts.isLanguageAvailable(usLocale) == TextToSpeech.LANG_MISSING_DATA || tts.isLanguageAvailable(usLocale) == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // US English is missing or not supported
                        // Handle the error or disable the speech functionality
                        // Example:
                        Toast.makeText(requireContext(), "US English is not available", Toast.LENGTH_SHORT).show();
                        btnVolume.setEnabled(false); // Disable the volume button
                    }
                } else {
                    // Text-to-Speech engine initialization failed
                    // Handle the error or disable the speech functionality
                    // Example:
                    Toast.makeText(requireContext(), "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
                    btnVolume.setEnabled(false); // Disable the volume button
                }
            }
        });

        // Rest of the code...

        // Handle the click listener for the volume button
        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToSpeak = tvWord.getText().toString();
                if (!textToSpeak.isEmpty()) {
                    tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(requireContext(), "No word to speak", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}