package com.ssinsirity.library.ui.bottom_nav

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentBottomNavBinding
import com.ssinsirity.library.util.UserMode
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavFragment : Fragment(R.layout.fragment_bottom_nav) {
    private val binding by viewBinding(FragmentBottomNavBinding::bind)
    private val outerNavController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() = with(binding) {
        val navHostFragment =
            childFragmentManager.findFragmentById(contentNavHost.id) as NavHostFragment
        val bottomNavController = navHostFragment.navController
        bottomNavigation.setupWithNavController(bottomNavController)

        if (UserMode.mode == UserMode.Mode.READER) {
            bottomNavigation.menu.removeItem(R.id.readersFragment)
        }
    }
}