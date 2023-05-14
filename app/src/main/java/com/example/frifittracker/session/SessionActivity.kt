package com.example.frifittracker.session

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.R
import com.example.frifittracker.session.sessionDatabase.ExerciseEntity
import com.example.frifittracker.session.sessionDatabase.SessionDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for managing the sessions.
 */
class SessionActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: SessionAdapterClass
    private lateinit var exeWindow: Dialog
    private lateinit var passedDate: String

    /**
     * Called when the activity is starting or restarting.
     *
     * @param savedInstanceState The saved instance state Bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        layoutManager = LinearLayoutManager(this)
        exeWindow = Dialog(this)
        passedDate = intent.getStringExtra("date").toString()
        adapter = SessionAdapterClass(passedDate)

        val sessionItemsList: RecyclerView = findViewById(R.id.sessionItemsList)
        //spravuje pozície itemov
        sessionItemsList.layoutManager = layoutManager
        // spravuje inputy
        sessionItemsList.adapter = adapter


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.newSessionToolbar)
        toolbar.title = passedDate

        //Priradí toolbar k layoutu
        setSupportActionBar(toolbar)


        val change = intent.getBooleanExtra("change", false)
        if (change) {
            loadExercises()
        }

    }

    /**
     * Called to save the current instance state.
     *
     * @param outState The Bundle to save the instance state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("isDialogShown", exeWindow.isShowing)

        if (exeWindow.isShowing) {
            outState.putString(
                "exeName",
                exeWindow.findViewById<EditText>(R.id.exe_name_input).text.toString()
            )

            val exeSets = exeWindow.findViewById<EditText>(R.id.num_of_sets_input).text.toString()
                .toIntOrNull()
            if (exeSets != null) outState.putInt("exeSets", exeSets)

            val exeReps = exeWindow.findViewById<EditText>(R.id.num_of_rep_input).text.toString()
                .toIntOrNull()
            if (exeReps != null) outState.putInt("exeReps", exeReps)

            val weight = exeWindow.findViewById<EditText>(R.id.exe_weight_input).text.toString()
                .toIntOrNull()
            if (weight != null) outState.putInt("weight", weight)
        }

        outState.putParcelable("adapter", adapter.onSaveInstanceState())
    }

    /**
     * Called to restore the previously saved instance state.
     *
     * @param savedInstanceState The Bundle containing the saved instance state.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val isDialogShown = savedInstanceState.getBoolean("isDialogShown")
        if (isDialogShown) {
            showDialogWindow()
            exeWindow.findViewById<EditText>(R.id.exe_name_input)
                .setText(savedInstanceState.getString("exeName"))

            val exeSets = savedInstanceState.getInt("exeSets")
            if (exeSets != 0) exeWindow.findViewById<EditText>(R.id.num_of_sets_input)
                .setText(exeSets.toString())

            val exeReps = savedInstanceState.getInt("exeReps")
            if (exeReps != 0) exeWindow.findViewById<EditText>(R.id.num_of_rep_input)
                .setText(exeReps.toString())

            val weight = savedInstanceState.getInt("weight")
            if (weight != 0) exeWindow.findViewById<EditText>(R.id.exe_weight_input)
                .setText(weight.toString())
        }

        adapter.onRestoreInstanceState(savedInstanceState.getParcelable("adapter"))
    }

    /**
     * Called when a menu item is selected.
     *
     * @param item The selected menu item.
     * @return `true` if the menu item selection is handled, `false` otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_session_button -> saveNewExe()
            R.id.add_exe_button -> showDialogWindow()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Saves the new exercise to the database.
     * If the exercise list is empty, finishes the activity.
     */
    private fun saveNewExe() {
        if (adapter.getExerciseList().isEmpty()) {
            finish()
        }

        val exerciseList = adapter.getExerciseList()

        val exerciseEntities = exerciseList.map { exercise ->
            ExerciseEntity(
                exeName = exercise.getExeName(),
                sets = exercise.getnumOfSets(),
                reps = exercise.getnumOfReps(),
                weight = exercise.getWeight(),
                date = passedDate
            )
        }

        val database = SessionDataDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            // uloží cviky do databázy aj s atribútmi
            exerciseEntities.forEach { entity ->
                database.sessionDataClassDao.insertExercise(entity)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@SessionActivity, "Tréning uložený!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Loads exercises from the database for the current date and updates the UI with the retrieved data.
     * The function retrieves ExerciseEntity objects from the database based on the passedDate.
     * The ExerciseEntity objects are then mapped to SessionItem models.
     * The UI is updated on the main thread by setting the exercise list in the adapter and calling notifyDataSetChanged().
     * After loading the exercises, they are deleted from the database to avoid duplicates.
     */
    private fun loadExercises() {
        val database = SessionDataDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            val exerciseEntities =
                database.sessionDataClassDao.getExercisesByDate(passedDate.toString())

            val exerciseList = exerciseEntities.map { entity ->
                SessionItem(
                    exeName = entity.getExeName(),
                    sets = entity.getnumOfSets(),
                    reps = entity.getnumOfReps(),
                    weight = entity.getWeight()
                )
            }

            withContext(Dispatchers.Main) {
                adapter.setExerciseList(ArrayList(exerciseList))
                adapter.notifyDataSetChanged()
            }

            database.sessionDataClassDao.deleteExercises(passedDate.toString())
        }
    }

    /**
     * Displays a dialog window for adding a new exercise.
     * The function creates a Dialog instance and sets its content view to the new_exe_dialog layout.
     * The dialog window is shown to the user.
     * The function sets click listeners for the close dialog button, save exercise button,
     * and save and continue button.
     * When the close dialog button is clicked, the dialog window is dismissed.
     * When the save exercise button is clicked, the function add a new item to the adapter
     * and dismisses the dialog window .
     * When the save and continue button is clicked, the function add a new item to the adapter.
     */
    private fun showDialogWindow() {
        exeWindow = Dialog(this)
        exeWindow.setContentView(R.layout.new_exe_dialog)
        exeWindow.show()

        val closeDialogListener = exeWindow.findViewById<ImageButton>(R.id.close_dialog_button)
        closeDialogListener.setOnClickListener {
            exeWindow.dismiss()
        }

        val saveExeListener = exeWindow.findViewById<Button>(R.id.save_exe_button)
        saveExeListener.setOnClickListener {
            if (addNewItem()) {
                exeWindow.dismiss()
            }

        }


        val saveAndContinueListener = exeWindow.findViewById<Button>(R.id.continue_exe_button)
        saveAndContinueListener.setOnClickListener {
            addNewItem()
        }

    }

    /**
     * Adds a new item to the adapter based on the input values from the dialog window.
     * The function retrieves the exercise name, number of sets, number of reps, and weight
     * from the corresponding input fields in the dialog window.
     * It performs validation checks on the input values and displays Toast messages for any errors.
     * If all input values are valid, a new SessionItem object is created with the input values.
     * The new item is added to the adapter, and the adapter is notified of the item insertion.
     * The function returns true if the new item was successfully added, or false if there were validation errors.
     */
    private fun addNewItem(): Boolean {

        val exeName = exeWindow.findViewById<EditText>(R.id.exe_name_input).text.toString()
        val exeSets =
            exeWindow.findViewById<EditText>(R.id.num_of_sets_input).text.toString().toIntOrNull()
        val exeReps =
            exeWindow.findViewById<EditText>(R.id.num_of_rep_input).text.toString().toIntOrNull()
        val weight =
            exeWindow.findViewById<EditText>(R.id.exe_weight_input).text.toString().toIntOrNull()

        if (exeName.isEmpty()) {
            Toast.makeText(this, "Zadaj názov cviku!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (exeSets == null) {
            Toast.makeText(this, "Zadaj počet sérií!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (exeReps == null) {
            Toast.makeText(this, "Zadaj počet opakovaní!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (weight == null) {
            Toast.makeText(this, "Zadaj váhu!", Toast.LENGTH_SHORT).show()
            return false
        }

        val exercise = SessionItem(exeName, exeSets, exeReps, weight)
        adapter.addExercise(exercise)
        adapter.notifyItemInserted(adapter.itemCount - 1)
        return true
    }

    /**
     * Initializes the options menu for the activity.
     * Inflates the specified menu resource file, which defines the items to be shown in the menu.
     * The inflated menu is passed as a parameter to the function.
     * The function returns true to indicate that the menu has been successfully initialized.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.session_menu, menu)
        return true
    }

}