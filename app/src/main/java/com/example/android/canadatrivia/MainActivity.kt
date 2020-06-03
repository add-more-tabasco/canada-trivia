/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.canadatrivia


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.android.canadatrivia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration : AppBarConfiguration
    //for logging:
    val tag: String = "MainActivity"
    //needed for workaround to change dark theme:
    private var darkBtnClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //@Suppress("UNUSED_VARIABLE")

        //use data binding to get reference to views
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //enable night mode by device settings
        if (savedInstanceState != null && savedInstanceState["DARK_BTN_CLICKED"] != true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Log.e(tag,"changed to system default in oncreate")
        } //
        val darkModeToggleButton = findViewById<ToggleButton>(R.id.darkModeToggle)//*****************
        //check current mode
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        //set dark mode switch ON if already so (todo: move to ViewModel)
        fun setInitialToggle() {
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                if (darkModeToggleButton != null){
                darkModeToggleButton.isChecked = true
                }
            }
        }
        setInitialToggle()
    }

    //enable users to override device dark mode setting
    fun changeMode(View: View) {
        //check current mode
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        //prevent AppCompatDelegate from resetting the theme in onCreate:
        darkBtnClicked = true
        //switch it up
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        Log.e(tag, "mode change$darkBtnClicked")
    }

    /** This callback is called only when there is a saved instance that is previously saved by using
    // onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().*/
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            darkBtnClicked = savedInstanceState.getBoolean("DARK_BTN_CLICKED")
        }
        Log.e(tag, "onRestore $darkBtnClicked")
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putBoolean("DARK_BTN_CLICKED", darkBtnClicked)
        }
        Log.e(tag, "onSave $outState")

        //save toggle button status (todo: implement in ViewModel or fix data binding)
        val darkModeToggleButton = findViewById<ToggleButton>(R.id.darkModeToggle)
        if (darkModeToggleButton != null){
            outState.run {
                putString("toggleCheck", darkModeToggleButton.isChecked.toString())
            }
        }
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
