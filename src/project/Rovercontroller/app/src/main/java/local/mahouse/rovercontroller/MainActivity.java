package local.mahouse.rovercontroller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import local.mahouse.rovercontroller.ui.home.HomeFragment;
import local.mahouse.rovercontroller.discover.client.DiscoveryClient;




public class MainActivity extends AppCompatActivity {
    static interface Callback {
        void onComplete(String result);
    }
    private AppBarConfiguration mAppBarConfiguration;

    //For getting notifications on Android 13 and higher
    final int PERMISSION_REQUEST_CODE = 112;

    //Inicialitzem el Singleton
    Singleton mSingleton = Singleton.getInstance();
    //Used for setting it's visibility
    MenuItem shutdown;
    MenuItem search;
    //Used for setting it's text from the shutdown button
    MenuItem conDis;

    NotificationManagerCompat notifyManCompat;
    NotificationCompat.Builder notiBuild;

    //We create a new ExecutorService
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    Executor executor = new Executor() {
        @Override
        public void execute(Runnable runnable) {
            executorService.execute(runnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //M'ho havia deixat, F
        createNotificationChannel();
        getNotificationPermission();
        notifyManCompat = NotificationManagerCompat.from(this);
        notiBuild = new NotificationCompat.Builder(this, "ROVER_CONTROLLER_NOTIFICATIONS");
        notiBuild.setSmallIcon(R.drawable.baseline_android_24);

        //Send notification
        //notifyManCompat.notify(0, notiBuild.build());


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

                        su = mSingleton.connect(addr, executor);
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
                                        search.setVisible(true);
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
                String address;
                ProgressDialog progress = new ProgressDialog(this);
                Context forWrite = this;

                Callback callback = new Callback() {
                    @Override
                    public void onComplete(String result) {
                        progress.dismiss();



                        //All this to update the IP address
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HomeFragment.setEnterIP(result);
                                notiBuild.setContentTitle(getText(R.string.title_notification))
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(null)
                                        .setAutoCancel(true);
                                if(result != "") {
                                    notiBuild.setContentText(getText(R.string.text_success_notification));
                                    mSingleton.writeToPreference(forWrite, "robot_ip_address", result);
                                } else {
                                    notiBuild.setContentText(getText(R.string.text_failure_notification));
                                }
                                notifyManCompat.notify(0, notiBuild.build());
                            }
                        });
                    }
                };

                //We create the progress dialog
                progress.setTitle(R.string.title_in_search);
                progress.setMessage(getText(R.string.in_search));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMax(255);
                progress.setCancelable(false);
                progress.show();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(DiscoveryClient.main());
                    }
                });

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



    //Creem el canal de notoficacions
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ROVER_CONTROLLER_NOTIFICATIONS", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //From https://stackoverflow.com/a/75354948
    public void getNotificationPermission(){
        try {
            if (Build.VERSION.SDK_INT > 32) {
                System.out.println("Running on Android 13 or higher");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow

                }  else {
                    //deny
                }
                return;

        }

    }


}