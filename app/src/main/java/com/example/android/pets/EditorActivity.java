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
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

import java.io.IOException;

import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_PET_GENDER;
import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_PET_WEIGHT;
import static com.example.android.pets.data.PetContract.PetEntry.CONTENT_URI;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /** Loader initialization  */
    private static final int PET_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentPetUri;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;


    /** Tag for log messages */
    private static final String LOG_TAG = EditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentPetUri = this.getIntent().getData();

        Log.e(LOG_TAG, "*******************Passed URI is:" + mCurrentPetUri);

        if(mCurrentPetUri != null) {
            setTitle(R.string.editor_activity_title_edit_pet);
        }else {
            setTitle(R.string.editor_activity_title_new_pet);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private void insertPet(){

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String nameString = mNameEditText.getText().toString().trim();
            String breeString = mBreedEditText.getText().toString().trim();
            String weightString = mWeightEditText.getText().toString().trim();
            int weightInt = Integer.parseInt(weightString);

            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();

            values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameString);
            values.put(PetContract.PetEntry.COLUMN_PET_BREED, breeString);
            values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender);
            values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weightInt);

            // Defines a new Uri object that receives the result of the insertion
            Uri mNewUri = getContentResolver().insert(
                    CONTENT_URI,                        // the user PetEntry content URI
                    values                              // the values to insert
            );


            // Show a toast message depending on whether or not the insertion was successful
            if (mNewUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet data into the database
                insertPet();

                //Exit the activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains.
        // all columns from the pet table
        String[] PROJECTION = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        };

            /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case PET_LOADER:
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                // This loader will execute the ContentProvider's query method on
                // a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentPetUri,         // Query the content URI for the current pet
                        PROJECTION,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null                    // Default sort order
                );

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
         // Bail early if the cursor is null or there is less than 1 row in the cursor
         if (cursor == null || cursor.getCount() < 1) {
             return;
         }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if(cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String currentName = cursor.getString(nameColumnIndex);
            String currentBreed = cursor.getString(breedColumnIndex);
            int currentGender = cursor.getInt(genderColumnIndex);
            int currentWeight = cursor.getInt(weightColumnIndex);


            Log.e(LOG_TAG, "*******************NAME is:" + currentName);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(currentName);
            mBreedEditText.setText(currentBreed);
            mWeightEditText.setText(Integer.toString(currentWeight));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (currentGender) {
                case PetContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }
}