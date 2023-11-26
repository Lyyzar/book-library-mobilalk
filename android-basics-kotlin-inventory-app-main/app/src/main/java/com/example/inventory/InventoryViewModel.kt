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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Book
import com.example.inventory.data.BookDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val bookDao: BookDao) : ViewModel() {

    val allBooks: LiveData<List<Book>> = bookDao.getBooks().asLiveData()
    val allPages: LiveData<Int> = bookDao.getSumOfPages()


    fun updateBook(
        bookId: Int,
        bookName: String,
        bookAuthor: String,
        bookPages: String,
        bookFinishedAt: String
    ) {
        val updatedBook = getUpdatedBookEntry(bookId, bookName, bookAuthor, bookPages, bookFinishedAt)
        updateBook(updatedBook)
    }


    private fun updateBook(book: Book) {
        viewModelScope.launch {
            bookDao.update(book)
        }
    }


    fun addNewBook(bookName: String, bookAuthor: String, bookPages: String, bookFinishedAt: String) {
        val newBook = getNewBookEntry(bookName, bookAuthor, bookPages, bookFinishedAt)
        insertBook(newBook)
    }


    private fun insertBook(book: Book) {
        viewModelScope.launch {
            bookDao.insert(book)
            Log.d("InventoryViewModel","insertBook-nál " + book.bookName)
        }
    }


    fun deleteBook(book: Book) {
        viewModelScope.launch {
            bookDao.delete(book)
        }
    }


    fun retrieveBook(id: Int): LiveData<Book> {
        return bookDao.getBook(id).asLiveData()
    }


    fun isEntryValid(bookName: String, bookAuthor: String, bookPages: String, bookFinishedAt: String): Boolean {
        if (bookName.isBlank() || bookAuthor.isBlank() || bookPages.isBlank() || bookFinishedAt.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewBookEntry(bookName: String, bookAuthor: String,bookPages: String, bookFinishedAt: String): Book {
        return Book(
            bookName = bookName,
            bookAuthor = bookAuthor,
            bookPages = bookPages.toInt(),
            bookFinishedAt = bookFinishedAt
        )
    }


    private fun getUpdatedBookEntry(
        bookId: Int,
        bookName: String,
        bookAuthor: String,
        bookPages: String,
        bookFinishedAt: String
    ): Book {
        return Book(
            id = bookId,
            bookName = bookName,
            bookAuthor = bookAuthor,
            bookPages = bookPages.toInt(),
            bookFinishedAt = bookFinishedAt
        )
    }
}

class InventoryViewModelFactory(private val bookDao: BookDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

