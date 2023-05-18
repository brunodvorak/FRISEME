package com.example.frifittracker.tracker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.R

/**
 * Adapter class for the Tracker RecyclerView.
 * Handles the display and interaction with the list of TrackerItems.
 */
class TrackerAdapterClass(context: Context) :
    RecyclerView.Adapter<TrackerAdapterClass.ViewHolder>() {

    private var valWindow = Dialog(context)
    private lateinit var winButton: ImageButton

    private var bodyPartsList = arrayListOf<TrackerItem>(
        TrackerItem("Výška", 0.0),
        TrackerItem("Váha", 0.0),
        TrackerItem("Hruď", 0.0),
        TrackerItem("Brucho", 0.0),
        TrackerItem("Boky", 0.0),
        TrackerItem("Ruka pravá", 0.0),
        TrackerItem("Ruka ľavá", 0.0),
        TrackerItem("Predlaktie pravé", 0.0),
        TrackerItem("Predlaktie ľavé", 0.0),
        TrackerItem("Stehno pravé", 0.0),
        TrackerItem("Stehno ľavé", 0.0),
        TrackerItem("Lýtko pravé", 0.0),
        TrackerItem("Lýtko ľavé", 0.0)
    )

    /**
     * Saves the state of the adapter as a Parcelable object.
     * @return The Parcelable object representing the state of the adapter.
     */
    fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putSerializable("bodyPartsList", bodyPartsList)
        bundle.putBoolean("isShowing", valWindow.isShowing)

        if (valWindow.isShowing && valWindow.findViewById<EditText>(R.id.value_input).text.isNotEmpty() && valWindow.findViewById<EditText>(
                R.id.value_input
            ).text.isNotBlank()
        ) {
            val value =
                valWindow.findViewById<EditText>(R.id.value_input).text.toString().toDouble()
            bundle.putDouble("value", value)
        }
        return bundle
    }

    /**
     * Restores the state of the adapter from a Parcelable object.
     * @param state The Parcelable object representing the state of the adapter.
     */
    fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle
        bundle?.let {
            bodyPartsList = it.getSerializable("bodyPartsList") as ArrayList<TrackerItem>
            notifyDataSetChanged()
        }

        if (bundle?.getBoolean("isShowing") != null && bundle.getBoolean("isShowing")) {
            showDialog()
            val value = bundle.getDouble("value")
            if (value != 0.0) valWindow.findViewById<EditText>(R.id.value_input)
                .setText(value.toString())
        }
    }

    /**
     * Returns the total number of items in the adapter.
     * @return The number of items in the adapter.
     */
    override fun getItemCount(): Int {
        return bodyPartsList.size
    }

    /**
     * Returns the list of body parts in the adapter.
     * @return The list of body parts.
     */
    fun getBodyPartsList(): ArrayList<TrackerItem> {
        return bodyPartsList
    }

    /**
     * Sets the list of body parts in the adapter.
     * @param newBodyPartList The new list of body parts.
     */
    fun setBodyPartsList(newBodyPartList: ArrayList<TrackerItem>) {
        bodyPartsList = newBodyPartList
    }

    /**
     * Creates a new ViewHolder by inflating the layout for a tracker card.
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return A new instance of ViewHolder.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackerAdapterClass.ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.tracker_card, parent, false)

        return ViewHolder(layoutInflater)
    }

    /**
     * Binds the data of a body part to the corresponding ViewHolder.
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the body part in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val difference = bodyPartsList[position].getDifference()

        holder.bodyPartName.text = bodyPartsList[position].getBodyPart()
        if (bodyPartsList[position].getBodyPart() == "Váha") {
            holder.trackerValue.text = bodyPartsList[position].getValue().toString() + "kg"
            if (difference > 0) holder.trackerDiffrence.text = "+" + difference.toString() + "kg"
            else holder.trackerDiffrence.text = difference.toString() + "kg"
        } else {
            holder.trackerValue.text = bodyPartsList[position].getValue().toString() + "cm"
            if (difference > 0) holder.trackerDiffrence.text = "+" + difference.toString() + "cm"
            else holder.trackerDiffrence.text = difference.toString() + "cm"
        }
    }

    /**
     * Shows a dialog window.
     * @param context The context in which the dialog should be shown.
     * @return The created dialog.
     */
    fun showDialog(): ImageButton {
        valWindow.setContentView(R.layout.number_picker_dialog)
        valWindow.show()
        winButton = valWindow.findViewById(R.id.save_value_button)
        return winButton
    }

    /**
     * ViewHolder class for the TrackerAdapterClass.
     * @param itemView The inflated item view for the ViewHolder.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bodyPartName: TextView
        var trackerValue: TextView
        var trackerDiffrence: TextView

        /**
         * Initializes the views within the ViewHolder.
         */
        init {
            bodyPartName = itemView.findViewById(R.id.tracker_part_name)
            trackerValue = itemView.findViewById(R.id.tracker_value)
            trackerDiffrence = itemView.findViewById(R.id.tracker_difference)


            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val button = showDialog()

                button.setOnClickListener {
                    val value =
                        valWindow.findViewById<EditText>(R.id.value_input).text.toString()
                            .toDouble()
                    bodyPartsList[position].setValue(value)
                    notifyItemChanged(position)
                    valWindow.dismiss()
                }
            }

        }

    }
}