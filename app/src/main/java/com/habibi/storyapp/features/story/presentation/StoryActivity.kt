package com.habibi.storyapp.features.story.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.habibi.storyapp.R
import com.habibi.storyapp.databinding.ActivityStoryBinding
import com.habibi.storyapp.features.story.presentation.add.StoryAddFragment
import com.habibi.storyapp.features.story.presentation.detail.StoryDetailFragment
import com.habibi.storyapp.features.story.presentation.list.StoryListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityStoryBinding

    private val viewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel.getUserName()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_language -> {
                showDialogChooseLanguage()
                true
            }
            R.id.action_account -> {
                showDialogProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialogChooseLanguage() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.language)
            .setSingleChoiceItems(
                arrayOf(
                    getString(R.string.indonesia),
                    getString(R.string.english)
                ),
                0
            ) { _, position ->
                Log.i("thiiis", "showDialogProfile: $position")
            }
            .setPositiveButton(R.string.choose) { _, _ ->
                Log.i("h", "showDialogProfile: ")
            }
            .setNegativeButton(R.string.cancel) {_, _ ->

            }
            .show()
    }

    private fun showDialogProfile() {
        MaterialAlertDialogBuilder(this)
            .setTitle(viewModel.userName)
            .setIcon(R.drawable.ic_person)
            .setPositiveButton(R.string.logout) { _, _ ->
                goToLogin()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                Log.i("h", "showDialogProfile: ")
            }
            .show()
    }

    private fun goToLogin() {
        val extras = ActivityNavigator.Extras.Builder()
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .build()

        when (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)?.childFragmentManager?.fragments?.get(0)) {
            is StoryListFragment -> {
                viewModel.setUserNotLogin()
                navController.navigate(
                    resId = R.id.action_StoryListFragment_to_authenticationActivity,
                    args = null,
                    navOptions = null,
                    navigatorExtras = extras
                )
            }
            is StoryDetailFragment -> {
                viewModel.setUserNotLogin()
                navController.navigate(
                    resId = R.id.action_StoryDetailFragment_to_authenticationActivity,
                    args = null,
                    navOptions = null,
                    navigatorExtras = extras
                )
            }
            is StoryAddFragment -> {
                viewModel.setUserNotLogin()
                navController.navigate(
                    resId = R.id.action_storyAddFragment_to_authenticationActivity,
                    args = null,
                    navOptions = null,
                    navigatorExtras = extras
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}