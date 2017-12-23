package net.ddns.kennhuang.christmastree;

import android.speech.tts.TextToSpeech;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by user on 12/14/2017.
 */

public class PresentEngine implements Runnable {
    private LightsControl server;
    private TextToSpeech ttsTW, ttsEng;
    private PresentDataBase pdb;
    private boolean run = false;
    private final Object sync = new Object();
    private final int lightNum = 4;
    private boolean autoSpeak;

    private long prevTime;

    private int currentStage = 0;
    private boolean firstRun = true;
    private int groupMode;
    private PresentData[] data = null;

    private String[] idleString = {"歡迎來到高二乙的聖誕樹，我不會跟你們說，我們有語音介紹唷", "歡迎來到高二乙的聖誕樹，我們有語音介紹唷"};

    private CharSequence startup = "高二乙聖誕樹語音介紹";

    public PresentEngine() {
        server = new LightsControl(8081);
        pdb = new PresentDataBase(MainActivity.getContext());
//        pdb.init();
        currentStage = 0; // set to Idle
        groupMode = 0;
        autoSpeak = true;
    }

    @Override
    public void run() {
        if (firstRun) {
            Thread st = new Thread(server);
            st.setName("LightsControl");
            st.start();
            createLanguageTTS();
        }
        while (!run) ;
        System.out.println("PresentEngine init");
        ttsTW.speak(startup, TextToSpeech.QUEUE_FLUSH, null, "startup");
        prevTime = System.nanoTime();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("PresentEngine started.");
        while (true) {
            if (currentStage == 0) {
                if (ttsTW.isSpeaking() || ttsEng.isSpeaking()) continue;
                MainActivity.status = "Idle";
                MainActivity.msg = " ";
                server.setLightsMode("1,1,1,1,1,1");

                Calendar c = Calendar.getInstance();
                if (autoSpeak && System.nanoTime() - prevTime > 60 * Math.pow(10, 9) && c.get(Calendar.HOUR_OF_DAY) < 21 && c.get(Calendar.HOUR_OF_DAY) > 6) {
                    Random ran = new Random();
                    speak(idleString[(ran.nextInt(100) + 1) % idleString.length], 0);
                    prevTime = System.nanoTime();
                }

            } else if (currentStage == -1) {
                MainActivity.status = "No Present Data";
            } else if (currentStage == -2) {
                MainActivity.status = "Check";
                System.out.println("Checking DB for mode " + groupMode);
                data = pdb.getDataByGroup(groupMode);
                if (data.length == 0) {
                    currentStage = -1;
                    System.out.println("No Present Data.");
                } else {
                    currentStage = 1;
                }
            } else {
                prevTime = System.nanoTime();
                if (!ttsTW.isSpeaking() || !ttsEng.isSpeaking()) {
                    MainActivity.status = "Stage " + currentStage;

                    int stage = currentStage;
                    if (stage - 1 >= 0) {
                        speak(data[stage - 1].speak, getLanguage(data[stage - 1].group));
                        MainActivity.msg = data[stage - 1].speak.toString();

                        server.setLightsMode(data[stage - 1].mode);
                        if (data[stage - 1].speak.toString().contains("。")) {
                            while (ttsTW.isSpeaking() || ttsEng.isSpeaking()) ;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    currentStage = stage;

                    if (currentStage >= data.length) {
                        System.out.println("Mode: " + groupMode + " Present end.");
                        currentStage = 0;
                    } else if (currentStage != 0) {
                        currentStage++;
                    }
                }

            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLanguageTTS() {
        if (ttsTW == null) {
            ttsTW = new TextToSpeech(MainActivity.getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int arg0) {
                    if (arg0 == TextToSpeech.SUCCESS) {
                        Locale l = Locale.TAIWAN;
                        if (ttsTW.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            ttsTW.setLanguage(l);
                        } else {
                            System.out.println(l.toLanguageTag() + " is not available");
                        }

                        ttsTW.setSpeechRate((float) 0.95);
                        run = true;
                    }
                }
            }
            );
        }

        if (ttsEng == null) {
            ttsEng = new TextToSpeech(MainActivity.getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int arg0) {
                    if (arg0 == TextToSpeech.SUCCESS) {
                        Locale l = Locale.US;
                        if (ttsEng.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            ttsEng.setLanguage(l);
                        } else {
                            System.out.println(l.toLanguageTag() + " is not available");
                        }

                        ttsEng.setSpeechRate((float) 0.9);
                        run = true;
                    }
                }
            }
            );
        }
    }

    public int getLanguage(int group) {
        switch (group) {
            case 2:
                return 1;
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    public void startPresent(int group) {
        if (currentStage != 0) {
            System.out.println("Present had started");
            return;
        }
        groupMode = group;
        currentStage = -2;
    }

    public void stopAll() {
        ttsTW.speak("", TextToSpeech.QUEUE_FLUSH, null, "clear");
        ttsEng.speak("", TextToSpeech.QUEUE_FLUSH, null, "clear");
        currentStage = 0;
    }

    private void speak(CharSequence speak, int language) {
        if (language == 0) {
            ttsTW.speak(speak, TextToSpeech.QUEUE_ADD, null, "TWpresent");
        } else {
            ttsEng.speak(speak, TextToSpeech.QUEUE_ADD, null, "ENGpresent");
        }
    }

    public void setAutoSpeak(boolean b) {
        autoSpeak = b;
    }
}
