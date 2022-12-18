package com.app.cinema.utills

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object SharedPrefsHelper {

    fun saveData(activity: Activity, historyList: ArrayList<String>) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        val sharedPreferences = activity.getSharedPreferences(
            "shared preferences",
            AppCompatActivity.MODE_PRIVATE
        )

        // creating a variable for editor to
        // store data in shared preferences.
        val editor = sharedPreferences.edit()

        // creating a new variable for gson.
        val gson = Gson()

        // getting data from gson and storing it in a string.
        val json = gson.toJson(historyList)

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("history", json)

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply()
    }

    fun loadData(activity: Activity): ArrayList<String> {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        val sharedPreferences = activity.getSharedPreferences(
            "shared preferences",
            AppCompatActivity.MODE_PRIVATE
        )

        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences.getString("history", null)

        // below line is to get the type of our array list.
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type

        var historyArrayList: ArrayList<String>? =
            gson.fromJson<Any>(json, type) as ArrayList<String>?

        // checking below if the array list is empty or not
        if (historyArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            historyArrayList = ArrayList()
        }

        // in below line we are getting data from gson
        // and saving it to our array list
        return historyArrayList
    }
}