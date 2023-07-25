package local.mahouse.rovercontroller.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import local.mahouse.rovercontroller.R;
public class GalleryFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View galleryFragment = inflater.inflate(R.layout.fragment_gallery, container, false);//You f*ucking idiot

        //Inicialitzem els objectes en pantalla i l'adaptador per la llista
        final Button send = galleryFragment.findViewById(R.id.btnSend);
        final EditText message = galleryFragment.findViewById(R.id.editTextMessage);
        final ListView list = galleryFragment.findViewById(R.id.listMessages);

        //Inicialitzem l'adaptador
        ArrayList<String> strMessages = new ArrayList<String>();
        ArrayAdapter<String> result = new ArrayAdapter<String>(galleryFragment.getContext(), android
                .support
                .v7
                .appcompat
                .R
                .layout.support_simple_spinner_dropdown_item, strMessages);
        list.setAdapter(result);
        result.notifyDataSetChanged();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strMessages.add(message.getText().toString());
                result.notifyDataSetChanged();
            }
        });







        return galleryFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}