package edu.vandy.mooc.aad_3_assg_3.assignment.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import edu.vandy.mooc.aad_3_assg_3.framework.provider.FeedContract;
import edu.vandy.mooc.aad_3_assg_3.framework.provider.FeedDBAdapter;

/**
 * The Content Provider for this application.
 * <p/>
 * Content providers are one of the primary building blocks of Android
 * applications, providing content to applications. They encapsulate data and
 * provide it to applications through the single ContentResolver interface. A
 * content provider is only required if you need to share data between multiple
 * applications. For example, the contacts data is used by multiple applications
 * and must be stored in a content provider. If you don't need to share data
 * amongst multiple applications you can use a database directly via
 * SQLiteDatabase.
 * <p>
 * Note: this provider is functional, but not complete. There is more that can be added to it to
 * give it additional features. This assignment provides a means for you to become familiar with
 * Content Providers. It is not an example of finished code that would be used in production.
 * Additionally, the hardest part about Content Providers has already been provided for you: The
 * underlying DatabaseAdapter( {@link FeedDBAdapter} ) & Contract ( {@link FeedContract} ). If
 * you wish to gain the ability to write a Content Provider from scratch, then delete
 * FeedDBAdapter, and try re-implementing it. (There are Integration Tests provided that test
 * that class + the provider tests indirectly test it too).
 *
 * @author Michael A. Walker
 */
@SuppressWarnings({"UnusedParameters", "SameParameterValue"})
public class FeedProvider extends ContentProvider {

    // logging tag.
    private final static String LOG_TAG = FeedProvider.class.getCanonicalName();

    // shorten variable names for easier readability
    private static final int ALL_ENTRY_ROWS = FeedContract.Entry.PATH_TOKEN;
    private static final int SINGLE_ENTRY_ROW = FeedContract.Entry.PATH_FOR_ID_TOKEN;
    private static final UriMatcher mUriMatcher = FeedContract.URI_MATCHER;

