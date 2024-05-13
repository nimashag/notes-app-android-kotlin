package com.example.notes_app_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.notes_app_kotlin.database.NoteDatabase
import com.example.notes_app_kotlin.repository.NoteRepository
import com.example.notes_app_kotlin.viewmodel.NoteViewModel
import com.example.notes_app_kotlin.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    // ViewModel for managing notes data
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up ViewModel when activity is created
        setupViewModel()
    }

    // Function to set up ViewModel for managing notes data
    private fun setupViewModel(){
        // Create NoteRepository with the NoteDatabase
        val noteRepository = NoteRepository(NoteDatabase(this))
        // Create ViewModelProviderFactory with the application context and NoteRepository
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        // Get the NoteViewModel instance using ViewModelProvider and ViewModelProviderFactory
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory)[NoteViewModel::class.java]
    }
}
