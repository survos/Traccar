
package com.survos.tracker.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.survos.tracker.BuildConfig;
import com.survos.tracker.TrackerApplication;


/**
 * Class that's used to form a layer between the Database and the Application
 * 
 * @author Anshul Kamboj
 */
public class DBInterface {

    private static final String TAG                 = "DBInterface";

    /**
     * Reference to the Async Query Handler for the Applicationm
     */
    private static final YeloAsyncQueryHandler ASYNC_QUERY_HANDLER = new YeloAsyncQueryHandler();

    /**
     * Insert a row into the database
     * 
     * @param table The table to insert into
     * @param nullColumnHack column names are known and an empty row can't be
     *            inserted. If not set to null, the nullColumnHack parameter
     *            provides the name of nullable column name to explicitly insert
     *            a NULL into in the case where your values is empty.
     * @param values The fields to insert
     * @param autoNotify <code>true</code> to automatically notify a change on
     *            the table
     * @return The row Id if inserted, -1 if not
     */
    public static long insert(final String table, final String nullColumnHack,
                    final ContentValues values, final boolean autoNotify) {
        throwIfOnMainThread();
        return YeloSQLiteOpenHelper
                        .getInstance(TrackerApplication.getStaticContext())
                        .insert(table, nullColumnHack, values, autoNotify);

    }

    /**
     * Async method for inserting rows into the database
     * 
     * @param taskId Unique id for this operation
     * @param tag A tag for task cancellation
     * @param cookie Any extra object to be passed into the query to be returned
     *            when the query completes. Can be <code>null</code>
     * @param table The table to insert into
     * @param nullColumnHack column names are known and an empty row can't be
     *            inserted. If not set to null, the nullColumnHack parameter
     *            provides the name of nullable column name to explicitly insert
     *            a NULL into in the case where your values is empty.
     * @param values The fields to insert
     * @param autoNotify Whether to automatically notify once the row is
     *            inserted
     * @param callback A {@link AsyncDbQueryCallback} to be notified when the
     *            async operation finishes
     */
    public static void insertAsync(final int taskId, final Object tag,
                    final Object cookie, final String table,
                    final String nullColumnHack, final ContentValues values,
                    final boolean autoNotify,
                    final AsyncDbQueryCallback callback) {
        logIfNotOnMainThread();
        ASYNC_QUERY_HANDLER
                        .startInsert(taskId,tag,cookie,table,nullColumnHack,values,autoNotify,callback);
    }

    /**
     * Updates the table with the given data
     *
     * @param table The table to update
     * @param values The fields to update
     * @param whereClause The WHERE clause
     * @param whereArgs Arguments for the where clause
     * @param autoNotify <code>true</code> to automatically notify a change on
     *            the table
     * @return The number of rows updated
     */
    public static int update(final String table, final ContentValues values,
                    final String whereClause, final String[] whereArgs,
                    final boolean autoNotify) {
        throwIfOnMainThread();
        return YeloSQLiteOpenHelper
                        .getInstance(TrackerApplication.getStaticContext())
                        .update(table, values, whereClause, whereArgs, autoNotify);

    }

    /**
     * Asynchronously updates the table with the given data
     *
     * @param A unique id for this operation
     * @param tag A tag for cancelling the request
     * @param cookie Any extra object to be passed into the query to be returned
     *            when the query completes. Can be <code>null</code>
     * @param table The table to update
     * @param values The fields to update
     * @param selection The WHERE clause
     * @param selectionArgs Arguments for the where clause
     * @param autoNotify Whether to automatically notify once the update is done
     * @param callback A {@link AsyncDbQueryCallback} to be notified when the
     *            async operation finishes
     */
    public static void updateAsync(final int taskId, final Object tag,
                    final Object cookie, final String table,
                    final ContentValues values, final String selection,
                    final String[] selectionArgs, final boolean autoNotify,
                    final AsyncDbQueryCallback callback) {
        logIfNotOnMainThread();
        ASYNC_QUERY_HANDLER
                        .startUpdate(taskId, tag, cookie, table, values, selection, selectionArgs, autoNotify, callback);
    }

    /**
     * Delete rows from the database
     *
     * @param table The table to delete from
     * @param whereClause The WHERE clause
     * @param whereArgs Arguments for the where clause
     * @param autoNotify <code>true</code> to automatically notify a change on
     *            the table
     * @return The number of rows deleted
     */
    public static int delete(final String table, final String whereClause,
                    final String[] whereArgs, final boolean autoNotify) {
        throwIfOnMainThread();
        return YeloSQLiteOpenHelper
                        .getInstance(TrackerApplication.getStaticContext())
                        .delete(table, whereClause, whereArgs, autoNotify);
    }

    /**
     * Asynchronously delete rows from the database
     *
     * @param taskId A unique id for this operation
     * @param tag A tag for tagging the task
     * @param cookie Any extra object to be passed into the query to be returned
     *            when the query completes. Can be <code>null</code>
     * @param table The table to delete from
     * @param selection The WHERE clause
     * @param selectionArgs Arguments for the where clause
     * @param autoNotify Whether to automatically notify once the delete
     *            operation is done
     * @param callback A {@link AsyncDbQueryCallback} to be notified when the
     *            async operation finishes
     */
    public static void deleteAsync(final int taskId, final Object tag,
                    final Object cookie, final String table,
                    final String selection, final String[] selectionArgs,
                    final boolean autoNotify,
                    final AsyncDbQueryCallback callback) {

        logIfNotOnMainThread();
        ASYNC_QUERY_HANDLER
                        .startDelete(taskId, tag, cookie, table, selection, selectionArgs, autoNotify, callback);
    }

