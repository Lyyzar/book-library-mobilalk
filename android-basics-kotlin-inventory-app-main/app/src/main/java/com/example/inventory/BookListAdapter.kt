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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Book
import com.example.inventory.databinding.BookListBookBinding


/**
 * [ListAdapter] implementation for the recyclerview.
 */

class BookListAdapter(private val onBookClicked: (Book) -> Unit) :
    ListAdapter<Book, BookListAdapter.BookViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(
            BookListBookBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onBookClicked(current)
        }
        holder.bind(current)
    }

    class BookViewHolder(private var binding: BookListBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.bookName.text = book.bookName
            binding.bookPages.text = book.bookPages.toString()
            binding.bookFinishedat.text = book.bookFinishedAt.toString()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldBook: Book, newBook: Book): Boolean {
                return oldBook === newBook
            }

            override fun areContentsTheSame(oldBook: Book, newBook: Book): Boolean {
                return oldBook.bookName == newBook.bookName
            }
        }
    }
}
