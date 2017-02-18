package edu.vandy.mooc.aad_3_assg_3.assignment.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import edu.vandy.mooc.aad_3_assg_3.R;
import edu.vandy.mooc.aad_3_assg_3.assignment.services.DownloadAtomFeedService;
import edu.vandy.mooc.aad_3_assg_3.assignment.services.utils.ServiceResult;
import edu.vandy.mooc.aad_3_assg_3.assignment.services.utils.ServiceResultHandler;
import edu.vandy.mooc.aad_3_assg_3.framework.Interfaces.UpdateEntriesInterface;
import edu.vandy.mooc.aad_3_assg_3.framework.common.activities.CustomLoggingActivityBase;
import edu.vandy.mooc.aad_3_assg_3.framework.common.logger.Log;
import edu.vandy.mooc.aad_3_assg_3.framework.common.logger.LogFragment;
import edu.vandy.mooc.aad_3_assg_3.framework.common.logger.LogWrapper;
import edu.vandy.mooc.aad_3_assg_3.framework.common.logger.MessageOnlyLogFilter;
import edu.vandy.mooc.aad_3_assg_3.framework.orm.Entry;

/**
 * Main Activity of the Application containing:
 * <p><ul>
 * <li>A summary of the Application.
 * <li>A toggleable simple fragment that displays the UI's log statements
 * <li>A Fragment containing a RecyclerView that displays a list of metadata about YouTube videos.
 * </ul><p>
 */
public class MainActivity extends CustomLoggingActivityBase implements ServiceResult {

    /**
     * String used for logging.
     */
    private static final String TAG = MainActivity.class.getCanonicalName();

    /**
     * reference to RecyclerViewFragment implementing this interface.
     */
    private UpdateEntriesInterface updateEntriesInterface;

    /**
     * This is CNN's Youtube Atom Feed:
     */
    @SuppressWarnings("SpellCheckingInspection") // IDE is over-zealous on spelling like this.
    private final static String CNN_YOUTUBE_ATOM_FEED_URL = "https://www.youtube.com"
            + "/feeds/videos.xml?channel_id=UCupvZG-5ko_eiXAupbDfxWw";

    /**
     * Whether the Log Fragment is currently shown
     */
    private boolean mLogShown;

    /**
     * View flipper & its support to show and hide a progressBar.
     */
    private ViewFlipper viewFlipper;
    private int mRecyclerFlipperIndex;
    private int mProgressFlipperIndex;

    /**
     * variable we use within the Main Activity to 'flag' what kind of request is being returned
     * to us. This is less useful when an Activity only requests 1 thing. However, as soon as an
     * Activity starts requesting more than one thing, it becomes crucial to know what request
     * type is being returned. Therefore, it is best to get into the practice of always using
     * request variables like this.
     */
    private final static int REQUEST_YOUTUBE_ENTRIES = 1;

    /**
     * Stores an instance of ServiceResultHandler.
     */
    private Handler mServiceResultHandler = null;

    /*
     * Android Activity Lifecycle methods
     */

