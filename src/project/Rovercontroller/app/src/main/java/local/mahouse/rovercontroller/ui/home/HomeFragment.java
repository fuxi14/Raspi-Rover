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
import local.mahouse.rovercontroller.Singleton;

public class HomeFragment extends Fragment {

    static EditText enterIP = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);//You f*ucking idiot


        //Inicialitzem les vistes
        enterIP = homeFragment.findViewById(R.id.editTextIPAddress);


        //Escoltem si es clica el botó i fem acció


       /*
        * Resulta que l'error:
        * java.lang.NullPointerException: Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference
        * Estava provocat pel fet que els fragments no retornaven la "pantalla" que fan servir i per tant a l'hora d'intentar
        * inicialitzar qualsevol vista fallava perquè aquesta no existia :)
        * */
        return homeFragment; //You f*ucking idiot
    }

    public static String getIPText() {
        return enterIP.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}