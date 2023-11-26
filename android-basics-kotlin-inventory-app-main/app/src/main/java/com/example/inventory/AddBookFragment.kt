/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Book
import com.example.inventory.databinding.FragmentAddBookBinding
import java.util.Calendar


class AddBookFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .bookDao()
        )
    }
    private val navigationArgs: BookDetailFragmentArgs by navArgs()

    lateinit var book: Book

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.bookName.text.toString(),
            binding.bookAuthor.text.toString(),
            binding.bookPages.text.toString(),
            binding.bookFinishedat.text.toString(),
        )
    }


    private fun bind(book: Book) {
        binding.apply {
            bookName.setText(book.bookName, TextView.BufferType.SPANNABLE)
            bookAuthor.setText(book.bookAuthor, TextView.BufferType.SPANNABLE)
            bookPages.setText(book.bookPages.toString(), TextView.BufferType.SPANNABLE)
            bookFinishedat.setText(book.bookFinishedAt, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateBook() }
        }
    }


    private fun addNewBook() {
        if (isEntryValid()) {
            viewModel.addNewBook(
                binding.bookName.text.toString(),
                binding.bookAuthor.text.toString(),
                binding.bookPages.text.toString(),
                binding.bookFinishedat.text.toString(),
            )
            val action = AddBookFragmentDirections.actionAddBookFragmentToBookListFragment()
            findNavController().navigate(action)
        }
    }


    private fun updateBook() {
        if (isEntryValid()) {
            viewModel.updateBook(
                this.navigationArgs.bookId,
                this.binding.bookName.text.toString(),
                this.binding.bookName.text.toString(),
                this.binding.bookPages.text.toString(),
                this.binding.bookFinishedat.text.toString()
            )
            val action = AddBookFragmentDirections.actionAddBookFragmentToBookListFragment()
            findNavController().navigate(action)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.bookFinishedat.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pickDate.setOnClickListener {
            showDatePickerDialog()
        }
        val id = navigationArgs.bookId
        if (id > 0) {
            viewModel.retrieveBook(id).observe(this.viewLifecycleOwner) { selectedBook ->
                book = selectedBook
                bind(book)

            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewBook()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
