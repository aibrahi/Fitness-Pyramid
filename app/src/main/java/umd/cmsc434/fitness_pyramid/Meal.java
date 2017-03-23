package umd.cmsc434.fitness_pyramid;

/**
 * Created by ahmedinibrahim on 3/22/17.
 */

public class Meal {

    private String mealID;
    private String mealName;
    private int mealCalories;

    public Meal (){

    }

    public Meal (String mealID, String mealName, int mealCalories){
        this.mealID = mealID;
        this.mealName = mealName;
        this.mealCalories = mealCalories;
    }


    public String getMealID() {
        return mealID;
    }

    public String getMealName() {
        return mealName;
    }

    public int getMealCalories() {
        return mealCalories;
    }
}
