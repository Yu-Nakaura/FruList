package app.nakaura.chloe.original.todo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.nakaura.chloe.original.R
import app.nakaura.chloe.original.databinding.ToDoListItemBinding

class ToDoAdapter : ListAdapter<ToDo, ToDoViewHolder>(diffUtilItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = ToDoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.checkBox.setOnClickListener(View.OnClickListener {
            Log.d("チェックされた", position.toString())
            listener.onItemClick(position)
        })
        holder.binding.openButton.setOnClickListener (View.OnClickListener {
            openListener.onItemClick(position)
        })
    }
    private lateinit var listener: OnCheckBoxClickListener

    private lateinit var openListener: OnOpenButtonClickListener

    interface OnCheckBoxClickListener{
        fun onItemClick(position: Int)
    }

    interface  OnOpenButtonClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnCheckBoxClickListener(listener: OnCheckBoxClickListener){
        this.listener = listener
    }

    fun setOnOpenButtonClickListener(openListener: OnOpenButtonClickListener){
        this.openListener = openListener
    }

}

class ToDoViewHolder(
    val binding: ToDoListItemBinding,
    //val checkBox: CheckBox = binding.checkBox
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todo: ToDo) {
        binding.checkBox.text = todo.title
        binding.pointText.text = todo.point
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