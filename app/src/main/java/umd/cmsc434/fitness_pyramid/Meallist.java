package umd.cmsc434.fitness_pyramid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by ahmedinibrahim on 3/23/17.
 */

public class Meallist extends ArrayAdapter<Meal> {

    private Activity context;
    private List<Meal> mealList;

    public Meallist(Activity context, List<Meal> mealList){
        super(context, R.layout.activity_meallist, mealList);
        this.context = context;
        this.mealList = mealList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View meallistview = inflater.inflate(R.layout.activity_meallist, null, true);

        TextView textViewMealName = (TextView) meallistview.findViewById(R.id.textViewMealName);
        TextView textViewMealCal = (TextView) meallistview.findViewById(R.id.textViewMealCal);

        Meal meal = mealList.get(position);
        textViewMealName.setText(meal.getMealName());
        textViewMealCal.setText(String.valueOf(meal.getMealCalories()));

        return meallistview;
    }
}
