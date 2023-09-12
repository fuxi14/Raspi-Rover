package local.mahouse.rovercontroller;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;


//Using as reference: https://web.archive.org/web/20181208100333/https://developer.android.com/reference/android/preference/PreferenceActivity
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.main_preferences);
    }

    /**
     * Populate the activity with the top-level headers.
     */
    /*
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    } */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
