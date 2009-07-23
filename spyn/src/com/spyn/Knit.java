package com.spyn;

/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for NotePadProvider
 */
public final class Knit {
    public static final String AUTHORITY = "com.spyn.db.Knit";

    // This class cannot be instantiated
    private Knit() {}
    
    /**
     * Knit Memories table
     */
    public static final class Memories implements BaseColumns {
        // This class cannot be instantiated
        private Memories() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/memories");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of memories.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.memory";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single memory.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.memory";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The title of the memory
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The body of the memory
         * <P>Type: TEXT</P>
         */
        
        public static final String BODY = "body";


        /**
         * The timestamp for when the memory was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the memory was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
        
        /**
         * The location of the memory
         * <P>Type: TEXT</P>
         */
        
        public static final String LOCATION = "location";
        
        /**
         * The latitude of the memory
         * <P>Type: DOUBLE</P>
         */
        
        public static final String LOCATION_LAT = "latitude";
        
        /**
         * The longitude of the memory
         * <P>Type: DOUBLE</P>
         */
        
        public static final String LOCATION_LON = "longitude";
        
        /**
         * The knit id of the memory
         * <P>Type: INTEGER</P>
         */
        
        public static final String KNIT_ID = "knit";
        
        /**
         * The photo count of the memory
         * <P>Type: INTEGER</P>
         */
        
        public static final String PHOTO = "photo";
        
        /**
         * The audio count of the memory
         * <P>Type: INTEGER</P>
         */
        
        public static final String AUDIO = "knit";
        
        /**
         * The video count of the memory
         * <P>Type: INTEGER</P>
         */
        
        public static final String VIDEO = "knit";
    }
    
    public static final class Knits implements BaseColumns {
        // This class cannot be instantiated
        private Knits() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/memories");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of memories.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.knit";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single knit.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.knit";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The title of the knit
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The body of the knit
         * <P>Type: TEXT</P>
         */
        
        public static final String BODY = "body";


        /**
         * The timestamp for when the knit was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the knit was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
        
        /**
         * The location of the knit
         * <P>Type: TEXT</P>
         */
        
        
        /**
         * The photo count of the knit
         * <P>Type: INTEGER</P>
         */
        
        public static final String PHOTO = "photo";
        
        /**
         * The audio count of the knit
         * <P>Type: INTEGER</P>
         */

    }
}
