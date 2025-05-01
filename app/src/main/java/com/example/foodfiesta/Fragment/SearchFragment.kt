package com.example.foodfiesta.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfiesta.Adapter.MenuAdapter
import com.example.foodfiesta.Model.MenuItemModel
import com.example.foodfiesta.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var  adapter : MenuAdapter

    private lateinit var database: FirebaseDatabase
    private val originalMenuItems= mutableListOf<MenuItemModel>()





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSearchBinding.inflate(inflater,container,false)

        retrieveMenuItem()

        setupSearchView()

        return binding.root
    }

    private fun retrieveMenuItem() {
        database=FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference=database.reference.child("Menu")
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem= foodSnapshot.getValue(MenuItemModel::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showAllMenu() {
        val filteredMenuItem=ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: List<MenuItemModel>) {
        adapter= MenuAdapter(filteredMenuItem,requireContext())
        binding.menurecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.menurecyclerView.adapter=adapter
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String?) {
        val filteredMenuItem = originalMenuItems.filter {
            query?.let { it1 -> it.foodName?.contains(it1,ignoreCase = true) } ==true

        }
        setAdapter(filteredMenuItem)

    }

    companion object {

    }
}