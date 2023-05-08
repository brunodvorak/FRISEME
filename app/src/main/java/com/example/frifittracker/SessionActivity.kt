package com.example.frifittracker

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.se.omapi.Session
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sessionRecyclerView.SessionItem
import java.text.SimpleDateFormat
import java.util.*

class SessionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AdapterClass.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        layoutManager = LinearLayoutManager(this)

        val sessionItemsList: RecyclerView = findViewById(R.id.sessionItemsList)
        sessionItemsList.layoutManager = layoutManager
        adapter = AdapterClass()
        sessionItemsList.adapter = adapter

        //Date picker dialog okno
        findViewById<TextView>(R.id.sessionDateText).setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        //Priradí toolbar k layoutu
        setSupportActionBar(findViewById(R.id.newSessionToolbar))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_session_button -> finish()
            R.id.add_exe_button -> showDialogWindow()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialogWindow() {
        val exeWindow = Dialog(this)
        exeWindow.setContentView(R.layout.new_exe_dialog)
        exeWindow.show()

        val closeDialogListener = exeWindow.findViewById<ImageButton>(R.id.close_dialog_button)
        closeDialogListener.setOnClickListener {
            exeWindow.dismiss()
        }

        val saveExeListener = exeWindow.findViewById<Button>(R.id.save_exe_button)
        saveExeListener.setOnClickListener {
            saveExe()
        }
    }

    private fun saveExe() {
        // Get references to the EditText views
        val nameInput = dialog?.findViewById<EditText>(R.id.exe_name_input)
        val setsInput = dialog?.findViewById<EditText>(R.id.num_of_sets_input)
        val repsInput = dialog?.findViewById<EditText>(R.id.num_of_rep_input)
        val weightInput = dialog?.findViewById<EditText>(R.id.exe_weight_input)

        // Get the values entered in the EditText views
        val name = nameInput?.text?.toString() ?: ""
        val sets = setsInput?.text?.toString()?.toIntOrNull() ?: 0
        val reps = repsInput?.text?.toString()?.toIntOrNull() ?: 0
        val weight = weightInput?.text?.toString()?.toIntOrNull() ?: 0

        // Create a new SessionItem object
        val newItem = SessionItem(sets, reps, weight)

        // Update the ArrayLists
        exeNameList.add(name)
        exeAttrList.add(newItem)
        exeWeightList.add(newItem.getFullWeight())

        // Update the RecyclerView adapter data source
        val adapter = recyclerView.adapter as ExerciseListAdapter
        adapter.addExercise(name, newItem.getFullWeight())

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged()
    }

    //Vytvorí menu pre Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.session_menu, menu)
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        findViewById<TextView>(R.id.sessionDateText).text = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(calendar.timeInMillis)
    }

}