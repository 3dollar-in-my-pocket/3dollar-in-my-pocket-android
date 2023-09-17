package zion830.com.common.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.ext.replaceFragment

fun AppCompatActivity.startNewFragment(containerId: Int, fragment: Fragment, tag: String = "", showAnimation: Boolean = true) {
    supportFragmentManager.addNewFragment(containerId, fragment, tag, showAnimation)
}

fun AppCompatActivity.replaceFragment(containerId: Int, fragment: Fragment, tag: String = "", showAnimation: Boolean = false) {
    supportFragmentManager.replaceFragment(containerId, fragment, tag, showAnimation)
}

fun AppCompatActivity.removeCurrentFragment() {
    supportFragmentManager.popBackStack()
}

fun AppCompatActivity.removeAllFragment() {
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}