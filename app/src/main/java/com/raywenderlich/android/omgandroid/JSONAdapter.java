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

package com.raywenderlich.android.omgandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONAdapter extends BaseAdapter {

  private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

  private Context context;
  private LayoutInflater inflater;
  private JSONArray jsonArray;

  JSONAdapter(Context context,
                     LayoutInflater inflater) {

    this.context = context;
    this.inflater = inflater;
    jsonArray = new JSONArray();
  }

  void updateData(JSONArray jsonArray) {

    // update the adapter's dataset
    this.jsonArray = jsonArray;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return jsonArray.length();
  }

  @Override
  public JSONObject getItem(int position) {
    return jsonArray.optJSONObject(position);
  }

  @Override
  public long getItemId(int position) {

    // your particular dataset uses String IDs
    // but you have to put something in this method
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;

    // check if the view already exists
    // if so, no need to inflate and findViewById again!
    if (convertView == null) {

      // Inflate the custom row layout from your XML.
      convertView = inflater.inflate(R.layout.row_book, null);

      // create a new "Holder" with subviews
      holder = new ViewHolder();
      holder.thumbnailImageView =
              convertView
                  .findViewById(R.id.img_thumbnail);
      holder.titleTextView =
              convertView
                  .findViewById(R.id.text_title);
      holder.authorTextView =
              convertView
                  .findViewById(R.id.text_author);

      // hang onto this holder for future recyclage
      convertView.setTag(holder);
    } else {

      // skip all the expensive inflation/findViewById
      // and just get the holder you already made
      holder = (ViewHolder) convertView.getTag();
    }

    // Get the current book's data in JSON form
    JSONObject jsonObject = getItem(position);

    // See if there is a cover ID in the Object
    if (jsonObject.has("cover_i")) {

      // If so, grab the Cover ID out from the object
      String imageID = jsonObject.optString("cover_i");

      // Construct the image URL (specific to API)
      String imageURL = IMAGE_URL_BASE
          + imageID
          + "-S.jpg";

      // Use Picasso to load the image
      // Temporarily have a placeholder in case it's slow to load
      Picasso.with(context)
          .load(imageURL)
          .placeholder(R.drawable.ic_books)
          .into(holder.thumbnailImageView);
    } else {

      // If there is no cover ID in the object, use a placeholder
      holder.thumbnailImageView
          .setImageResource(R.drawable.ic_books);
    }

    // Grab the title and author from the JSON
    String bookTitle = "";
    String authorName = "";

    if (jsonObject.has("title")) {
      bookTitle = jsonObject.optString("title");
    }

    if (jsonObject.has("author_name")) {
      authorName = jsonObject.optJSONArray("author_name")
          .optString(0);
    }

    // Send these Strings to the TextViews for display
    holder.titleTextView.setText(bookTitle);
    holder.authorTextView.setText(authorName);

    return convertView;
  }

  // this is used so you only ever have to do
  // inflation and finding by ID once ever per View
  private static class ViewHolder {
    ImageView thumbnailImageView;
    TextView titleTextView;
    TextView authorTextView;
  }
}

