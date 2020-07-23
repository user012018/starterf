/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.omgandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.raywenderlich.android.omgandroid.R
import com.raywenderlich.android.omgandroid.DetailActivity
import com.squareup.picasso.Picasso
import android.content.Intent
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView

class DetailActivity : AppCompatActivity() {
    var imageURL = ""
    var shareActionProvider: ShareActionProvider? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tell the activity which XML layout is right
        setContentView(R.layout.activity_detail)

        // Enable the "Up" button for more navigation options
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        // Access the imageview from XML
        val imageView = findViewById<ImageView>(R.id.img_cover)

        // 13. unpack the coverId from its trip inside your Intent
        val coverId = this.intent.extras.getString("coverID")

        // See if there is a valid coverId
        if (coverId != null && coverId.length > 0) {

            // Use the ID to construct an image URL
            imageURL = "$IMAGE_URL_BASE$coverId-L.jpg"

            // Use Picasso to load the image
            Picasso.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.img_books_loading)
                    .into(imageView)
        }
    }

    private fun setShareIntent() {

        // create an Intent with the contents of the TextView
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageURL)

        // Make sure the provider knows
        // it should work with that Intent
        if (shareActionProvider != null) {
            shareActionProvider!!.setShareIntent(shareIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu
        // this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        // Access the Share Item defined in menu XML
        val shareItem = menu.findItem(R.id.menu_item_share)

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        }
        setShareIntent()
        return true
    }

    companion object {
        private const val IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"
    }
}