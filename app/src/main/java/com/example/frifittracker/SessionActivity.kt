package com.example.frifittracker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.database.ExerciseEntity
import com.example.frifittracker.database.SessionDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class SessionActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var adapter = AdapterClass()
    private lateinit var exeWindow: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        layoutManager = LinearLayoutManager(this)
        exeWindow = Dialog(this)

        val sessionItemsList: RecyclerView = findViewById(R.id.sessionItemsList)
        sessionItemsList.layoutManager = layoutManager //spravuje pozície itemov
        sessionItemsList.adapter = adapter // spravuje inputy


        //Priradí toolbar k layoutu
        setSupportActionBar(findViewById(R.id.newSessionToolbar))
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_session_button -> saveNewExe()
            R.id.add_exe_button -> showDialogWindow()
            R.id.save_session_button2 -> loadExercises()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNewExe() {
        if (findViewById<EditText>(R.id.sessionNameText).text.toString().isBlank()) {
            Toast.makeText(this, "Zadaj názov tréningu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (adapter.getExerciseList().isEmpty()) {
            Toast.makeText(this, "Tréning je prázdny!", Toast.LENGTH_SHORT).show()
            return
        }

        val exerciseList = adapter.getExerciseList()

        val exerciseEntities = exerciseList.map { exercise ->
            ExerciseEntity(
                exeName = exercise.getExeName(),
                sets = exercise.getnumOfSets(),
                reps = exercise.getnumOfReps(),
                weight = exercise.getWeight()
            )
        }

        val database = SessionDataDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            // Save the exercise entities to the database
            exerciseEntities.forEach { entity ->
                database.sessionDataClassDao.insertExercise(entity)
            }

            // Show a success message or perform any other necessary actions on the main thread
            withContext(Dispatchers.Main) {
                adapter.clear()
                Toast.makeText(this@SessionActivity, "Tréning uložený!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadExercises() {
        val database = SessionDataDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            val exerciseEntities = database.sessionDataClassDao.getExercisesByDate()

            // Map the ExerciseEntity objects to your SessionItem model
            val exerciseList = exerciseEntities.map { entity ->
                SessionItem(
                    exeName = entity.getExeName(),
                    sets = entity.getnumOfSets(),
                    reps = entity.getnumOfReps(),
                    weight = entity.getWeight()
                )
            }

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                // Update your adapter or UI elements with the loaded data
                adapter.setExerciseList(ArrayList(exerciseList))
                adapter.notifyDataSetChanged()
            }
        }
    }

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

    //Vytvorí menu pre Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.session_menu, menu)
        return true
    }

}