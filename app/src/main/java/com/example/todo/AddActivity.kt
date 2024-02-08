package com.example.todo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.databinding.ActivityAddBinding
import com.example.todo.databinding.EachTodoItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddActivity : AppCompatActivity(), TaskRecyclerAdapter.TaskAdapterInterface {
    private lateinit var binding: ActivityAddBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: MutableList<User>
    private lateinit var taskAdapter: TaskRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add)


        auth = Firebase.auth
        database = Firebase.database.reference
        binding.add.setOnClickListener{
            showCustomDialog() }

        taskList = mutableListOf()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        taskAdapter = TaskRecyclerAdapter(this, taskList)
        binding.recyclerView.adapter = taskAdapter
        taskAdapter.setListener(this)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                taskList.clear() // Clear existing tasks before updating
                for (taskSnapshot in dataSnapshot.children) {
                    val user = taskSnapshot.getValue<User>()
                    user?.let { taskList.add(it) }
                }
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAGY", "loadPost:onCancelled", databaseError.toException())
                Toast.makeText(this@AddActivity, "Task Unsuccessfully", Toast.LENGTH_SHORT).show()
            }
        }
        database.child("Tasks").child(auth.currentUser!!.uid).addValueEventListener(postListener) // Use auth ID
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnNo: Button = dialog.findViewById(R.id.cancel_button)
        val btnNxt: Button = dialog.findViewById(R.id.done)
        val editText: EditText = dialog.findViewById(R.id.task)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        btnNxt.setOnClickListener {
            val text = editText.text.toString()

            if (text.isNotEmpty()) {
                val newTaskRef = database.child("Tasks").child(auth.currentUser!!.uid).push()
                val user1 = User(text, newTaskRef.key)
                newTaskRef.setValue(user1) // Use push() with auth ID
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task Saved Successfully", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Task Save Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Fill the task or cancel ", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()
    }

     override  fun onDeleteItemClicked(user: User , position : Int) {

         database.child("Tasks").child(auth.currentUser!!.uid).child(user.key!!).removeValue()
             .addOnCompleteListener {
                 if (it.isSuccessful) {
                     Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                 } else {
                     Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                 }

             }
     }

    override fun onEditItemClicked(user: User, position: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Pre-fill the EditText with the existing task text
        val editText: EditText = dialog.findViewById(R.id.task)
        editText.setText(user.task)

        val btnNo: Button = dialog.findViewById(R.id.cancel_button)
        val btnNxt: Button = dialog.findViewById(R.id.done)

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnNxt.setOnClickListener {
            val updatedTaskText = editText.text.toString()

            if (updatedTaskText.isNotEmpty()) {
                // Update the task in Firebase (as you described in the next step)
                val updatedTask = User(updatedTaskText, user.key)  // Assuming User class has task and key properties
                database.child("Tasks").child(auth.currentUser!!.uid).child(user.key!!).setValue(updatedTask)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task Saved Successfully", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Task Save Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }

                // Update the local list and notify the adapter
                taskList[position] = updatedTask
                taskAdapter.notifyItemChanged(position)

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Fill the task or cancel", Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }


}
