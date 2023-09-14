package local.mahouse.rovercontroller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import local.mahouse.rovercontroller.ui.home.HomeFragment;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    //Inicialitzem el Singleton
    Singleton mSingleton = Singleton.getInstance();

    //Used for setting it's visibility
    MenuItem shutdown;
    MenuItem search;
    //Used for setting it's text from the shutdown button
    MenuItem conDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //M'ho havia deixat, F


        //Inicialitzem la barra d'eines i el botó flotant
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, getText(R.string.replace_with_action), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Maleït sigui el binding, el que m'ha donat de guerra
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        shutdown = menu.findItem(R.id.action_shutDown);
        search = menu.findItem(R.id.action_search);
        conDis = menu.findItem(R.id.action_con_dis);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_con_dis:

                String addr = HomeFragment.getIPText(); //Un fil separat no pot tocar les vistes d'un altre fil (main)
                System.out.println(addr);
                //Spaguetti code!!!
                if(mSingleton.isConnected() == false) {

                    try {
                        //Provem de connectar
                        Exception su;
                        su = mSingleton.connect(addr);
                        if (su != null) { //Sortim si ha hagut un error
                            throw su;
                        }
                        item.setTitle(R.string.disconnect_button);
                        Snackbar.make(getWindow().getDecorView(), getText(R.string.connected) + addr,
                                Snackbar.LENGTH_LONG).show();
                        shutdown.setVisible(true);
                        search.setVisible(false);




                    } catch (NoRouteToHostException e) {
                                Toast.makeText(this, getText(R.string.error_NoRouteToHostException), Toast.LENGTH_LONG).show();



                    } catch (ConnectException e) {
                        Toast.makeText(this, getText(R.string.error_ConnectException), Toast.LENGTH_LONG).show();




                    } catch (IOException e) {
                        Toast.makeText(this, getText(R.string.error_IOException), Toast.LENGTH_SHORT).show();



                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                }else{
                    item.setTitle(R.string.connect_button);
                    try {
                        mSingleton.disconnect();
                        shutdown.setVisible(false);
                        search.setVisible(true);
                        Snackbar.make(getWindow().getDecorView(), getText(R.string.disconnected),
                                Snackbar.LENGTH_LONG).show();
                        //Correm el codi que controla la xarxa en un fil separat

                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        Snackbar.make(getParent().getCurrentFocus(), getText(R.string.error_IOException), BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                    }

                }
                return true;
            case R.id.action_shutDown:
                if(mSingleton.isConnected()) {
                    //Create confirmation dialog
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    //Send shutdown request
                                    mSingleton.sendIt(new int[] {255, 0, 0, 0});

                                    //Wait a little bit so the server recieves that we want to shut it down
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    //Disconnect application
                                    conDis.setTitle(R.string.connect_button);
                                    try {
                                        mSingleton.disconnect();
                                        shutdown.setVisible(false);
                                        Snackbar.make(getWindow().getDecorView(), getText(R.string.disconnected),
                                                Snackbar.LENGTH_LONG).show();
                                        //Correm el codi que controla la xarxa en un fil separat

                                    } catch (IOException e) {
                                        //throw new RuntimeException(e);
                                        Snackbar.make(getParent().getCurrentFocus(), getText(R.string.error_IOException), BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                    }

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getText(R.string.confirm_shutdown)).setPositiveButton(getText(R.string.yes), dialogClickListener)
                            .setNegativeButton(getText(R.string.no), dialogClickListener).show();
                }
                return true;

            case R.id.action_settings:
                //Iniciem acivitat de configuració
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_search:
                //We create the progress dialog

                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle(R.string.title_in_search);
                progress.setMessage(getText(R.string.in_search));
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setMax(255);
                progress.setCancelable(false);
                progress.show();
                //Create confirmation dialog for starting search
                mSingleton.searchAddress();

                //Deprecated
                /*
                AlertDialog.Builder builder;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //We start the search

                                mSingleton.searchAddress(mainThread);
                                //progress.show();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }


                    }
                };

                 builder = new AlertDialog.Builder(this);
                builder.setMessage(getText(R.string.confirm_search)).setPositiveButton(getText(R.string.yes), dialogClickListener)
                        .setNegativeButton(getText(R.string.no), dialogClickListener).show(); */


                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



}