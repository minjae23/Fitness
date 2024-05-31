package com.example.fitness;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;

import java.util.ArrayList;

public class WeekPlanSystem extends SQLiteOpenHelper {

    WeekPlanSystem wdb;
    SQLiteDatabase db;
    public WeekPlanSystem(Context context) {
        super(context,"exerDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists schedule(plan_num integer PRIMARY KEY AUTOincrement, user_id varchar(20), plan_date var(20), exer_part var(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPlan(SQLiteDatabase db, String user_id, Plan plan) {
        String weekly=plan.getWeekly();
        String exerPart=plan.getExerPartArray();
        String sql="INSERT INTO schedule(user_id, plan_date, exer_part) VALUES('"+user_id+"','"+weekly+"','"+exerPart+"');";
        db.execSQL(sql);
    }

    public ArrayList<Plan> callPlan(SQLiteDatabase db, String user_id)
    {
        Cursor cu;
        int i=0;
        ArrayList<Plan> b = new ArrayList<>();
        cu=db.rawQuery("SELECT plan_date, exer_part From schedule WHERE user_id='"+user_id+"';",null);
        while(cu.moveToNext())
            b.add(new Plan(cu.getString(0),cu.getString(1)));
        return b;
    }

    public void initTable(SQLiteDatabase db, String user_id){
        db.execSQL("DELETE FROM schedule WHERE user_id='"+user_id+"';");
    }

}
