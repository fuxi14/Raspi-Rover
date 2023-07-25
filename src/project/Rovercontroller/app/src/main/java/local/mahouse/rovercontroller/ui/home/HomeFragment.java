package local.mahouse.rovercontroller.ui.home;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;

import local.mahouse.rovercontroller.R;

public class HomeFragment extends Fragment {

    boolean connected = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);//You f*ucking idiot


        //Inicialitzem les vistes
        final EditText enterIP = homeFragment.findViewById(R.id.editTextIPAddress);
        final Button btnConDis = homeFragment.findViewById(R.id.btnConDis);

        //Escoltem si es clica el botó i fem acció
        btnConDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "IP Address inserted is: " + enterIP.getText().toString(),
                                Snackbar.LENGTH_LONG)
                        //.setAction("Action", null)
                        .show();

                if(connected == false) {
                    btnConDis.setText(getText(R.string.disconnect_button));
                    connected = true;
                }else{
                    btnConDis.setText(getText(R.string.connect_button));
                    connected = false;
                }
            }
        });

       /*
        * Resulta que l'error:
        * java.lang.NullPointerException: Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference
        * Estava provocat pel fet que els fragments no retornaven la "pantalla" que fan servir i per tant a l'hora d'intentar
        * inicialitzar qualsevol vista fallava perquè aquesta no existia :)
        * */
        return homeFragment; //You f*ucking idiot
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}