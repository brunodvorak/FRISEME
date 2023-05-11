package com.example.frifittracker

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.database.SessionDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener  {

    private val calendar = Calendar.getInstance()
    private var adapter = AdapterClass()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        layoutManager = LinearLayoutManager(this)
        val sessionItemsList: RecyclerView = findViewById(R.id.actualTraining)
        sessionItemsList.layoutManager = layoutManager //spravuje pozície itemov
        sessionItemsList.adapter = adapter // spravuje inputy
        loadExercises()

        updateDate()

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
        }

        findViewById<ImageButton>(R.id.rightArrowButton).setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDate()
        }
    }

    private fun updateDate() {
        findViewById<TextView>(R.id.sessionDateText).text =
            SimpleDateFormat("d.MMM.yyyy", Locale.getDefault()).format(calendar.timeInMillis)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        updateDate()
    }

    //Vytvorí menu pre Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_session -> Toast.makeText(this, "Pridaj tréning", Toast.LENGTH_SHORT).show()
            R.id.new_session -> this.sessionActivitySwitch()
            R.id.saved_sessions -> Toast.makeText(this, "Uložené tréningy", Toast.LENGTH_SHORT).show()
            R.id.params -> Toast.makeText(this, "Body tracker", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sessionActivitySwitch() {
        val intent = Intent(this@MainActivity, SessionActivity::class.java)
        startActivity(intent)
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

}