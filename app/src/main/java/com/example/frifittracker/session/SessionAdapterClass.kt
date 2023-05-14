package com.example.frifittracker.session

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frifittracker.R

/**
 * Adapter class for the session items in the RecyclerView.
 *
 * @param passedDate The date when the session was done.
 */
class SessionAdapterClass(private var passedDate: String) :
    RecyclerView.Adapter<SessionAdapterClass.ViewHolder>() {

    private var exerciseList = arrayListOf<SessionItem>()

    /**
     * Adds a new exercise to the adapter's exercise list.
     *
     * @param exercise The exercise to add.
     */
    fun addExercise(exercise: SessionItem) {
        exerciseList.add(exercise)
    }

    /**
     * Retrieves the exercise list.
     *
     * @return The exercise list.
     */
    fun getExerciseList(): ArrayList<SessionItem> {
        return exerciseList
    }

    /**
     * Sets the exercise list.
     *
     * @param list The exercise list to set.
     */
    fun setExerciseList(list: ArrayList<SessionItem>) {
        exerciseList = list
    }

    /**
     * Changes the passed date associated with the session.
     *
     * @param date The new date.
     */
    fun changePassedDate(date: String) {
        passedDate = date
    }

    /**
     * Creates a ViewHolder for the session item view.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.exe_card, parent, false)
        return ViewHolder(layoutInflater)
    }

    /**
     * Returns the number of items in the exercise list.
     *
     * @return The item count.
     */
    override fun getItemCount(): Int {
        return exerciseList.size
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the exercise list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.exeName.text = exerciseList[position].getExeName()

        val exeSets: String = exerciseList[position].getnumOfSets().toString()
        val exeReps: String = exerciseList[position].getnumOfReps().toString()
        val exeWeight: String = exerciseList[position].getWeight().toString()
        holder.exeAttributes.text = exeSets + "x" + exeReps + "x" + exeWeight + "kg"
        holder.exeWeight.text = exerciseList[position].getFullWeight().toString() + "kg"
    }

    /**
     * Saves the state of the adapter.
     *
     * @return The Parcelable state of the adapter.
     */
    fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putSerializable("exerciseList", exerciseList)
        return bundle
    }

    /**
     * Restores the state of the adapter.
     *
     * @param state The Parcelable state to restore.
     */
    fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle
        bundle?.let {
            exerciseList = it.getSerializable("exerciseList") as ArrayList<SessionItem>
            notifyDataSetChanged()
        }
    }

    /**
     * Clears the exercise list.
     */
    fun clear() {
        exerciseList.clear()
        notifyDataSetChanged()
    }

    /**
     * ViewHolder class for holding references to the views within each item of the SessionAdapterClass.
     *
     * @property itemView The root view of the item layout.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var exeName: TextView
        var exeAttributes: TextView
        var exeWeight: TextView

        /**
         * Initializes the views within the ViewHolder.
         */
        init {
            exeName = itemView.findViewById(R.id.exe_name)
            exeAttributes = itemView.findViewById(R.id.exe_attributes)
            exeWeight = itemView.findViewById(R.id.exe_weight)

            /**
             * Sets a long click listener to the itemView.
             * When long clicked, it removes the item from the exerciseList at the corresponding position,
             * and notifies the adapter about the item removal.
             */
            itemView.setOnLongClickListener {
                val position: Int = adapterPosition
                exerciseList.removeAt(position)
                notifyItemRemoved(position)
                true
            }
        }
    }
}