package com.example.foodfiesta.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfiesta.DetailsActivity
import com.example.foodfiesta.databinding.PopularitemBinding

class PopularAdapter (
    private val Items:List<String>,
    private val price:List<String>,
    private val image:List<Int>,
    private val requireContext:Context
): RecyclerView.Adapter<PopularAdapter.PopulerViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopulerViewHolder {
        return PopulerViewHolder(PopularitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }



    override fun onBindViewHolder(holder: PopulerViewHolder, position: Int) {
        val item = Items[position]
        val images=image[position]
        val price =price[position]
        holder.bind(item,images,price)

        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName",item)
            intent.putExtra("MenuItemImage",images)
            requireContext.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return Items.size
    }

    class PopulerViewHolder(private val binding: PopularitemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imagesview=binding.imagePopular
        fun bind(item: String, images: Int,price: String) {
            binding.foodNamePopular.text=item
            binding.pricePolpular.text=price
            imagesview.setImageResource(images)

        }

    }
}