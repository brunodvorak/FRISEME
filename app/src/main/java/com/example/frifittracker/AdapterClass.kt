package com.example.frifittracker



import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent

class AdapterClass : RecyclerView.Adapter<AdapterClass.ViewHolder>() {

    private var exerciseList = arrayListOf<SessionItem>()

    fun addExercise(exercise: SessionItem) {
        exerciseList.add(exercise)
    }

    fun getExerciseList(): ArrayList<SessionItem> {
        return exerciseList
    }

    fun setExerciseList(list: ArrayList<SessionItem>){
        exerciseList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.exe_card, parent, false)


        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: AdapterClass.ViewHolder, position: Int) {

        holder.exeName.text = exerciseList[position].getExeName()

        val exeSets: String = exerciseList[position].getnumOfSets().toString()
        val exeReps: String = exerciseList[position].getnumOfReps().toString()
        val exeWeight: String = exerciseList[position].getWeight().toString()
        holder.exeAttributes.text = exeSets + "x" + exeReps + "x" + exeWeight + "kg"
        holder.exeWeight.text = exerciseList[position].getFullWeight().toString() + "kg"
    }

    fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putSerializable("exerciseList", exerciseList)
        return bundle
    }

    fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle
        bundle?.let {
            exerciseList = it.getSerializable("exerciseList") as ArrayList<SessionItem>
            notifyDataSetChanged()
        }
    }

    fun clear() {
        exerciseList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var exeName: TextView
        var exeAttributes: TextView
        var exeWeight: TextView

        init {
            exeName = itemView.findViewById(R.id.exe_name)
            exeAttributes = itemView.findViewById(R.id.exe_attributes)
            exeWeight = itemView.findViewById(R.id.exe_weight)

            itemView.setOnLongClickListener {
                val position: Int = adapterPosition
                exerciseList.removeAt(position)
                notifyItemRemoved(position)
                true
            }
        }
    }
}