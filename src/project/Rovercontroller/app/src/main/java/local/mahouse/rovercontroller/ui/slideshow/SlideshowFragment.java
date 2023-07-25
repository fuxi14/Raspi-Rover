package local.mahouse.rovercontroller.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;

import local.mahouse.rovercontroller.R;


public class SlideshowFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View slideshowFragment = inflater.inflate(R.layout.fragment_slideshow, container, false);//You f*ucking idiot

        final TextView textView = slideshowFragment.findViewById(R.id.text_slideshow);
        textView.setText("This is the slideshow fragment");
        return slideshowFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}