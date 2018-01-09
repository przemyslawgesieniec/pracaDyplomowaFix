package com.gesieniec.przemyslaw.aviotsystem.voicehandler;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.gesieniec.przemyslaw.aviotsystem.MainActivity;
import com.gesieniec.przemyslaw.aviotsystem.taskdispatcher.TaskDispatcher;

import java.util.Locale;

/**
 * Created by przem on 31.10.2017.
 */

public class VoiceRecognition implements RecognitionListener {

    private MainActivity mainActivity;

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    private SpeechRecognizer speechRecognizer;

    public Intent getSpeechRecognizerIntent() {
        return speechRecognizerIntent;
    }

    private Intent speechRecognizerIntent;

    public VoiceRecognition(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mainActivity);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,mainActivity.getPackageName());
    }

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {
        mainActivity.setAviotButtonState(false);
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        if (null != results) {
            TaskDispatcher.newTask(TaskDispatcher.VoiceTaskContext.EXECUTE_VOICE_COMMAND,
                    new VoiceCommand(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)));
        }
        mainActivity.setAviotButtonState(false);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {}

    @Override
    public void onEvent(int eventType, Bundle params) {}
}
