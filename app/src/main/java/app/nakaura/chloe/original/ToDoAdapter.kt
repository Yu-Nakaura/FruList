package app.nakaura.chloe.original

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.nakaura.chloe.original.databinding.ToDoListItemBinding

class ToDoAdapter : ListAdapter<ToDo, ToDoViewHolder>(diffUtilItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = ToDoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ToDoViewHolder(
    private val binding: ToDoListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todo: ToDo) {
        //val userName: String = binding.userSignupText.text.toString()
        //        val password: String = binding.passwordSignupText.text.toString()
        binding.checkBox.text = todo.userName
        binding.pointNumberText.text = todo.point.toString()
    }
}

private val diffUtilItemCallback = object : DiffUtil.ItemCallback<ToDo>() {
    override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
        return oldItem.title == newItem.title
    }
}