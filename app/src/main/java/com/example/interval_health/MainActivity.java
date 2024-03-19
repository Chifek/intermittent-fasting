package com.example.interval_health;

import java.util.Date;
import java.util.Random;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.SystemClock;
import android.app.AlertDialog;
import android.widget.TextView;
import android.os.CountDownTimer;
import java.text.SimpleDateFormat;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final long TIMER_DURATION = 16 * 60 * 60 * 1000;
    private CountDownTimer timer;
    private Button startButton;
    private Button stopButton;
    private TextView timerTextView;
    private TextView endTimeTextView;
    private long timeLeftInMillis = TIMER_DURATION;
    private long endTimeMillis; // Добавляем переменную для хранения времени завершения
    private SharedPreferences sharedPreferences;
    private static final String TIMER_PREFS = "TimerPrefs";
    private static final String TIME_LEFT_KEY = "timeLeft";
    private static final String TIMER_RUNNING_KEY = "timerRunning";
    private static final String LAST_TIMER_UPDATE_KEY = "lastTimerUpdate";
    private static final String END_TIME_KEY = "endTime"; // Ключ для сохранения времени завершения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        timerTextView = findViewById(R.id.timerTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        sharedPreferences = getSharedPreferences(TIMER_PREFS, MODE_PRIVATE);

        View rootView = findViewById(android.R.id.content); // Получите корневой вид вашей активности
        String text = getRandomHelloText();
        Snackbar snackbar = Snackbar.make(rootView, text,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        // Восстановление состояния таймера при запуске приложения
        timeLeftInMillis = sharedPreferences.getLong(TIME_LEFT_KEY, TIMER_DURATION);
        long lastTimerUpdate = sharedPreferences.getLong(LAST_TIMER_UPDATE_KEY, 0);
        boolean timerRunning = sharedPreferences.getBoolean(TIMER_RUNNING_KEY, false);
        endTimeMillis = sharedPreferences.getLong(END_TIME_KEY, 0); // Восстановление времени завершения

        if (timerRunning) {
            long elapsedTime = SystemClock.elapsedRealtime() - lastTimerUpdate;
            timeLeftInMillis -= elapsedTime;
            if (timeLeftInMillis <= 0) {
                timeLeftInMillis = 0;
                updateTimerText(0);
            } else {
                startTimer(timeLeftInMillis);
            }
        } else {
            updateTimerText(timeLeftInMillis);
        }

        if (endTimeMillis > 0) {
            // Если время завершения было сохранено, отобразить его
            showEndTime();
        }
    }

    private void startTimer() {
        if (endTimeMillis == 0) {
            // Если время завершения еще не было вычислено, вычислить и сохранить
            endTimeMillis = System.currentTimeMillis() + TIMER_DURATION;
            saveEndTime(endTimeMillis);
        }
        startTimer(timeLeftInMillis);
    }

    private String getRandomEndText()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(4);
        String text;
        switch (randomNumber) {
            case 0:
                text = "В следующий раз вломаю телефон! \uD83E\uDD28";
                break;
            case 1:
                text = "Вычислю по IP \uD83D\uDCF1";
                break;
            case 2:
                text = "Ну ты даёшь!? \uD83D\uDE21";
                break;
            case 3:
                text = "Неожидал от тебя такого \uD83D\uDE22";
                break;
            default:
                text = "Неожиданный поворот событий \uD83D\uDE21";
                break;
        }

        return text;
    }

    private String getRandomHelloText()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(3);
        String text;
        switch (randomNumber) {
            case 0:
                text = "Красава! Рус верит в тебя! \uD83D\uDE4C";
                break;
            case 1:
                text = "Вычисляю по IP \uD83E\uDD13";
                break;
            case 2:
                text = "Удачи в похудении! \uD83E\uDEE1";
                break;
            default:
                text = "Неожиданный поворот событий \uD83D\uDE21";
                break;
        }

        return text;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ты чего это? \uD83D\uDE31 Уверен, что хочешь завершить таймер?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Пользователь нажал "Да", остановить таймер
                if (timer != null) {
                    timer.cancel();
                    timeLeftInMillis = TIMER_DURATION;
                    startButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.GONE);
                    updateTimerText(TIMER_DURATION);
                    endTimeTextView.setText(""); // Скрыть время завершения таймера при остановке
                    saveTimerState(TIMER_DURATION, false);
                    endTimeMillis = 0; // Сбрасываем время завершения
                    String text = getRandomEndText();
                    View rootView = findViewById(android.R.id.content); // Получите корневой вид вашей активности
                    Snackbar snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        builder.setNegativeButton("Нет, еще держусь", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Пример использования Snackbar
                View rootView = findViewById(android.R.id.content); // Получите корневой вид вашей активности
                String text = getRandomHelloText();
                Snackbar snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT);
                snackbar.show();
                // Пользователь нажал "Нет", продолжить таймер
                startTimer(timeLeftInMillis);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText(millisUntilFinished);
                showEndTime();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimerText(0);
                saveTimerState(0, false);
                endTimeMillis = 0; // Сбрасываем время завершения
            }
        };

        timer.start();
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);

        // Сохранение состояния таймера при его запуске
        saveTimerState(timeLeftInMillis, true);
    }

    private void stopTimer() {
        // Проверка, прошел ли таймер
        if (timeLeftInMillis > 0) {
            showConfirmationDialog();
        } else {
            if (timer != null) {
                timer.cancel();
                timeLeftInMillis = TIMER_DURATION;
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                updateTimerText(TIMER_DURATION);
                endTimeTextView.setText(""); // Скрыть время завершения таймера при остановке
                saveTimerState(TIMER_DURATION, false);
                endTimeMillis = 0; // Сбрасываем время завершения
            }
        }
    }

    private void updateTimerText(long millisUntilFinished) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void showEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date endDate = new Date(endTimeMillis);
        String endTimeFormatted = sdf.format(endDate);
        endTimeTextView.setText("Можно есть после: " + endTimeFormatted);
    }

    private void saveTimerState(long timeLeft, boolean timerRunning) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TIME_LEFT_KEY, timeLeft);
        editor.putBoolean(TIMER_RUNNING_KEY, timerRunning);
        editor.putLong(LAST_TIMER_UPDATE_KEY, SystemClock.elapsedRealtime());
        editor.apply();
    }

    private void saveEndTime(long endTimeMillis) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(END_TIME_KEY, endTimeMillis);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Отменить таймер при уничтожении активности (например, приложение закрыто)
        if (timer != null) {
            timer.cancel();
        }
    }
}
