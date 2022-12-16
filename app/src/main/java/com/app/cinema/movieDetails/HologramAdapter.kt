package com.app.cinema.movieDetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.R

class HologramAdapter :
    RecyclerView.Adapter<HologramAdapter.HologramViewHolder>() {

    private var hologramList = ArrayList<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setHologramList(
        hologramList: ArrayList<String>
    ) {
        this.hologramList = hologramList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HologramViewHolder {
        // Initialize view
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.hologram_card_view, parent, false
        )
        // Pass holder view
        return HologramViewHolder(view)
    }

    override fun onBindViewHolder(holder: HologramViewHolder, position: Int) {
        // Set text on radio button
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return hologramList.size
    }

    inner class HologramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assign variable
        var hologramText: TextView = view.findViewById(R.id.genre)

        fun setData(position: Int) {
            hologramText.text = hologramList[position]

        }
    }

}