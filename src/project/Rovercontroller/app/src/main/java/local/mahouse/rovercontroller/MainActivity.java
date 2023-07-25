package local.mahouse.rovercontroller;

import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import local.mahouse.rovercontroller.ui.home.HomeFragment;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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
                AtomicBoolean failed = new AtomicBoolean(false);
                //Spaguetti code!!!
                if(Singleton.isConnected() == false) {

                    //Correm el codi que controla la xarxa en un fil separat
                    new Thread(() -> {
                        try {
                            Singleton.connect(addr); //Provem de connectar
                        } catch (NoRouteToHostException e) {
                            failed.set(true);
                            runOnUiThread(() -> //Run toast on (main) thread
                                    Toast.makeText(this, getText(R.string.error_NoRouteToHostException), Toast.LENGTH_LONG).show());
                        } catch (ConnectException e) {
                            failed.set(true);
                            runOnUiThread(() -> //So we can create a toast notification
                                    Toast.makeText(this, getText(R.string.error_ConnectException), Toast.LENGTH_LONG).show());


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    //If we don't fail to connect
                    if (failed.get() == false) {
                        item.setTitle(R.string.disconnect_button);
                        Snackbar.make(getWindow().getDecorView(), getText(R.string.connected) + addr,
                                Snackbar.LENGTH_LONG).show();
                    }else{
                        Singleton.setConnected(false);
                    }

                }else{
                    item.setTitle(R.string.connect_button);
                    try {
                        //Correm el codi que controla la xarxa en un fil separat
                        new Thread(() -> {
                            try {
                                Singleton.disconnect();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                        Snackbar.make(getWindow().getDecorView(), getText(R.string.disconnected),
                                Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                        Snackbar.make(getParent().getCurrentFocus(), getText(R.string.error_IOException), BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}