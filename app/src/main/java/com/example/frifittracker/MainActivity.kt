package com.example.frifittracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.session.SessionAdapterClass
import java.text.SimpleDateFormat
import java.util.*

/**
 * The main activity of the application.
 */
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val calendar = Calendar.getInstance()
    private var adapter = SessionAdapterClass("")
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var mainViewModel: MainActivityViewModel

    private var alertDialog: AlertDialog? = null

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

        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
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

        if(alertDialog?.isShowing == true) outState.putBoolean("isShowing", true)
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

        val date = findViewById<TextView>(R.id.sessionDateText).text.toString()
        val isShowing = savedInstanceState.getBoolean("isShowing")
        if (isShowing) showAlertDialog(date)
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

    /**
     * Callback method that is invoked when the options menu is being created.
     *
     * @param menu The options menu in which items are placed.
     * @return true to display the menu, false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.add_menu, menu)
        return true
    }

    /**
     * Shows an AlertDialog with the specified message and button actions.
     *
     * @param date The date to be passed to the button actions.
     */
    private fun showAlertDialog(date: String) {
        alertDialog = AlertDialog.Builder(this)
            .setMessage("Vytvorenie nového tréningu prepíše uložený tréning, chcete pokračovať?")
            .setPositiveButton("Pokračovať") { dialog, _ ->
                mainViewModel.sessionActivitySwitch(false, this@MainActivity, date)
                dialog.dismiss()
            }
            .setNeutralButton("Upraviť uložený tréning") { dialog, _ ->
                mainViewModel.sessionActivitySwitch(true, this@MainActivity, date)
                dialog.dismiss()
            }
            .setNegativeButton("Zavrieť") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog?.show()
    }

    /**
     * Creates the options menu for the activity.
     *
     * @param item Item from itemMenu.
     * @return true for the menu to be displayed, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val date = findViewById<TextView>(R.id.sessionDateText).text.toString()

        when (item.itemId) {
            R.id.save_session_button -> if (adapter.getExerciseList().isNotEmpty()) {
                showAlertDialog(date)
            } else mainViewModel.sessionActivitySwitch(false, this@MainActivity, date)
            R.id.change_training -> mainViewModel.sessionActivitySwitch(
                true,
                this@MainActivity,
                date
            )
            R.id.params -> mainViewModel.trackerActivitySwitch(this@MainActivity)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Loads exercises from the database based on the selected date and updates the UI with the loaded data.
     */
    private fun loadExercises() {
        val date = findViewById<TextView>(R.id.sessionDateText).text.toString()
        mainViewModel.loadExercises(this@MainActivity.applicationContext, date) { exerciseList ->
            adapter.setExerciseList(ArrayList(exerciseList))
            adapter.notifyDataSetChanged()
        }
    }

}