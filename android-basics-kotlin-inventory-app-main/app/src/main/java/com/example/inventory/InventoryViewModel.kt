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

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Book
import com.example.inventory.data.BookDao
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class InventoryViewModel(private val bookDao: BookDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allBooks: LiveData<List<Book>> = bookDao.getBooks().asLiveData()


    /**
     * Updates an existing Item in the database.
     */
    fun updateBook(
        bookId: Int,
        bookName: String,
        bookPages: String,
        bookFinishedAt: String
    ) {
        val updatedBook = getUpdatedBookEntry(bookId, bookName, bookPages, bookFinishedAt)
        updateBook(updatedBook)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateBook(book: Book) {
        viewModelScope.launch {
            bookDao.update(book)
        }
    }



    /**
     * Inserts the new Item into database.
     */
    fun addNewBook(bookName: String, bookPages: String, bookFinishedAt: String) {
        val newBook = getNewBookEntry(bookName, bookPages, bookFinishedAt)
        insertBook(newBook)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertBook(book: Book) {
        viewModelScope.launch {
            bookDao.insert(book)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            bookDao.delete(book)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveBook(id: Int): LiveData<Book> {
        return bookDao.getBook(id).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(bookName: String, bookPages: String, bookFinishedAt: String): Boolean {
        if (bookName.isBlank() || bookPages.isBlank() || bookFinishedAt.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Book] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewBookEntry(bookName: String, bookPages: String, bookFinishedAt: String): Book {
        return Book(
            bookName = bookName,
            bookPages = bookPages.toDouble(),
            bookFinishedAt = bookFinishedAt.toInt()
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Book] entity class with the item info updated by the user.
     */
    private fun getUpdatedBookEntry(
        bookId: Int,
        bookName: String,
        bookPages: String,
        bookFinishedAt: String
    ): Book {
        return Book(
            id = bookId,
            bookName = bookName,
            bookPages = bookPages.toDouble(),
            bookFinishedAt = bookFinishedAt.toInt()
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class InventoryViewModelFactory(private val bookDao: BookDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

