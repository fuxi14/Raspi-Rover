package local.mahouse.rovercontroller.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import pl.droidsonroids.gif.GifImageView;

import local.mahouse.rovercontroller.R;


public class SlideshowFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View slideshowFragment = inflater.inflate(R.layout.fragment_slideshow, container, false);//You f*ucking idiot



        //Gif que indica la direcció i signe d'Stop
        GifImageView direction = (GifImageView) slideshowFragment.findViewById(R.id.gifDirection);
        ImageView stop = (ImageView) slideshowFragment.findViewById(R.id.imgStop);

        //Botons de direcció
        ImageButton btnForward = (ImageButton) slideshowFragment.findViewById(R.id.btnForward);
        ImageButton btnReverse = (ImageButton) slideshowFragment.findViewById(R.id.btnReverse);
        ImageButton btnLeft = (ImageButton) slideshowFragment.findViewById(R.id.btnLeft);
        ImageButton btnRight = (ImageButton) slideshowFragment.findViewById(R.id.btnRight);




        btnForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop.setVisibility(View.INVISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    direction.setRotation(-90);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                }
                return true;
            }
        });

        btnReverse.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop.setVisibility(View.INVISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    direction.setRotation(90);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                }
                return true;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop.setVisibility(View.INVISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    direction.setRotation(180);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                }
                return true;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop.setVisibility(View.INVISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    direction.setRotation(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                }
                return true;
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
