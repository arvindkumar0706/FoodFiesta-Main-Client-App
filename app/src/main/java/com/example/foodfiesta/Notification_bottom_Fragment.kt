package com.example.foodfiesta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfiesta.Adapter.notificationAdapter
import com.example.foodfiesta.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Notification_bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notifications = listOf("Your order has been Cancelled Successfully",
            "Order has been taken by the driver",
            "Congrats! Your Order has been Placed")

        val notificationImages= listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.success)
        val adapter=notificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )

        binding.NotificationRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.NotificationRecyclerView.adapter=adapter
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {

    }
}