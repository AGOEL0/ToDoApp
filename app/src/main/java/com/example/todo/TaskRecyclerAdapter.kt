package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.EachTodoItemBinding

class TaskRecyclerAdapter(val context: Context,var taskList: MutableList<User>):
    RecyclerView.Adapter<TaskRecyclerAdapter.MyViewHolder>() {
    private  lateinit var binding: EachTodoItemBinding
    private  val TAG = "TaskAdapter"
    private var listener:TaskAdapterInterface? = null
    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener
    }
    inner class MyViewHolder( private var  binding:EachTodoItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user=user


        }


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding=EachTodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var user=taskList[position]
        holder.bind(user)
        binding.deleteTask.setOnClickListener {
            listener?.onDeleteItemClicked(user, position)
        }
        binding.updateTask.setOnClickListener {
            listener?.onEditItemClicked(user , position)
        }


    }

    interface TaskAdapterInterface{
        fun onDeleteItemClicked(user: User , position : Int)
        fun onEditItemClicked(user: User , position: Int)
    }



}