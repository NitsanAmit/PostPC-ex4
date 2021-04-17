package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import static exercise.find.roots.CalculateRootsService.FOUND_ROOTS_ACTION;
import static exercise.find.roots.CalculateRootsService.FOUND_ROOTS_FAILURE_ACTION;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;
  private Button buttonCalculateRoots;
  private EditText editTextUserInput;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = findViewById(R.id.progressBar);
    editTextUserInput = findViewById(R.id.editTextInputNumber);
    buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        try {
          Long.parseLong(newText);
          buttonCalculateRoots.setEnabled(true);
        } catch (NumberFormatException e) {
          buttonCalculateRoots.setEnabled(false);
        }
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      long userInputLong;
      try {
        userInputLong = Long.parseLong(userInputString);
      } catch (NumberFormatException e) {
        buttonCalculateRoots.setEnabled(false);
        return;
      }
      intentToOpenService.putExtra("number_for_service", userInputLong);
      startService(intentToOpenService);
      buttonCalculateRoots.setEnabled(false);
      editTextUserInput.setEnabled(false);
      progressBar.setVisibility(View.VISIBLE);
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals(FOUND_ROOTS_ACTION))
          return;
        incomingIntent.setClass(MainActivity.this, SuccessActivity.class);
        resetUiStateAfterCalculation();
        startActivity(incomingIntent);
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter(FOUND_ROOTS_ACTION));

    broadcastReceiverForFailure = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals(FOUND_ROOTS_FAILURE_ACTION))
          return;
        Toast.makeText(MainActivity.this, "calculation aborted after 20 seconds", Toast.LENGTH_SHORT).show();
        resetUiStateAfterCalculation();
      }
    };
    registerReceiver(broadcastReceiverForFailure, new IntentFilter(FOUND_ROOTS_FAILURE_ACTION));
  }

  private void resetUiStateAfterCalculation() {
    buttonCalculateRoots.setEnabled(false);
    editTextUserInput.setEnabled(true);
    editTextUserInput.setText("");
    progressBar.setVisibility(View.GONE);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForSuccess);
    this.unregisterReceiver(broadcastReceiverForFailure);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    MainActivityState activityState = new MainActivityState(
            editTextUserInput.getText().toString(),
            editTextUserInput.isEnabled(),
            buttonCalculateRoots.isEnabled(),
            progressBar.getVisibility()
    );
    outState.putSerializable("activity_state", activityState);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    MainActivityState activityState = (MainActivityState) savedInstanceState.getSerializable("activity_state");
    if (activityState == null) return;
    this.editTextUserInput.setText(activityState.editTextValue);
    this.editTextUserInput.setEnabled(activityState.editTextEnabled);
    this.buttonCalculateRoots.setEnabled(activityState.buttonEnabled);
    this.progressBar.setVisibility(activityState.progressVisibility);
  }

  static class MainActivityState implements Serializable {

    String editTextValue;
    boolean editTextEnabled;
    boolean buttonEnabled;
    int progressVisibility;

    public MainActivityState(String editTextValue, boolean editTextEnabled, boolean buttonEnabled, int progressVisibility) {
      this.editTextValue = editTextValue;
      this.editTextEnabled = editTextEnabled;
      this.buttonEnabled = buttonEnabled;
      this.progressVisibility = progressVisibility;
    }
  }

}