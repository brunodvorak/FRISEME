package com.example.frifittracker.tracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.R

/**
 * Activity for managing body tracker data.
 */
class BodyTrackerActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: TrackerAdapterClass
    private lateinit var bodyTrackerViewModel: BodyTrackerActivityViewModel


    /**
     * Initializes the activity and sets up the layout, toolbar, and RecyclerView.
     * Loads the tracker values from the database.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_tracker)

        layoutManager = LinearLayoutManager(this)
        adapter = TrackerAdapterClass(this)
        val trackerItemsList: RecyclerView = findViewById(R.id.tracker_item_list)
        trackerItemsList.layoutManager = layoutManager //spravuje pozície itemov
        trackerItemsList.adapter = adapter // spravuje inputy

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.tracker_Toolbar)
        setSupportActionBar(toolbar)

        bodyTrackerViewModel = ViewModelProvider(this).get(BodyTrackerActivityViewModel::class.java)
        ViewModelProvider(this).get(BodyTrackerActivityViewModel::class.java)
        if(savedInstanceState == null) {
            bodyTrackerViewModel.loadValues(this@BodyTrackerActivity) { loadedTrackerItems ->
                adapter.setBodyPartsList(ArrayList(loadedTrackerItems))
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Saves the current state of the activity into the provided Bundle.
     * Saves the state of the adapter by calling its onSaveInstanceState() method.
     * The saved adapter state is stored in the Bundle with the key "adapter".
     *
     * @param outState The Bundle to save the activity state into.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("adapter", adapter.onSaveInstanceState())
    }

    /**
     * Restores the previously saved state of the activity from the provided Bundle.
     * Restores the state of the adapter by calling its onRestoreInstanceState() method,
     * passing the saved adapter state retrieved from the Bundle with the key "adapter".
     *
     * @param savedInstanceState The Bundle containing the saved activity state.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        adapter.onRestoreInstanceState(savedInstanceState.getParcelable("adapter"))
    }

    /**
     * Handles the selection of menu items.
     * If the selected item has the ID R.id.save_values_button,
     * the saveValues() method is called to save the values.
     *
     * @param item The selected MenuItem.
     * @return true if the item is handled, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_values_button) saveValues()
        return true
    }

    /**
     * Inflates the menu layout specified by R.menu.tracker_menu into the provided Menu.
     * This populates the menu items in the toolbar.
     *
     * @param menu The Menu to inflate the menu layout into.
     * @return true to display the menu, false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.tracker_menu, menu)
        return true
    }

    /**
     * Saves the values from the adapter's body parts list using the BodyTrackerViewModel,
     * and finishes the activity.
     */
    private fun saveValues() {
        val trackerList = adapter.getBodyPartsList()
        bodyTrackerViewModel.saveValues(trackerList, this@BodyTrackerActivity)
        finish()
    }
}