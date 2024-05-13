package com.example.notes_app_kotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes_app_kotlin.MainActivity
import com.example.notes_app_kotlin.R
import com.example.notes_app_kotlin.adapter.NoteAdapter
import com.example.notes_app_kotlin.databinding.FragmentHomeBinding
import com.example.notes_app_kotlin.model.Note
import com.example.notes_app_kotlin.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {

    // View binding instance for accessing views in the layout
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    // ViewModel for managing notes data
    private lateinit var notesViewModel: NoteViewModel

    // Adapter for managing the list of notes to display
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the host activity and add this fragment as a menu provider
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Obtain the NoteViewModel from the MainActivity
        notesViewModel = (activity as MainActivity).noteViewModel

        // Setup RecyclerView to display notes
        setupHomeRecyclerView()

        // Set OnClickListener for the FAB to navigate to AddNoteFragment
        binding.addNoteFab.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }
    }

    // Function to update UI based on the list of notes
    private fun updateUI(note: List<Note>?){
        if (note != null){
            if (note.isNotEmpty()){
                binding.emptyNotesImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    // Function to setup RecyclerView to display notes
    private fun setupHomeRecyclerView(){
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            // Observe changes in the list of notes and update the UI accordingly
            notesViewModel.getAllNotes().observe(viewLifecycleOwner){ note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }

    // Function to search notes based on query
    private fun searchNote(query: String?){
        val searchQuery = "%$query"

        // Observe changes in the search result and update the RecyclerView
        notesViewModel.searchNote(searchQuery).observe(this) {list ->
            noteAdapter.differ.submitList(list)
        }
    }

    // Function called when user submits a query text
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    // Function called when user changes query text
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            // Search for notes based on the new query text
            searchNote(newText)
        }
        return true
    }

    // Clean up resources when Fragment is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Avoid memory leaks by setting view binding instance to null
        homeBinding = null
    }

    // Function to create menu items
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Clear existing menu items
        menu.clear()
        // Inflate home_menu.xml for the options menu
        menuInflater.inflate(R.menu.home_menu, menu)

        // Get the SearchView from the menu and set its properties and listeners
        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    // Function to handle menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}
