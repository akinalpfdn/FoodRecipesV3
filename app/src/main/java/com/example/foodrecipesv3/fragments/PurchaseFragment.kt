package com.example.foodrecipesv3.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.foodrecipesv3.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PurchaseFragment : BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        fun newInstance(): PurchaseFragment {
            val fragment = PurchaseFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val displayMetrics = DisplayMetrics()
            val display = activity?.windowManager?.defaultDisplay
            display?.getMetrics(displayMetrics)
            val height = (displayMetrics.heightPixels * 0.85).toInt()
            val width = (displayMetrics.widthPixels * 1)//.toInt()
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Set the gravity to bottom so the dialog will appear at the bottom of the screen
            val layoutParams = dialog.window?.attributes
            layoutParams?.gravity = Gravity.BOTTOM
            dialog.window?.attributes = layoutParams

            // Explicitly set the window animations here
            dialog.window?.setWindowAnimations(R.style.DialogSlideAnimation)
            // Apply slide-up animation
            dialog.window?.attributes?.windowAnimations = R.style.DialogSlideAnimation
            // Apply a flag to ensure it respects the animations
            dialog.window?.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
            // Get the bottom sheet view
            val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            // Set the bottom sheet to expand fully on opening
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT // Optional: make it fully expanded by default

                behavior.skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


    }


}