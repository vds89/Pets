/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

import static android.text.TextUtils.isEmpty;
import static com.example.android.pets.data.PetContract.BASE_CONTENT_URI;
import static com.example.android.pets.data.PetContract.PATH_PETS;
import static com.example.android.pets.data.PetContract.PetEntry.CONTENT_URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies a particular Loader being used in this component
    private static final int PET_LOADER = 0;

    // This is the Adapter being used to display the list's data
    PetCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup cursor adapter using null cursor
        mAdapter = new PetCursorAdapter(this, null);
        // Sets the adapter for the view
        petListView.setAdapter(mAdapter);

        //Set up the click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When a Pet in the list is clicked on, open its Edit Tab
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Get the Intent that started Editor activity
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                //Form the content URI that represent the specific pet that was clicked on,
                //by appending the "id" (passed as an input to this method) onto the
                //{@link PetEntry#CONTENT_URI}.
                //For example, the URI will be content://com.example.android.pets/pets/2
                //if the pet with ID=2 was clicked on
                intent.setData(ContentUris.withAppendedId(CONTENT_URI, id));

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(PET_LOADER, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, "7 kg");

        // Insert the new row, returning the primary key value of the new row
        //long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);

        // Defines a new Uri object that receives the result of the insertion
        Uri mNewUri = getContentResolver().insert(
                CONTENT_URI,                        // the user PetEntry content URI
                values                              // the values to insert
        );
        if (mNewUri != null){
            // The insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                                Toast.LENGTH_SHORT).show();
        }
        //Log.v("CatalogActivity", "New row ID " + newRowId);
    }

    private void deleteAllPets(){

        // Call the ContentResolver to delete ALL the pet in the table.

        // Deletes the pet that match the selection criteria
        int mRowsDeleted = getContentResolver().delete(
                PetContract.PetEntry.CONTENT_URI,                     // the user dictionary content URI
                null,                    // the column to select on
                null                      // the value to compare to
        );

        // Show a toast message depending on whether or not the update was successful.
        if (mRowsDeleted == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_deleteAll_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] PROJECTION = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED
        };

            /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case PET_LOADER:
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(this, PetContract.PetEntry.CONTENT_URI,
                        PROJECTION, null, null, null);

            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(cursor);

    }

    @Override
    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