    /**
     * the 'onCreate' Activity Lifecycle method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFragment(savedInstanceState);
        setupFAB();
        mServiceResultHandler = new ServiceResultHandler(this);

        // Setup the view flipper.
        setupViewFlipper();
        // create new thread to do the SQL activity NOT on the UI thread. Then fill the Display
        // Fragment with the results (if there is any).
        Thread thread = new Thread() {
            @Override
            public void run() {
                ProviderUtils.loadFragmentFromProvider(MainActivity.this, updateEntriesInterface);
            }
        };
        thread.start();
    }

    /**
     * Generates Strings for the Content Provider & the RecyclerView's adapter. This data would
     * usually come from something more meaningful such as from a local content provider or
     * a remote server. However, we have provided this method for testing purposes.
     */
    private void fillProvider() {
        android.util.Log.d(TAG, "FillProvider");
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Entry newEntry = new Entry();
            newEntry.AUTHOR = "Author " + i;
            newEntry.TITLE = "Title: " + i;
            newEntry.PUBLISHED = "1999-12-31:11:59";
            entries.add(newEntry);
        }
        ProviderUtils.addEntriesToProvider(this, entries);
    }

    /**
     * Sets up the view flipper which swaps the currently displayed view
     * between a progressBar and the recycler layout.
     */
    private void setupViewFlipper() {
        View view = findViewById(android.R.id.content);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        View recyclerFragment = viewFlipper.findViewById(R.id.recyclerFragment);
        View progressBar = viewFlipper.findViewById(R.id.progressBar);
        mRecyclerFlipperIndex = viewFlipper.indexOfChild(recyclerFragment);
        mProgressFlipperIndex = viewFlipper.indexOfChild(progressBar);
    }

    /**
     * Hook method called when a configuration change is about to occur.
     *
     * @param outState the Bundle which stores the state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Entry> entries = updateEntriesInterface.getEntries();
        outState.putParcelableArrayList("Entries", entries);
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called after {@link #onStart} when the activity is being
     * re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.
     *
     * @param savedInstanceState the data most recently supplied in {@link
     *                           #onSaveInstanceState}.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (updateEntriesInterface != null) {
            ArrayList<Entry> entries =
                    savedInstanceState.getParcelableArrayList("Entries");
            updateEntriesInterface.updateEntries(entries);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Start a download.
     */
    private void startDownload(Uri url) {
        // Create an intent to download the YouTube Atom Feed for CNN.
        Intent intent = DownloadAtomFeedService.makeIntent(
                this.getApplicationContext(),
                1001,
                Uri.parse(MainActivity.CNN_YOUTUBE_ATOM_FEED_URL),
                mServiceResultHandler);

        Log.d(TAG,
                "starting the DownloadAtomFeedService for "
                        + url.toString()
        );

        // Show the indeterminate progressBar spinner.
        viewFlipper.setDisplayedChild(mProgressFlipperIndex);

        // call startService on that Intent.
        this.startService(intent);
    }

    /**
     * Called when a launched Service sends back results from
     * computations it runs, giving the requestCode it was started
     * with, the resultCode it returned, and any additional data from
     * it.  The resultCode will be RESULT_CANCELED if the Service
     * explicitly returned that.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the Bundle containing the results of the Service call.
     */
    @Override
    public void onServiceResult(int requestCode, int resultCode, Bundle data) {

        // Show the recycler layout.
        viewFlipper.setDisplayedChild(mRecyclerFlipperIndex);

        // check if resultCode = Activity.RESULT_CANCELED, if it does, then call
        // handleDownloadFailure and return;
        if (resultCode == Activity.RESULT_CANCELED) {
            this.handleDownloadFailure(data);
            return;
        }

        String text = "";

        // Otherwise **resultCode == Activity.RESULT_OK**
        // Handle a successful download.
        // Log to both the on-screen & logcat logs the requestUri from the data.
        if (resultCode == Activity.RESULT_OK) {
            text = "Successfully downloaded from " + data.getString("FEED_URL") + ".";
            Log.i(MainActivity.TAG, text);
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

        // Get the Entries from the 'data' and store them.
        // Log to the on-screen and logcat logs the number of entries downloaded.
        final ArrayList<Entry> entries = data.getParcelableArrayList("ENTRY_ARRAY_KEY");
        text = "Downloaded " + entries.size() + " entries.";
        Log.i(MainActivity.TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

        // Update the RecyclerView fragment via calling updateEntries(...) on it.
        this.updateEntriesInterface.updateEntries(entries);

        Thread thread = new Thread() {
            @Override
            public void run() {
                ProviderUtils.deleteProviderContents(MainActivity.this);
                ProviderUtils.addEntriesToProvider(MainActivity.this, entries);
            }
        };
        thread.start();
    }

    /**
     * Handle failure to download an image.
     *
     * @param data the Bundle containing the download attempt's information.
     */
    private void handleDownloadFailure(Bundle data) {
        // Extract the URL from the message.
        final Uri url = DownloadAtomFeedService.getRequestUri(data);

        Toast.makeText(this,
                "Feed at :\n"
                        + url.toString()
                        + "\n failed to download!",
                Toast.LENGTH_LONG
        ).show();
    }

    /*
     * Fragment & FloatingActionButton setup
     */

    /**
     * Helper method to isolate the code for setting up the FAB (Floating Action Button).
     */
    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Sync FAB Pressed.");

                // call startDownload(...) with the parsed Uri version of
                // CNN_YOUTUBE_ATOM_FEED_URL
                startDownload(Uri.parse(MainActivity.CNN_YOUTUBE_ATOM_FEED_URL));
            }
        });
    }

    /**
     * Helper method to isolate the code for setting up the fragment(s).
     */
    @SuppressWarnings("UnusedParameters")
    private void setupFragment(Bundle savedInstanceState) {
        updateEntriesInterface = (UpdateEntriesInterface)
                getSupportFragmentManager().findFragmentById(R.id.recyclerFragment);
    }

    /**
     * Sets up the custom Logging display fragment that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

     /*
     * Menu hook methods.
     */

    /**
     * Create Options Menu
     *
     * @param menu Menu to create
     * @return true if menu is created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        android.util.Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {

            case R.id.action_fill_provider:
                fillProvider();
                return true;

            case R.id.action_delete_provider:
                Thread deleteThread = new Thread() {
                    @Override
                    public void run() {
                        ProviderUtils.deleteProviderContents(MainActivity.this);
                    }
                };
                deleteThread.start();
                return true;

            case R.id.action_read_provider:
                Thread readThread = new Thread() {
                    @Override
                    public void run() {
                        ProviderUtils.loadFragmentFromProvider(
                                MainActivity.this,
                                updateEntriesInterface
                        );
                    }
                };
                readThread.start();
                return true;

            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown)

                {
                    output.setDisplayedChild(1);
                } else

                {
                    output.setDisplayedChild(0);
                }

                supportInvalidateOptionsMenu();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
