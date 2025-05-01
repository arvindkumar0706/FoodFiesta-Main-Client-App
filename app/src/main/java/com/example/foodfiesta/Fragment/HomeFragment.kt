package com.example.foodfiesta.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodfiesta.Adapter.MenuAdapter
import com.example.foodfiesta.MenuBottomSheetFragment
import com.example.foodfiesta.Model.MenuItemModel
import com.example.foodfiesta.R
import com.example.foodfiesta.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItemModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.ViewMenu.setOnClickListener {
            val bottomSheet = MenuBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "Test")
        }

        retrieveAndDisplayPopularItems()

        return binding.root


    }

    private fun retrieveAndDisplayPopularItems() {

        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("Menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItem = index.take(numItemToShow).map { menuItems[it] }

        setPopularItemsAdapter(subsetMenuItem)
    }

    private fun setPopularItemsAdapter(subsetMenuItem: List<MenuItemModel>) {
        val adapter= MenuAdapter(subsetMenuItem,requireContext())
        binding.populerrecycle.layoutManager= LinearLayoutManager(requireContext())
        binding.populerrecycle.adapter=adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner4, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })


    }

}