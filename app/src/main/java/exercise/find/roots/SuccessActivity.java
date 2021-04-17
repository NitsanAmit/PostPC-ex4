package exercise.find.roots;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        long originalNumber = extras.getLong("original_number");
        long calculationTime = extras.getLong("calculation_time");
        TextView calculationTimeTextView = findViewById(R.id.text_calculation_time);
        TextView inputTextView = findViewById(R.id.text_input);
        TextView resultTextView = findViewById(R.id.text_result);
        inputTextView.setText(String.format(getString(R.string.text_input), originalNumber));
        calculationTimeTextView.setText(String.format(getString(R.string.text_calc_time), calculationTime));

        if (extras.getBoolean("is_prime")) {
            resultTextView.setText(getString(R.string.text_is_prime));
        } else {
            long root1 = extras.getLong("root1");
            long root2 = extras.getLong("root2");
            resultTextView.setText(String.format(getString(R.string.text_calc_result), root1, root2));
        }
    }

}