    /**
     * Query the given URL, returning a Cursor over the result set.
     *
     * @param distinct <code>true</code> if dataset should be unique
     * @param table The table to query
     * @param columns The columns to fetch
     * @param selection The selection string, formatted as a WHERE clause
     * @param selectionArgs The arguments for the selection parameter
     * @param groupBy GROUP BY clause
     * @param having HAVING clause
     * @param orderBy ORDER BY clause
     * @param limit LIMIT clause
     * @return A {@link android.database.Cursor} over the dataset result
     */
    public static Cursor query(final boolean distinct, final String table,
                    final String[] columns, final String selection,
                    final String[] selectionArgs, final String groupBy,
                    final String having, final String orderBy,
                    final String limit) {
        throwIfOnMainThread();
        return YeloSQLiteOpenHelper
                        .getInstance(TrackerApplication.getStaticContext())
                        .query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * Asynchronously query the given table, returning a Cursor over the result
     * set.
     *
     * @param taskId A unique id for this query
     * @param tag An object for tagging tasks
     * @param cookie Any extra object to be passed into the query to be returned
     *            when the query completes. Can be <code>null</code>
     * @param distinct <code>true</code> if dataset should be unique
     * @param table The table to query
     * @param columns The columns to fetch
     * @param selection The selection string, formatted as a WHERE clause
     * @param selectionArgs The arguments for the selection parameter
     * @param groupBy GROUP BY clause
     * @param having HAVING clause
     * @param orderBy ORDER BY clause
     * @param limit LIMIT clause
     * @param callback A {@link AsyncDbQueryCallback} to be notified when the
     *            async operation finishes
     */
    public static void queryAsync(final int taskId, final Object tag,
                    final Object cookie, final boolean distinct,
                    final String table, final String[] columns,
                    final String selection, final String[] selectionArgs,
                    final String groupBy, final String having,
                    final String orderBy, final String limit,
                    final AsyncDbQueryCallback callback) {

        logIfNotOnMainThread();
        ASYNC_QUERY_HANDLER
                        .startQuery(taskId, tag, cookie, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, callback);

    }

    /**
     * Notify any loaders that the content of the table has changed
     *
     * @param tableName The table name that has been updated
     */
    public static void notifyChange(final String tableName) {
        YeloSQLiteOpenHelper
                        .getInstance(TrackerApplication.getStaticContext())
                        .notifyChange(tableName);
    }

    /**
     * Cancels any pending async db operations with a particular tag
     *
     * @param tag The tag for cancellation
     */
    public static void cancelAll(final Object tag) {
        ASYNC_QUERY_HANDLER.cancel(tag);
    }

    /**
     * Interface that represent callbacks for an asynchronous DB operation
     */
    public static interface AsyncDbQueryCallback {

        /**
         * Method called when an asynchronous insert operation is done
         *
         * @param taskId The token passed into the async mthod
         * @param cookie Any extra object passed into the query.
         * @param insertRowId The inserted row id, or -1 if it failed
         */
        public void onInsertComplete(final int taskId, final Object cookie,
                                     final long insertRowId);

        /**
         * Method called when an asynchronous delete operation is done
         *
         * @param taskId The token passed into the async method
         * @param cookie Any extra object passed into the query.
         * @param deleteCount The number of rows deleted
         */
        public void onDeleteComplete(final int taskId, final Object cookie,
                                     final int deleteCount);

        /**
         * Method called when an asynchronous update operation is done
         *
         * @param taskId The token passed into the async method
         * @param cookie Any extra object passed into the query.
         * @param updateCount The number of rows updated
         */
        public void onUpdateComplete(final int taskId, final Object cookie,
                                     final int updateCount);

        /**
         * Method called when an asyncronous query operation is done
         *
         * @param taskId The token passed into the async method
         * @param cookie Any extra object passed into the query.
         * @param cursor The {@link android.database.Cursor} read from the database
         */
        public void onQueryComplete(final int taskId, final Object cookie,
                                    final Cursor cursor);
    }

    /**
     * Checks if the current async database query is being made on the main
     * thread. If not, logs a warning
     */
    private static void logIfNotOnMainThread() {
        if (!Utils.isMainThread()) {
            Logger.w(TAG, "Performing async database query on a thread other than main thread. Are you sure you don't want to use the synchronous versions instead?");
        }
    }

    /**
     * Checks if the current database query is being made on the main thread. If
     * yes, either throws an Exception(if in DEBUG mode) or Logs an error(in
     * PRODUCTION mode)
     */
    private static void throwIfOnMainThread() {
        if (Utils.isMainThread()) {
            if (BuildConfig.DEBUG) {
                throw new RuntimeException("Accessing database on main thread! Use the async() versions of the methods.");
            } else {
                Logger.e(TAG, "Accessing database on main thread! Use the async() versions of the methods.");
            }
        }
    }
}
