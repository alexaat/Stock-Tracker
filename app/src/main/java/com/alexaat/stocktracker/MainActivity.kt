package com.alexaat.stocktracker


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.alexaat.stocktracker.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

private lateinit var drawerLayout: DrawerLayout
private lateinit var appBarConfiguration: AppBarConfiguration




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment)
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navDrawer,navController)
        navController.addOnDestinationChangedListener{nc,destination,_->
            if(destination.id == nc.graph.startDestination){
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }else{
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }






}
