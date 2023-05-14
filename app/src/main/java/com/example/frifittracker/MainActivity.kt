package com.example.frifittracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.session.SessionActivity
import com.example.frifittracker.session.SessionAdapterClass
import com.example.frifittracker.session.SessionItem
import com.example.frifittracker.session.sessionDatabase.SessionDataDatabase
import com.example.frifittracker.tracker.BodyTrackerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * The main activity of the application.
 */
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val calendar = Calendar.getInstance()
    private var adapter = SessionAdapterClass("")
    private lateinit var layoutManager: RecyclerView.LayoutManager

    /**
     * Called when the activity is starting. Performs initialization of the activity,
     * such as inflating the layout, setting up the toolbar, initializing the layout manager
     * and adapter for the RecyclerView, updating the date, and loading exercises.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                            being shut down, this Bundle contains the data it most recently
     *                            supplied. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        layoutManager = LinearLayoutManager(this)
        val sessionItemsList: RecyclerView = findViewById(R.id.actualTraining)
        sessionItemsList.layoutManager = layoutManager //spravuje pozície itemov
        sessionItemsList.adapter = adapter // spravuje inputy

        updateDate()
        loadExercises()


        findViewById<TextView>(R.id.sessionDateText).setOnClickListener {
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        findViewById<ImageButton>(R.id.leftArrowButton).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDate()
            loadExercises()
        }

        findViewById<ImageButton>(R.id.rightArrowButton).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDate()
            loadExercises()
        }
    }

    /**
     * Called when the activity is resumed. Invokes the parent's onResume method and
     * loads exercises.
     */
    override fun onResume() {
        super.onResume()
        loadExercises()
    }

    /**
     * Updates the displayed date in the sessionDateText TextView based on the current date
     * stored in the calendar. Also updates the passed date in the adapter.
     */
    private fun updateDate() {
        val date = findViewById<TextView>(R.id.sessionDateText)
        date.text =
            SimpleDateFormat("d.MMM.yyyy", Locale.getDefault()).format(calendar.timeInMillis)
        adapter.changePassedDate(date.toString())
    }

    /**
     * Called to save the current instance state of the activity. Invokes the parent's
     * onSaveInstanceState method and saves the year, month, and day values from the calendar
     * into the provided bundle.
     *
     * @param outState The bundle to save the instance state into.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        outState.putInt("year", year)
        outState.putInt("month", month)
        outState.putInt("day", day)
    }

    /**
     * Called to restore the previously saved instance state of the activity. Invokes the parent's
     * onRestoreInstanceState method and retrieves the year, month, and day values from the saved bundle.
     * Updates the calendar with the restored date, updates the displayed date, clears the adapter, and
     * loads the exercises for the restored date.
     *
     * @param savedInstanceState The bundle containing the saved instance state.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val year = savedInstanceState.getInt("year")
        val month = savedInstanceState.getInt("month")
        val day = savedInstanceState.getInt("day")

        calendar.set(year, month, day)

        updateDate()
        adapter.clear()
        loadExercises()

    }

    /**
     * Callback method invoked when a date is set in the DatePickerDialog. Updates the calendar with the selected date,
     * loads the exercises for the selected date, and updates the displayed date.
     *
     * @param view The DatePicker view.
     * @param year The selected year.
     * @param month The selected month.
     * @param day The selected day.
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        loadExercises()
        updateDate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.add_menu, menu)
        return true
    }

    /**
     * Inflate the menu resource to create the options menu for the activity.
     *
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_session_button -> sessionActivitySwitch(false)
            R.id.change_training -> sessionActivitySwitch(true)
            R.id.params -> trackerActivitySwitch()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Start the BodyTrackerActivity to switch to the tracker screen.
     */
    private fun trackerActivitySwitch() {
        val intent = Intent(this@MainActivity, BodyTrackerActivity::class.java)
        startActivity(intent)
    }

    /**
     * Start the SessionActivity to switch to the session screen.
     *
     * @param change Flag indicating whether training is being changed or created.
     */
    private fun sessionActivitySwitch(change: Boolean) {
        val intent = Intent(this@MainActivity, SessionActivity::class.java)
        val date = findViewById<TextView>(R.id.sessionDateText).text.toString()
        intent.putExtra("change", change)
        intent.putExtra("date", date)
        startActivity(intent)
    }

    /**
     * Loads exercises from the database based on the selected date and updates the UI with the loaded data.
     */
    private fun loadExercises() {
        val database = SessionDataDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            val exerciseEntities =
                database.sessionDataClassDao.getExercisesByDate(findViewById<TextView>(R.id.sessionDateText).text.toString())

            val exerciseList = exerciseEntities.map { entity ->
                SessionItem(
                    exeName = entity.getExeName(),
                    sets = entity.getnumOfSets(),
                    reps = entity.getnumOfReps(),
                    weight = entity.getWeight()
                )
            }

            withContext(Dispatchers.Main) {
                // Update your adapter or UI elements with the loaded data
                adapter.setExerciseList(ArrayList(exerciseList))
                adapter.notifyDataSetChanged()
            }
        }
    }

}