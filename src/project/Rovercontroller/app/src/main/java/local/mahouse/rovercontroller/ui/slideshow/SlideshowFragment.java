package local.mahouse.rovercontroller.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import pl.droidsonroids.gif.GifImageView;

import local.mahouse.rovercontroller.R;


public class SlideshowFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View slideshowFragment = inflater.inflate(R.layout.fragment_slideshow, container, false);//You f*ucking idiot



        //Gif que indica la direcció
        GifImageView direction = (GifImageView) slideshowFragment.findViewById(R.id.gifDirection);

        ImageButton btnForward = (ImageButton) slideshowFragment.findViewById(R.id.btnForward);
        ImageButton btnReverse = (ImageButton) slideshowFragment.findViewById(R.id.btnReverse);
        ImageButton btnLeft = (ImageButton) slideshowFragment.findViewById(R.id.btnLeft);
        ImageButton btnRight = (ImageButton) slideshowFragment.findViewById(R.id.btnRight);

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction.setRotation(-90);
            }
        });

        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction.setRotation(90);
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction.setRotation(180);
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direction.setRotation(0);
            }
        });


        //NO TOCAR
        return slideshowFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
