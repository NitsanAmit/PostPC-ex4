package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.util.concurrent.TimeUnit;

public class CalculateRootsService extends IntentService {

  public static final String FOUND_ROOTS_ACTION = "found_roots";
  public static final String FOUND_ROOTS_FAILURE_ACTION = "found_roots_failure";

  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;

    long timeoutInMiliseconds;
    if (BuildConfig.DEBUG) {
      timeoutInMiliseconds = 200;
    } else {
      timeoutInMiliseconds = TimeUnit.SECONDS.toMillis(20);
    }

    long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }

    for (long i = 2; i < numberToCalculateRootsFor / 2; i++) {
      long elapsedTime = System.currentTimeMillis() - timeStartMs;
      if (numberToCalculateRootsFor % i == 0) {
        Intent rootsIntent = new Intent(FOUND_ROOTS_ACTION);
        rootsIntent.putExtra("original_number", numberToCalculateRootsFor);
        rootsIntent.putExtra("root1", i);
        rootsIntent.putExtra("root2", numberToCalculateRootsFor / i);
        rootsIntent.putExtra("calculation_time", elapsedTime);
        rootsIntent.putExtra("is_prime", false);
        this.sendBroadcast(rootsIntent);
        return;
      }
      if (elapsedTime >= timeoutInMiliseconds) {
        Intent failureIntent = new Intent(FOUND_ROOTS_FAILURE_ACTION);
        failureIntent.putExtra("original_number", numberToCalculateRootsFor);
        failureIntent.putExtra("time_until_give_up_seconds", elapsedTime);
        this.sendBroadcast(failureIntent);
      }
    }
    long elapsedTime = System.currentTimeMillis() - timeStartMs;
    Intent rootsIntent = new Intent(FOUND_ROOTS_ACTION);
    rootsIntent.putExtra("original_number", numberToCalculateRootsFor);
    rootsIntent.putExtra("calculationTime", elapsedTime);
    rootsIntent.putExtra("is_prime", true);
    this.sendBroadcast(rootsIntent);
  }
}