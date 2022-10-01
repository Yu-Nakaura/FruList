package app.nakaura.chloe.original.graph

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.nakaura.chloe.original.todo.ToDoFragment

class GraphAdapter(fm: ToDoFragment): FragmentStateAdapter(fm){
    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> AppleFragment()
            1 -> LemonFragment()
            2 -> PearFragment()
            3 -> GrapeFragment()
            else -> AppleFragment()
        }

    override fun getItemCount(): Int {
        return 4
    }
}
