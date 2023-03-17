package com.example.todoapplication.ui.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapplication.R
import com.example.todoapplication.data.Task
import com.example.todoapplication.databinding.FragmentAddEditTaskBinding
import com.example.todoapplication.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AddEditTaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentAddEditTaskBinding.inflate(inflater)
        val args = AddEditTaskFragmentArgs.fromBundle(requireArguments())

        if(args.task?.name === null){
            binding.apply {

                taskTitle.setText("Add Task")
                addButton.setOnClickListener {
                    if(TextUtils.isEmpty(taskName.text)){
                        Toast.makeText(
                            requireContext(),
                            "Please type the task name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val task_name = taskName.text.toString()
                    val description = description.text.toString()

                    val Task = getUID()?.let { user ->
                        Task(
                            getRandomString(),
                            task_name,
                            description,
                            System.currentTimeMillis(),
                            user.uid
                        )
                    }
                    if (Task != null) {
                        viewModel.insert(Task)
                    }
                    Toast.makeText(
                        requireContext(),
                        "Task saved",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(AddEditTaskFragmentDirections.actionAddEditTaskFragmentToTaskFragment(getUID()))
                }
            }
        } else {
            binding.apply {

                taskTitle.setText("Edit Task")
                taskName.setText(args.task?.name)
                description.setText(args.task?.description)

                addButton.setOnClickListener {
                    val task_name = taskName.text
                    val description = description.text
                    val task = Task(
                        args.task!!.id,
                        task_name.toString(),
                        description.toString(),
                        args.task!!.created,
                        args.task!!.uid
                    )

                    if(TextUtils.isEmpty(taskName.text)){
                        Toast.makeText(
                            requireContext(),
                            "Please type the task name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    viewModel.update(task)

                    findNavController().navigate(AddEditTaskFragmentDirections.actionAddEditTaskFragmentToTaskFragment(getUID()))
                    Toast.makeText(
                        requireContext(),
                        "Task saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setHasOptionsMenu(true)
        }
        return binding.root
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..20)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun getUID(): FirebaseUser? {
        val user = Firebase.auth.currentUser
        Log.d(TAG, "new UID: $user")
        return user
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_icon_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_task -> {
                deleteTask()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun deleteTask() {
        val args = AddEditTaskFragmentArgs.fromBundle(requireArguments())
        val task = Task(
            args.task!!.id,
            args.task!!.name,
            args.task!!.description,
            args.task!!.created,
            args.task!!.uid
        )

        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.delete(task)
                findNavController().navigate(AddEditTaskFragmentDirections.actionAddEditTaskFragmentToTaskFragment(getUID()))
                Toast.makeText(
                    requireContext(),
                    "Task deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}