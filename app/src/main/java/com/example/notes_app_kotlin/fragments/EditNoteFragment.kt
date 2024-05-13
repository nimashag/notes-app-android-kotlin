package com.example.notes_app_kotlin.fragments

import android.app.AlertDialog
import android.os.Bundle
import com.example.notes_app_kotlin.model.Note
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes_app_kotlin.MainActivity
import com.example.notes_app_kotlin.R
import com.example.notes_app_kotlin.databinding.FragmentEditNoteBinding
import com.example.notes_app_kotlin.viewmodel.NoteViewModel

class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    // View binding instance for accessing views in the layout
    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    // ViewModel for managing notes data
    private lateinit var notesViewModel: NoteViewModel

    // Current note being edited
    private lateinit var currentNote: Note

    // Arguments passed to this fragment, including the note to be edited
    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the host activity and add this fragment as a menu provider
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Obtain the NoteViewModel from the MainActivity
        notesViewModel = (activity as MainActivity).noteViewModel

        // Retrieve the current note from the navigation arguments
        currentNote = args.note!!

        // Set the title and description of the current note in EditText views
        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDesc)

        // Set OnClickListener for the save button to update the note
        binding.editNoteFab.setOnClickListener{
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            val noteDesc = binding.editNoteDesc.text.toString().trim()

            if (noteTitle.isNotEmpty()){
                // Update the note with new title and description
                val note = Note(currentNote.id, noteTitle, noteDesc)
                notesViewModel.updateNote(note)

                // Navigate back to the home fragment
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                // Show a Toast message if note title is empty
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to display AlertDialog for deleting the note
    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note? ")
            setPositiveButton("Delete"){_,_ ->
                // Delete the note using ViewModel
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(context,"Note Deleted", Toast.LENGTH_SHORT).show()

                // Navigate back to the home fragment
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    // Function to create menu items
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Clear existing menu items
        menu.clear()
        // Inflate menu_edit_note.xml for editing notes
        menuInflater.inflate(R.menu.menu_edit_note, menu)
    }

    // Function to handle menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            // If delete menu item is selected, call deleteNote function
            R.id.deleteMenu -> {
                deleteNote()
                true
            } else -> false
        }
    }

    // Clean up resources when Fragment is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Avoid memory leaks by setting view binding instance to null
        editNoteBinding = null
    }
}
