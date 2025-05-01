package com.example.foodfiesta.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfiesta.DetailsActivity
import com.example.foodfiesta.Model.MenuItemModel
import com.example.foodfiesta.databinding.MenuItemBinding

class MenuAdapter(

    private val menuItem: List<MenuItemModel>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder((binding))
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItem.size

    inner class MenuViewHolder(
        private val binding: MenuItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailActivity(position)
                }

            }
        }

        private fun openDetailActivity(position: Int) {
            val MenuItem = menuItem[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuItemName", MenuItem.foodName)
                putExtra("menuItemImage", MenuItem.foodImage)
                putExtra("menuItemPrice", MenuItem.foodPrice)
                putExtra("menuItemDecrip", MenuItem.foodDescription)
                putExtra("menuItemIngred", MenuItem.foodIngredients)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val MenuItem = menuItem[position]
            binding.apply {
                foodNameMenu.text = MenuItem.foodName
                priceMenu.text = MenuItem.foodPrice
                val uri=Uri.parse(MenuItem.foodImage)
                Glide.with(requireContext).load(uri).into(imageMenu)


            }
        }


    }

    interface OnClickListener {
        fun onItemClick(position: Int)

    }
}

