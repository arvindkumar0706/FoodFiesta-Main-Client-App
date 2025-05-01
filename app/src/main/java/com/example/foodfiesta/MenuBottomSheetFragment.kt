package com.example.foodfiesta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfiesta.Adapter.CartAdapter
import com.example.foodfiesta.Adapter.MenuAdapter
import com.example.foodfiesta.Model.MenuItemModel
import com.example.foodfiesta.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentMenuBottomSheetBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems:MutableList<MenuItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

        binding.backbutton.setOnClickListener {
            dismiss()
        }
        retriveMenuItems()
        return binding.root
    }

    private fun retriveMenuItems() {
       database=FirebaseDatabase.getInstance()
       val foodref:DatabaseReference=database.reference.child("Menu")
        menuItems= mutableListOf()
        foodref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setAdapter() {
        val adapter= MenuAdapter(menuItems,requireContext())
        binding.menuRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter=adapter
    }

    companion object {

    }
}