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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Book
import com.example.inventory.databinding.FragmentBookDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [BookDetailFragment] displays the details of the selected item.
 */
class BookDetailFragment : Fragment() {
    private val navigationArgs: BookDetailFragmentArgs by navArgs()
    lateinit var book: Book

    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.bookDao()
        )
    }

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(book: Book) {
        binding.apply {
            bookName.text = book.bookName
            bookPages.text = book.bookPages.toString()
            bookFinishedat.text = book.bookFinishedAt.toString()
            deleteBook.setOnClickListener { showConfirmationDialog() }
            editBook.setOnClickListener { editBook() }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editBook() {
        val action = BookDetailFragmentDirections.actionBookDetailFragmentToAddBookFragment(
            getString(R.string.edit_fragment_title),
            book.id
        )
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteBook()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteBook() {
        viewModel.deleteBook(book)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.bookId
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveBook(id).observe(this.viewLifecycleOwner) { selectedBook ->
            book = selectedBook
            bind(book)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
