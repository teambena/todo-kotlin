package com.example.todoapplication.ui.task

import android.app.AlertDialog
import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapplication.R
import com.example.todoapplication.data.Task
import com.example.todoapplication.databinding.FragmentTaskBinding
import com.example.todoapplication.viewmodel.TaskViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class TaskFragment : Fragment(), TaskAdapter.TaskClickListener  {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter : TaskAdapter
    private val args : TaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTaskBinding.inflate(inflater)

        binding.lifecycleOwner= viewLifecycleOwner
        binding.viewModel = viewModel

        adapter = TaskAdapter(this)

        binding.apply {
            binding.recyclerView.adapter = adapter
            fabAddTask.setOnClickListener {
                findNavController().navigate(R.id.action_taskFragment_to_addEditTaskFragment)
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTaskbyUid()
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }

    override fun onDeleteClick(task: Task) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.delete(task)
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

    override fun onEditClick(task: Task) {
        findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(task))
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.task_menu, menu)",
        "com.example.todoapplication.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater.inflate(R.menu.task_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_all -> {
                deleteAll()
            }
            R.id.sign_out -> {
                signOut()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun observeTaskbyUid(){
        Log.d(ContentValues.TAG, "check adapter item first: ${adapter.itemCount}")
            args.user?.let { it ->
                viewModel.getTaskByUid(it.uid).observe(viewLifecycleOwner){
                    adapter.submitList(it)
                    Log.d(ContentValues.TAG, "check adapter item: ${adapter.itemCount}")
                    if(adapter.itemCount == 0) {
                        Toast.makeText(
                            requireContext(),
                            "Loading. Please wait..",
                            Toast.LENGTH_LONG
                        ).show()
                        observeTaskbyUidRemote()
                    }
                }
            }
            Log.d(ContentValues.TAG, "Local")

    }

    private fun observeTaskbyUidRemote(){
        val handler = Handler()
        args.user?.let { it ->
            viewModel.getTaskByUidRemote(it.uid).observe(viewLifecycleOwner){
                adapter.submitList(it)

                handler.postDelayed({
                    Log.d(ContentValues.TAG, "check adapter item Remote: ${adapter.itemCount}")
                    val tasks = adapter.currentList
                    for( task in tasks){
                        viewModel.insert(
                            Task(
                                task.id,
                                task.name,
                                task.description,
                                task.created,
                                task.uid,
                                task.complete
                            )
                        )
                        Log.d(ContentValues.TAG, "task id: ${task.id}")
                    }
                    Log.d(ContentValues.TAG, "check inside: $tasks")
                    Toast.makeText(
                        requireContext(),
                        "Loading complete",
                        Toast.LENGTH_SHORT
                    ).show()
                }, 2500L)
            }
        }
    }

    private fun deleteAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All")
            .setMessage("Do you want to delete all checked task?")
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteAll()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun signOut() {
        googleSignIn()

        GoogleSignIn.getClient(requireActivity(), googleSignIn()).signOut()
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Are you sure you want to signout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            Firebase.auth.signOut()
                            findNavController().navigate(R.id.action_taskFragment_to_loginFragment)
                            Toast.makeText(
                                requireContext(),
                                "Sighout successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                }
            }
            )
    }

    private fun googleSignIn(): GoogleSignInOptions {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.todoapplication.R.string.default_web_client_id))
            .requestEmail()
            .build()
        return gso
    }

}