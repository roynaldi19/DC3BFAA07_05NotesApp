package com.roynaldi19.dc3_07notesapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.roynaldi19.dc3_07notesapp.adapter.NoteAdapter
import com.roynaldi19.dc3_07notesapp.databinding.ActivityMainBinding
import com.roynaldi19.dc3_07notesapp.entity.Note

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.data != null) {
                when (result.resultCode) {
                    NoteAddUpdateActivity.RESULT_ADD -> {
                        val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                        adapter.addItem(note)
                        binding.rvNotes.smoothScrollToPosition(adapter.itemCount -1)
                        showSnackBarMessage("Satu item berhasil ditambahkan")
                    }
                    NoteAddUpdateActivity.RESULT_UPDATE -> {
                        val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                        val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                        adapter.updateItem(position, note)
                        binding.rvNotes.smoothScrollToPosition(position)
                        showSnackBarMessage("Satu item berhasil diubah")
                    }
                    NoteAddUpdateActivity.RESULT_DELETE -> {
                        val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                        adapter.removeItem(position)
                        showSnackBarMessage("Satu item berhasil dihapus")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)

        adapter = NoteAdapter(object : NoteAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedNote: Note?, position: Int) {
                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, selectedNote)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener{
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun showSnackBarMessage(message: String){
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }
}