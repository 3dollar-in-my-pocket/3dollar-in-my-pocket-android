package zion830.com.common.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import zion830.com.common.R

fun FragmentManager.addNewFragment(containerId: Int, fragment: Fragment, tag: String = "") =
    commit {
        setCustomAnimations(
            R.anim.start_from_right,
            R.anim.exit_to_left,
            R.anim.start_from_left,
            R.anim.exit_to_right
        )
        add(containerId, fragment, tag)
        addToBackStack(null)
    }

fun FragmentManager.replaceFragment(containerId: Int, fragment: Fragment, tag: String = "") =
    commit {
        setCustomAnimations(
            R.anim.start_from_right,
            R.anim.exit_to_left,
            R.anim.start_from_left,
            R.anim.exit_to_right
        )
        replace(containerId, fragment, tag)
        addToBackStack(null)
    }