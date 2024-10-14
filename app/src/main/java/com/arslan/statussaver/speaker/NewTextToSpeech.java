//package com.devatrii.statussaver.speaker;
//
//import static android.content.Context.CONNECTIVITY_SERVICE;
//
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//
//
//public class NewTextToSpeech {
//    private final MediaPlayer mediaPlayer;
//    private String completeText;
//    private OnTextToSpeechListener mListener;
//    private String mLangCode;
//    private static NewTextToSpeech speech;
//    private static class holder{
//        private static final NewTextToSpeech speech=new NewTextToSpeech();
//    }
//
//    private NewTextToSpeech(){
//        mediaPlayer=new MediaPlayer();
//    }
//    public static NewTextToSpeech newInstance(){
//        if (speech==null)
//            speech=new NewTextToSpeech();
//        return speech;
//    }
//    public void startSpeech(Context context, String text, String langCode, OnTextToSpeechListener listener){
//
//        mLangCode=langCode;
//        completeText=text;
//        if (text.length() > 154) {
//           String subTxt= completeText.substring(0, 154);
//            completeText=completeText.substring(155);
//            prepare_for_speech(context, subTxt, langCode,listener);
//        } else {
//            prepare_for_speech(context, text, langCode,listener);
//            completeText=null;
//        }
//    }
//    private void prepare_for_speech(Context context, String text, String code,OnTextToSpeechListener listener){
//        try {
//
//            mListener=listener;
//
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        if (!isNetworkAvailable(context)) {
//                            listener.OnSpeechError();
//                            return;
//                        }
//                        String Url = "https://translate.google.com/translate_tts?ie=UTF-8";
//                        String pronouce = "&q=" + URLEncoder.encode(text, "UTF-8");
//                        String language = "&tl=" + code;
//                        String web = "&client=tw-ob";
//                        String fullUrl = Url + pronouce + language + web;
//                        Uri uri = Uri.parse(fullUrl);
//                        try {
//                            mediaPlayer.reset();
//                            mediaPlayer.setDataSource(uri.toString());
//                            mediaPlayer.prepareAsync();
//                            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
//                                mediaPlayer.start();
//                                if (listener!=null){
//                                    listener.OnSpeechPrepared(mediaPlayer);
//                                }
//
//                            });
//                            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
//                                if (completeText!=null && completeText.length()>0){
//                                    if (listener!=null)
//                                        listener.OnGetSpeechSubString(completeText,mLangCode);
//                                }else {
//                                    if (listener!=null)
//                                        listener.OnSpeechCompleted(mediaPlayer);
//                                }
//                            });
//
//                            mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
//                                if (listener!=null)
//                                    listener.OnSpeechMediaPlayerError(mediaPlayer,i,i1);
//                                return false;
//                            });
//
//                        }catch (IOException e){
//                            e.printStackTrace();
//                            if (listener!=null)
//                                listener.OnSpeechError();
//                        }
//                    }catch (Exception e){
//
//                    }
//
//                }
//            });
//
//        }catch (Exception e){
//            e.printStackTrace();
//            if (listener!=null)
//                listener.OnSpeechError();
//        }
//
//    }
//    public void reset(){
//        try {
//            if (mediaPlayer!=null)
//                mediaPlayer.stop();
//                mediaPlayer.reset();
//            if (mListener!=null){
//                mListener.OnSpeechReset();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//    public boolean isPlaying(){
//        if (mediaPlayer!=null)
//            return mediaPlayer.isPlaying();
//        else
//            return false;
//    }
//
//    public boolean isNetworkAvailable(Context context) {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
//            NetworkInfo info = cm.getActiveNetworkInfo();
//            return info != null && info.isConnected();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public void setOnTextToSpeechListener(OnTextToSpeechListener listener){
//        mListener=listener;
//    }
//    public  interface   OnTextToSpeechListener{
//        void OnSpeechReset();
//        void OnSpeechPrepared(MediaPlayer player);
//        void OnSpeechCompleted(MediaPlayer player);
//        void OnSpeechMediaPlayerError(MediaPlayer mediaPlayer, int i, int i1);
//        void OnSpeechError();
//        void OnGetSpeechSubString(String txt,String langCode);
//    }
//}
