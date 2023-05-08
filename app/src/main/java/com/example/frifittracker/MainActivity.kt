package com.example.frifittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
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

    fun sessionActivitySwitch() {
        val intent = Intent(this@MainActivity, SessionActivity::class.java)
        startActivity(intent)
    }

}