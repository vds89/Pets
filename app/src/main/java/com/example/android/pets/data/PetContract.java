package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by pcvincenzo on 10/12/17.
 */

public final class PetContract {

    /* Inner class that defines the table contents of the Pets table */
    public static final class PetEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "pets";

        // Column with the id key into the pets table.
        public static final String _ID = BaseColumns._ID;
        // Column with the pet name.
        public static final String COLUMN_PET_NAME = "name";
        // Column with the pet breed.
        public static final String COLUMN_PET_BREED = "breed";
        // Column with the pet gender.
        public static final String COLUMN_PET_GENDER = "gender";
        // Column with the pet weight.
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Possible values for the pet gender.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /** Complete CONTENT_URI - inside each of the Entry classes in the contract,
         * we create a full URI for the class as a constant called CONTENT_URI.
         * The Uri.withAppendedPath() method appends the BASE_CONTENT_URI (which contains the scheme
         * and the content authority) to the path segment. */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

     /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PETS = "pets";

}
