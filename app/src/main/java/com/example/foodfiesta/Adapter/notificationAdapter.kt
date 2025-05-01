package com.example.foodfiesta.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfiesta.databinding.NotificationItemBinding

class notificationAdapter(
    private val notification:ArrayList<String>,
    private val notificationImage : ArrayList<Int>
):RecyclerView.Adapter<notificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): notificationAdapter.NotificationViewHolder {
        val binding=NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: notificationAdapter.NotificationViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = notification.size


    inner class NotificationViewHolder(private val binding:NotificationItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                notificationtextview.text=notification[position]
                notificationImageView.setImageResource(notificationImage[position])
            }
        }

    }
}