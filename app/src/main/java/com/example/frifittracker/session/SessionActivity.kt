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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.R

/**
 * Activity for managing the sessions.
 */
class SessionActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: SessionAdapterClass
    private lateinit var exeWindow: Dialog
    private lateinit var passedDate: String
    private lateinit var sessionActivityViewModel: SessionActivityViewModel

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
        sessionActivityViewModel = ViewModelProvider(this).get(SessionActivityViewModel::class.java)
        val sessionItemsList: RecyclerView = findViewById(R.id.sessionItemsList)
        //spravuje pozície itemov
        sessionItemsList.layoutManager = layoutManager
        // spravuje inputy
        sessionItemsList.adapter = adapter


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar?>(R.id.newSessionToolbar)
        toolbar.title = passedDate

        //Priradí toolbar k layoutu
        setSupportActionBar(toolbar)

        if(savedInstanceState == null) {
            val change = intent.getBooleanExtra("change", false)
            if (change) {
                loadExercises()
            }
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
        adapter.notifyDataSetChanged()
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
     * Saves the new exercises to the database and finishes the current activity.
     *
     * @param passedDate The date associated with the session.
     */
    private fun saveNewExe() {
        val exerciseList = adapter.getExerciseList()

        sessionActivityViewModel.saveExercises(this@SessionActivity, exerciseList, passedDate) {
            Toast.makeText(this@SessionActivity, "Tréning uložený!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Loads exercises from the database and updates the UI with the retrieved data.
     */
    private fun loadExercises() {
        sessionActivityViewModel.loadExercises(this@SessionActivity, passedDate) { sessionItems ->
            adapter.setExerciseList(ArrayList(sessionItems))
            adapter.notifyDataSetChanged()
        }
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

    /**
     * Shows a dialog window for adding a new exercise.
     * The dialog window allows the user to enter details of a new exercise.
     * The user can save the exercise or save and continue adding new exercises.
     * When the user saves the exercise, the dialog window is dismissed.
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
}