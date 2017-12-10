package com.example.android.pets.data;

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

    }

}
