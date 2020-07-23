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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
    AdapterView.OnItemClickListener {

  private TextView emptyView;
  private TextView mainTextView;
  private EditText mainEditText;
  private JSONAdapter jsonAdapter;
  private ProgressBar progressBar;
  private ShareActionProvider shareActionProvider;
  private SharedPreferences sharedPreferences;
  private ListView mainListView;


  private static final String PREFS = "prefs";
  private static final String PREF_NAME = "name";
  private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 1. Access the TextView defined in layout XML
    // and then set its text
    mainTextView = findViewById(R.id.main_textview);

    // 2. Access the Button defined in layout XML
    // and listen for it here
    Button mainButton = findViewById(R.id.main_button);
    mainButton.setOnClickListener(this);

    // 3. Access the EditText defined in layout XML
    mainEditText = findViewById(R.id.main_edittext);

    // 4. Access the ListView
    mainListView = findViewById(R.id.main_listview);

    // 5. Set this activity to react to list items being pressed
    mainListView.setOnItemClickListener(this);

    // 7. Greet the user, or ask for their name if new
    displayWelcome();

    // 10. Create a JSONAdapter for the ListView
    jsonAdapter = new JSONAdapter(this, getLayoutInflater());

    // 11. Add a spinning progress bar (and make sure it's off)
    progressBar = findViewById(R.id.progress_bar);

    // 12. Get the view that signals there are no results
    emptyView = findViewById(R.id.empty_view);

    // Set the ListView to use the ArrayAdapter
    mainListView.setAdapter(jsonAdapter);
  }

  public void displayWelcome() {

    // Access the device's key-value storage
    sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

    // Read the user's name,
    // or an empty string if nothing found
    String name = sharedPreferences.getString(PREF_NAME, "");

    if (name.length() > 0) {

      // If the name is valid, display a Toast welcoming them
      Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
    } else {

      // otherwise, show a dialog to ask for their name
      AlertDialog.Builder alert = new AlertDialog.Builder(this);
      alert.setTitle("Hello!");
      alert.setMessage("What is your name?");

      // Create EditText for entry
      final EditText input = new EditText(this);
      input.setLines(1);
      input.setMaxLines(1);
      input.setInputType(InputType.TYPE_CLASS_TEXT);
      alert.setView(input);

      // Make an "OK" button to save the name
      alert.setPositiveButton("OK",
          new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,
                                int whichButton) {

              // Grab the EditText's input
              String inputName = input.getText().toString();

              // Put it into memory (don't forget to commit!)
              SharedPreferences.Editor e = sharedPreferences.edit();
              e.putString(PREF_NAME, inputName);
              e.apply();

              // Welcome the new user
              Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!",
                  Toast.LENGTH_LONG).show();
            }
          });

      // Make a "Cancel" button
      // that simply dismisses the alert
      alert.setNegativeButton("Cancel",
          new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
            }
          });

      alert.show();
    }
  }

  @Override
  public void onClick(View v) {

    // 9. Take what was typed into the EditText and use in search
    queryBooks(mainEditText.getText().toString());

    InputMethodManager inputManager = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }

  @Override
  public void onItemClick(AdapterView<?> parent,
                          View view, int position, long id) {

    // 12. Now that the user's chosen a book, grab the cover data
    String coverID =
        jsonAdapter.getItem(position).optString("cover_i", "");

    // create an Intent to take you over to a new DetailActivity
    Intent detailIntent = new Intent(this, DetailActivityKotlin.class);

    // pack away the data about the cover
    // into your Intent before you head out
    detailIntent.putExtra("coverID", coverID);

    // start the next Activity using your prepared Intent
    startActivity(detailIntent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu.
    // Adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);

    // Access the Share Item defined in menu XML
    MenuItem shareItem = menu.findItem(R.id.menu_item_share);

    // Access the object responsible for
    // putting together the sharing submenu
    if (shareItem != null) {
      shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    }

    // Create an Intent to share your content
    setShareIntent();

    return true;
  }

  private void setShareIntent() {

    // create an Intent with the contents of the TextView
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
    shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

    // Make sure the provider knows
    // it should work with that Intent
    shareActionProvider.setShareIntent(shareIntent);
  }

  private void queryBooks(String searchString) {

    // Prepare your search string to be put in a URL
    // It might have reserved characters or something
    String urlString = "";
    try {
      urlString = URLEncoder.encode(searchString, "UTF-8");
    } catch (UnsupportedEncodingException e) {

      // if this fails for some reason, let the user know why
      e.printStackTrace();
      Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // Create a client to perform networking
    AsyncHttpClient client = new AsyncHttpClient();

    // 11. start progress bar
    progressBar.setIndeterminate(true);

    // Have the client get a JSONArray of data
    // and define how to respond
    client.get(QUERY_URL + urlString, new JsonHttpResponseHandler() {

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {

        // 11. stop progress bar
        progressBar.setIndeterminate(false);

        // Display a "Toast" message
        // to announce your success
        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

        // update the data in your custom method.
        JSONArray array = jsonObject.optJSONArray("docs");
        if(array != null && array.length() > 0) {
          emptyView.setVisibility(View.GONE);
          mainListView.setVisibility(View.VISIBLE);
        } else {
          emptyView.setVisibility(View.VISIBLE);
          mainListView.setVisibility(View.GONE);
        }
        jsonAdapter.updateData(array);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {

        // 11. stop progress bar
        progressBar.setIndeterminate(false);

        // Display a "Toast" message
        // to announce the failure
        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " "
            + throwable.getMessage(), Toast.LENGTH_LONG).show();
        emptyView.setVisibility(View.VISIBLE);
        mainListView.setVisibility(View.GONE);
        // Log error message
        // to help solve any problems
        Log.e("omg android", statusCode + " " + throwable.getMessage());
      }
    });
  }
}