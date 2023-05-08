package com.example.frifittracker


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sessionRecyclerView.SessionItem

class AdapterClass: RecyclerView.Adapter<AdapterClass.ViewHolder>() {

    private var exeNameList = arrayListOf<String>("Drepy")
    private var exeAttrList = arrayListOf<SessionItem>(SessionItem(3,8,70))
    private var exeWeightList = arrayListOf<Int>(exeAttrList[0].getFullWeight())


    fun addSession(exeName: String, exeAttr: SessionItem, exeWeight: Int) {
        exeNameList.add(exeName)
        exeAttrList.add(exeAttr)
        exeWeightList.add(exeWeight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.exe_card, parent, false)

        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return exeNameList.size
    }

    override fun onBindViewHolder(holder: AdapterClass.ViewHolder, position: Int) {

        holder.exeName.text = exeNameList[position]
        holder.exeAttributes.text = exeAttrList[position].getTextRepresentation()
        holder.exeWeight.text = exeWeightList[position].toString() + "kg"
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var exeName: TextView
        var exeAttributes: TextView
        var exeWeight: TextView

        init {
            exeName = itemView.findViewById(R.id.exe_name)
            exeAttributes = itemView.findViewById(R.id.exe_attributes)
            exeWeight = itemView.findViewById(R.id.exe_weight)
        }
    }
}