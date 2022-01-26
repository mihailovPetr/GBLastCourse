package com.example.gblastcourse.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gblastcourse.databinding.FragmentMarkersItemBinding
import com.example.gblastcourse.model.Marker

class MarkersAdapter : RecyclerView.Adapter<MarkersAdapter.RecyclerItemViewHolder>() {
    private var data: List<Marker> = arrayListOf()

    fun setData(data: List<Marker>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            FragmentMarkersItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class RecyclerItemViewHolder(val view: FragmentMarkersItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(data: Marker) {
            view.markersNameTV.text = data.name
            view.markersDescriptionTV.text = data.description
            view.markersLatitudeTV.text = data.latitude.toString()
            view.markersLongitudeTV.text = data.longitude.toString()
        }

    }
}