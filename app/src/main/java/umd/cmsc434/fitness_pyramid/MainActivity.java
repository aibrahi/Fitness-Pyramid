package umd.cmsc434.fitness_pyramid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText mealName;
    private EditText mealCal;
    private Button addMealButton;

    DatabaseReference databaseMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseMeal = FirebaseDatabase.getInstance().getReference("meal");

        mealName = (EditText) findViewById(R.id.mealName);
        mealCal = (EditText) findViewById(R.id.mealCalories);
        addMealButton= (Button) findViewById(R.id.addMealButton);

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealStr = mealName.getText().toString().trim();
                int mealInt = Integer.parseInt(mealCal.getText().toString());

                if(!TextUtils.isEmpty(mealStr)){
                    String id = databaseMeal.push().getKey();
                    Meal meal = new Meal(id, mealStr, mealInt);
                    databaseMeal.child(id).setValue(meal);


                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "You need to include meal name", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


    }
}
