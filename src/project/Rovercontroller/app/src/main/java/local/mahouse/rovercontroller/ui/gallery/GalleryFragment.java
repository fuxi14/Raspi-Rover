package local.mahouse.rovercontroller.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import local.mahouse.rovercontroller.R;
public class GalleryFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View galleryFragment = inflater.inflate(R.layout.fragment_gallery, container, false);//You f*ucking idiot



        final TextView textView = galleryFragment.findViewById(R.id.text_gallery);
        textView.setText("This is the gallery fragment");

        return galleryFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}