    // The Adapter to the Database.
    private FeedDBAdapter mDB;

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the application
     * main thread at application launch time. It must not perform lengthy operations,
     * or application startup will be delayed.
     */
    @Override
    synchronized public boolean onCreate() {
        // initialize mDB, call open() on it, and return true.
        mDB = (new FeedDBAdapter(getContext())).open();
        return true;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the given URI.
     * The returned MIME type should start with vnd.android.cursor.item for a single record,
     * or vnd.android.cursor.dir/ for multiple items. This method can be called from multiple
     * threads, as described in Processes and Threads.
     */
    @Override
    synchronized public String getType(@NonNull Uri uri) {
        // Based on the Uri passed in, return the appropriate ContentType (Found in FeedContract)
        // I recommend you use a Switch statement.
        // You will need to use 'mUriMatcher' to match if the Uri matches SingleRow or MultipleRow
        // throw a new 'UnsupportedOperationException("URI: " + uri + " is not supported.")
        // if mUriMatcher returns an unsupported Uri.
        // TODO: Not so sure about the instructions
        switch (mUriMatcher.match(uri)) {
            case ALL_ENTRY_ROWS:
                return "vnd.android.cursor.dir/";
            case SINGLE_ENTRY_ROW:
                return "vnd.android.cursor.item";
            default:
                throw new UnsupportedOperationException("URI: " + uri + " is not supported.");
        }
    }


    /**
     * Retrieve data from your provider. Use the arguments to select the table to query,
     * the rows and columns to return, and the sort order of the result. Return the data as
     * a Cursor object.
     */
    @Override
    synchronized public Cursor query(@NonNull final Uri uri, final String[] projection,
                                     final String selection, final String[] selectionArgs,
                                     final String sortOrder) {
        // based on the parameters given, you need to call the private query(...) method with the
        // correct values. I recommend using a switch statement.
        // You will need to use 'mUriMatcher' to match if the Uri matches SingleRow or MultipleRow
        // if mUriMatcher.match(...) returns UriMatcher.NO_MATCH, then you should throw a
        // new IllegalArgumentException("Invalid URI").

        Log.d(LOG_TAG, "query(...) Uri: " + uri);

        // create copies of selection and sortOrder.
        String modifiedSelection = selection;
        String updatedSortOrder = sortOrder;

        // switch on match() from mUriMatcher
        switch (mUriMatcher.match(uri)) {
            // if SINGLE_ROW & selection is null, then set modifiedSelection to:
            // "_ID = " + uri .getLastPathSegment()
            // else make it:
            // modifiedSelection += " AND " + <the same as above>
            case SINGLE_ENTRY_ROW:
                if (selection == null)
                    modifiedSelection = "_ID = " + uri.getLastPathSegment();
                else
                    modifiedSelection += " AND " + "_ID = " + uri.getLastPathSegment();
                return this.query(uri, FeedContract.Entry.TABLE_NAME, projection, modifiedSelection, selectionArgs, updatedSortOrder);
            // if matcher matches ALL_ROWS,
            // if sortOrder is empty, then set new copy of sortOrder to "_ID ASC"
            // then call private query method with appropriate parameters(including new copy of
            // sortOrder).
            case ALL_ENTRY_ROWS:
                if (sortOrder == null)
                    updatedSortOrder = "_ID ASC";
                return this.query(uri, FeedContract.Entry.TABLE_NAME, projection, selection, selectionArgs, updatedSortOrder);
            // if matcher matches UriMatcher.NO_MATCH or default,
            // then throw new IllegalArgumentException("Invalid URI")
            default:
                throw new IllegalArgumentException("Invalid URI");
        }
    }

    /**
     * Private query that does the actual query based on the table.
     * <p>
     * This method makes use of SQLiteQueryBuilder to build a simple query.
     */
    synchronized private Cursor query(final Uri uri, final String tableName,
                                      final String[] projection, final String selection,
                                      final String[] selectionArgs, final String sortOrder) {
        // Make a new SQLiteQueryBuilder object.
        SQLiteQueryBuilder sqlLiteQueryBuilder = new SQLiteQueryBuilder();

        // set the table(s) to be queried upon.
        sqlLiteQueryBuilder.setTables(tableName);

        // return the builder.query(....) result, after passing the appropriate values.
        return sqlLiteQueryBuilder.query(this.mDB.getDB(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     * Implement this to handle requests to insert a new row. As a courtesy,
     * call notifyChange() after inserting. This method can be called from multiple threads,
     * as described in Processes and Threads.
     * <p>
     * (non-Javadoc)
     *
     * @see ContentProvider#insert(Uri, ContentValues)
     */
    @Override
    synchronized public Uri insert(@NonNull Uri uri, ContentValues assignedValues) {

        // switch on the results of 'mUriMatcher' matching the Uri passed in,
        switch (mUriMatcher.match(uri)) {

            // if the match equals a PATH TOKEN for ALL ROWS of a table in the FeedContract
            case ALL_ENTRY_ROWS:

                // create a ContentValues, then remove the '_ID' value from it.
                ContentValues contentValues = assignedValues;
                contentValues.remove("_ID");

                // insert the ContentValues into the database adapater instance and store the
                // returned rowID.
                Long rowID = mDB.insert(FeedContract.Entry.TABLE_NAME, contentValues);
                Log.i(LOG_TAG, "Inserted " + rowID + ".");
                // if returned rowID is < 0, then return null.
                if (rowID < 0) {
                    return null;
                }

                // Use ContentUris.withAppendedId(Uri, long) to create a new Uri.
                // use the appropriate table's CONTENT_URI and the rowID.
                Uri newUri = ContentUris.withAppendedId(uri, rowID);

                // Call notifyChanges on this, with the new Uri, and a null content observer
                // This calls notifyChange(Uri, ContentObserver) in setup Activities.
                this.notifyChanges(newUri, null);

                // Return the notification Uri
                return newUri;
            // in both cases: 1) a Single Row URI was passed, or 2) default case: throw a new
            //  IllegalArgumentException("Unsupported URI, unable to insert into specific row: "
            //        + uri);
            default:
                throw new IllegalArgumentException("Unsupported URI, unable to insert into specific row: " + uri);
        }
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     */
    @Override
    synchronized public int delete(@NonNull Uri uri,
                                   @Nullable String whereClause,
                                   @Nullable String[] whereArgs
    ) throws IllegalArgumentException {
        // copy of whereClause to modify if needed.
        String modifiedWhereClause = whereClause;

        // switch on mUriMatcher.match
        int uriMatchedValue = mUriMatcher.match(uri);
        Log.i(FeedProvider.LOG_TAG, "Delete path resolved " + uriMatchedValue + ".");
        switch (uriMatchedValue) {
            // if mUriMatcher equals a Single row of table, then modify the whereClause:
            // If whereClause is null, then set modifiedWhereClause to _ID = last path segement
            // If whereClause is nonNull, then append " AND " and then the same as above.
            // do not 'break' at the end of the this switch.('fall though' to the multi-row case)
            case SINGLE_ENTRY_ROW:
                if (whereClause == null) {
                    modifiedWhereClause = "_ID = " + uri.getLastPathSegment();
                } else {
                    modifiedWhereClause += " AND " + "_ID = " + uri.getLastPathSegment();
                }
            case ALL_ENTRY_ROWS:
            // if whereClause was modified or mUriMatcher equals ALL ROWS of table, then call
            // deleteAndNotify with the proper parameters. (including modifiedWhereClause)
                return deleteAndNotify(uri, FeedContract.Entry.TABLE_NAME, modifiedWhereClause, whereArgs);
            // default: throw new IllegalArgumentException("Unsupported URI: " + uri)
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /*
     * Private method to both attempt the delete command, and then to notify of
     * the changes
     */
    private int deleteAndNotify(final Uri uri, final String tableName,
                                final String whereClause, final String[] whereArgs) {
        // call delete on DBAdapter instance, and store the int number or rows deleted.
        int rowsDeleted = mDB.getDB().delete(tableName, whereClause, whereArgs);

        // if count > 0, then call notifyChange on the Uri provided. (with null observer)
        // then return the count.
        if (rowsDeleted > 0) {
            notifyChanges(uri, null);
            return rowsDeleted;
        } else
            return 0;
    }


    /**
     * Implement this to handle requests to update one or more rows.
     */
    @Override
    synchronized public int update(@NonNull Uri uri,
                                   @NonNull ContentValues values,
                                   @Nullable String whereClause,
                                   @Nullable String[] whereArgs
    ) throws IllegalArgumentException {

        // WhereClause copy for use in modifing the whereClause
        String modifedWhereClause = whereClause;

        // remove "_ID" from the content values.
        ContentValues contentValues = values;
        contentValues.remove("_ID");

        // switch based on the uri provided using mUriMatcher
        switch (mUriMatcher.match(uri)) {
            // if uri matches single row of table, modify whereClause appropriately:
            // If whereClause is null, then set modifiedWhereClause to _ID = last path segement
            // If whereClause is nonNull, then append " AND " and then the same as above.
            case SINGLE_ENTRY_ROW:
                if (whereClause == null)
                    modifedWhereClause = "_ID = " + uri.getLastPathSegment();
                else
                    modifedWhereClause += " AND " + "_ID = " + uri.getLastPathSegment();
            // if whereClause was modified, or if Uri matches all rows of a table, then call
            // updateAndNotify(...) with appropriate parameters.
            case ALL_ENTRY_ROWS:
                return updateAndNotify(uri, FeedContract.Entry.TABLE_NAME, contentValues, modifedWhereClause, whereArgs);
            // default: throw new IllegalArgumentException("Unknown URI " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /*
     * private update function that updates based on parameters, then notifies
     * change
     */
    private int updateAndNotify(@NonNull final Uri uri, final String tableName,
                                final ContentValues values, final String whereClause,
                                final String[] whereArgs) {
        // call update(...) on the DBAdapter instance variable, and store the count of rows updated.
        int updatedRows = this.mDB.getDB().update(tableName, values, whereClause, whereArgs);

        // if count > 0 then call notifyChanges(...) on the Uri (null observer), and return the
        // count.
        if (updatedRows > 0) {
            notifyChanges(uri, null);
            return updatedRows;
        } else
            return 0;
    }

    private void notifyChanges(Uri uri, ContentObserver contentObserver) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            return;
        }
        resolver.notifyChange(uri, null);
    }

}
