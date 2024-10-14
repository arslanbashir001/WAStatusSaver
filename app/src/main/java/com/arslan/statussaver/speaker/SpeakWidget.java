//package com.devatrii.statussaver.speaker;
//
//import static android.content.Context.CONNECTIVITY_SERVICE;
//
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.TypedArray;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.lifecycle.Lifecycle;
//
//
//public class SpeakWidget extends RelativeLayout implements  NewTextToSpeech.OnTextToSpeechListener, SpeechLifecycle {
//    ProgressBar progressBar;
//    ImageView icon;
//    boolean enableProgress;
//    int progressVisibility;
//    int iconStart;
//    int iconStop;
//    int progressbarTint;
//    int iconTint;
//    private String textToSpeak;
//    private String langCode;
//    NewTextToSpeech newTextToSpeech;
//    public SpeakWidget(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        progressBar = new ProgressBar(context);
//        icon=new ImageView(context);
//        TypedArray a = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.SpeakWidget,
//                0, 0);
//        try {
//            enableProgress = a.getBoolean(R.styleable.SpeakWidget_widget_enableProgress, true);
//            progressVisibility = a.getInt(R.styleable.SpeakWidget_widget_progress_visibility, View.GONE);
//            progressbarTint = a.getColor(R.styleable.SpeakWidget_progressbarTint,getResources().getColor(R.color.speak_widget_progress_color));
//            iconTint = a.getColor(R.styleable.SpeakWidget_iconTint,getResources().getColor(R.color.speak_widget_progress_color));
//            iconStart = a.getResourceId(R.styleable.SpeakWidget_widget_start_icon, R.drawable.ic_speak_custom_iv);
//            iconStop = a.getResourceId(R.styleable.SpeakWidget_widget_stop_icon, R.drawable.ic_stop_custom_iv);
//            init();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            a.recycle();
//        }
//
//    }
//
//    public SpeakWidget(Context context) {
//        super(context);
//        init();
//    }
//    private void init() {
//        try {
//            newTextToSpeech=NewTextToSpeech.newInstance();
//            setupIconView();
//            setupProgressView();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void setupIconView() {
//        try {
//            icon=new ImageView(getContext());
//            LayoutParams layoutParams =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//            icon.setLayoutParams(layoutParams);
//            icon.setImageResource(R.drawable.ic_speak_custom_iv);
//            int[][] states = new int[][] {
//                    new int[] { android.R.attr.state_enabled}, // enabled
//                    new int[] {-android.R.attr.state_enabled}, // disabled
//                    new int[] {-android.R.attr.state_checked}, // unchecked
//                    new int[] { android.R.attr.state_pressed}  // pressed
//            };
//
//            int[] colors = new int[] {
//                    iconTint,
//                    iconTint,
//                    iconTint,
//                    iconTint
//            };
//            icon.setImageTintList(new ColorStateList(
//                    states,
//                    colors
//            ));
//            this.addView(icon);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setupProgressView() {
//        try {
//            progressBar = new ProgressBar(getContext());
//            if (enableProgress){
//                progressBar.setVisibility(progressVisibility);
//                int[][] states = new int[][] {
//                        new int[] { android.R.attr.state_enabled}, // enabled
//                        new int[] {-android.R.attr.state_enabled}, // disabled
//                        new int[] {-android.R.attr.state_checked}, // unchecked
//                        new int[] { android.R.attr.state_pressed}  // pressed
//                };
//
//                int[] colors = new int[] {
//                        progressbarTint,
//                        progressbarTint,
//                        progressbarTint,
//                        progressbarTint
//                };
//                progressBar.setIndeterminateTintList(new ColorStateList(
//                        states,colors
//                ));
//                this.addView(progressBar);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//    boolean clicked=false;
//
//    public boolean isNetworkAvailable(Context activity) {
//        if (activity == null) {
//            return false;
//        }
//        try {
//            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
//            NetworkInfo info = cm.getActiveNetworkInfo();
//
//            return info != null && info.isConnected();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//
//    Lifecycle mlifecycle;
//    public void startSpeech(Lifecycle lifecycle){
//        mlifecycle=lifecycle;
//        mlifecycle.addObserver(this);
//
//        if (!isNetworkAvailable(getContext())){
//            showToast("No Internet found");
//            return;
//        }
//
//        if (textToSpeak==null){
//            showToast("Didn't have any text to speech");
//            return;
//        }
//        if (textToSpeak.isEmpty()){
//            showToast("Didn't have any text to speech");
//            return;
//        }
//        if (langCode==null){
//            showToast("Speech language code is null");
//            return;
//        }
//        if (langCode.isEmpty()){
//            showToast("Speech language code is empty");
//            return;
//        }
//
//        if (!clicked){
//            newTextToSpeech.reset();
//            icon.setImageResource(iconStop);
//            icon.setVisibility(GONE);
//            newTextToSpeech.startSpeech(
//                    getContext(),
//                    textToSpeak,
//                    langCode,
//                    this
//            );
//            if (enableProgress){
//                progressBar.setVisibility(VISIBLE);
//            }
//            clicked=true;
//        }else {
//            newTextToSpeech.reset();
//            clicked=false;
//        }
//
//    }
//
//    private void showToast(String s) {
//        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
//    }
//
//
//    public String getTextToSpeak() {
//        return textToSpeak;
//    }
//
//    public void setTextToSpeak(String textToSpeak) {
//        this.textToSpeak = textToSpeak;
//    }
//
//    public String getLangCode() {
//        return langCode;
//    }
//
//    public void setLangCode(String langCode) {
//        this.langCode = langCode;
//    }
//
//    @Override
//    public void OnSpeechReset() {
//        try {
//            clicked=false;
//            icon.setVisibility(VISIBLE);
//            icon.setImageResource(iconStart);
//            if (enableProgress)
//                progressBar.setVisibility(GONE);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void OnSpeechPrepared(MediaPlayer player) {
//        try {
//            icon.setVisibility(VISIBLE);
//            icon.setImageResource(iconStop);
//            if (enableProgress)
//                progressBar.setVisibility(GONE);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void OnSpeechCompleted(MediaPlayer player) {
//        try {
//            icon.setVisibility(VISIBLE);
//            icon.setImageResource(iconStart);
//            if (enableProgress)
//                progressBar.setVisibility(GONE);
//            clicked=false;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void OnSpeechMediaPlayerError(MediaPlayer mediaPlayer, int i, int i1) {
//        try {
//            icon.setVisibility(VISIBLE);
//            icon.setImageResource(iconStart);
//            if (enableProgress)
//                progressBar.setVisibility(GONE);
//            clicked=false;
//            if (getContext()!=null){
//                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void OnSpeechError() {
//        try {
//            icon.setVisibility(VISIBLE);
//            icon.setImageResource(iconStart);
//            if (enableProgress) {
//                progressBar.setVisibility(GONE);
//            }
//            clicked=false;
//            if (getContext()!=null){
//                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//            reset();
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public void OnGetSpeechSubString(String txt, String langCode) {
//        try {
//            newTextToSpeech.startSpeech(getContext(), txt,langCode,this);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void reset(){
//        try {
//            if (newTextToSpeech!=null)
//
//                newTextToSpeech.reset();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public boolean isPlaying(){
//        if (newTextToSpeech!=null){
//            return newTextToSpeech.isPlaying();
//        }else {
//            return false;
//        }
//    }
//
//    @Override
//    public void onDestroyed() {
//        try {
//            reset();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onPaused() {
//        try {
//
//            reset();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onResume() {
//    }
//}
