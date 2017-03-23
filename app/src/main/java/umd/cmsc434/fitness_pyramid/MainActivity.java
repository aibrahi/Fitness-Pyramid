package umd.cmsc434.fitness_pyramid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mealName;
    private EditText mealCal;
    private Button addMealButton;

    DatabaseReference databaseMeal;

    private ListView listViewMeal;
    private List<Meal> mealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseMeal = FirebaseDatabase.getInstance().getReference("meal");

        mealName = (EditText) findViewById(R.id.mealName);
        mealCal = (EditText) findViewById(R.id.mealCalories);
        addMealButton= (Button) findViewById(R.id.addMealButton);

        listViewMeal = (ListView) findViewById(R.id.listViewMeal);
        mealList = new ArrayList<>();

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


    @Override
    protected void onStart() {
        super.onStart();

        databaseMeal.addValueEventListener( new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealList.clear();

                for(DataSnapshot mealSnapshot : dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);

                    mealList.add(meal);
                }

                Meallist adapter = new Meallist(MainActivity.this, mealList);
                listViewMeal.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
