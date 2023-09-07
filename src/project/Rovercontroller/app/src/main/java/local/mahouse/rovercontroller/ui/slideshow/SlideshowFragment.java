package local.mahouse.rovercontroller.ui.slideshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import local.mahouse.rovercontroller.Singleton;
import pl.droidsonroids.gif.GifImageView;

import local.mahouse.rovercontroller.R;



public class SlideshowFragment extends Fragment {


    /*
     * Documentació (F*cking again per cagar-la)
     * Modes del rover (enviat amb l'array)
     * Index:
     * 0 = mode: 0 = Res | 1 = Control Manual | 2 = Control Automàtic (seguir línea) | 3 = Prova amb text | 4 = Comunicació server - client | 5 = Comunicació Client - Server
     * 1 (1) = direcció/gir: 0 = parar | 1 = Endevant | 2 = Endarrere | 3 = Girar Esquerre | 4 = Girar Dreta
     * 1 (2) = Anar o no: 0 = Deixar de seguir | 1 = Seguir
     * 1 (4) = Comm Ser-Cli: 0 - 4 = Canviar icona direcció a l'aplicació de mòbil | 0xee = Hi ha hagut un error | 0xff = Tancar connecció
     * 1 (5) = Comm Cli-Ser: 0xff = Tancar connecció
     * 2 (1) Velocitat motor esquerre (0 - 0xFF)
     *
     * 3 (1) Velocitat motor dreta (0 - 0xFF)
     *
     */

    int[] data = new int[4];


    @SuppressLint("ClickableViewAccessibility")
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

        //Velocitat motors
        SeekBar speedLeft = (SeekBar) slideshowFragment.findViewById(R.id.speed1);
        SeekBar speedRight = (SeekBar) slideshowFragment.findViewById(R.id.speed2);



        // ---------------------- START OF BUTTON ACTIONS --------------------------
        btnForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Singleton.isConnected()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //Control manual: tirar endevant
                        data[0] = 1;
                        data[1] = 1;
                        //Speed
                        data[2] = speedLeft.getProgress();
                        data[3] = speedRight.getProgress();

                        Singleton.sendIt(data);

                        stop.setVisibility(View.INVISIBLE);
                        direction.setVisibility(View.VISIBLE);
                        direction.setRotation(-90);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {


                        data[0] = 1;
                        data[1] = 0;
                        data[2] = 0;
                        data[3] = 0;

                        Singleton.sendIt(data);

                        direction.setVisibility(View.INVISIBLE);
                        stop.setVisibility(View.VISIBLE);

                    }
                return true;
                } else {
                    Toast.makeText(getContext(), getActivity().getText(R.string.no_connection_text), Toast.LENGTH_LONG).show();
                  return false;
                }
            }
        });

        btnReverse.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Singleton.isConnected()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //Control manual: tirar endarrere
                        data[0] = 1;
                        data[1] = 2;
                        //Speed
                        data[2] = speedLeft.getProgress();
                        data[3] = speedRight.getProgress();

                        Singleton.sendIt(data);


                        stop.setVisibility(View.INVISIBLE);
                        direction.setVisibility(View.VISIBLE);
                        direction.setRotation(90);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {


                        data[0] = 1;
                        data[1] = 0;
                        data[2] = 0;
                        data[3] = 0;

                        Singleton.sendIt(data);

                        direction.setVisibility(View.INVISIBLE);
                        stop.setVisibility(View.VISIBLE);

                    }
                    return true;
                } else {
                Toast.makeText(getContext(), getText(R.string.no_connection_text), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Singleton.isConnected()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //Control manual: Girar Esquerre
                        data[0] = 1;
                        data[1] = 4;
                        //Speed
                        data[2] = speedLeft.getProgress();
                        data[3] = speedRight.getProgress();

                        Singleton.sendIt(data);

                        stop.setVisibility(View.INVISIBLE);
                        direction.setVisibility(View.VISIBLE);
                        direction.setRotation(180);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        data[0] = 1;
                        data[1] = 0;
                        data[2] = 0;
                        data[3] = 0;

                        Singleton.sendIt(data);

                        direction.setVisibility(View.INVISIBLE);
                        stop.setVisibility(View.VISIBLE);


                    }
                    return true;
                } else {
                    Toast.makeText(getContext(), getText(R.string.no_connection_text), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Singleton.isConnected()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //Control manual: Girar Dreta
                        data[0] = 1;
                        data[1] = 3;
                        //Speed
                        data[2] = speedLeft.getProgress();
                        data[3] = speedRight.getProgress();

                        Singleton.sendIt(data);

                        stop.setVisibility(View.INVISIBLE);
                        direction.setVisibility(View.VISIBLE);
                        direction.setRotation(0);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        data[0] = 1;
                        data[1] = 0;
                        data[2] = 0;
                        data[3] = 0;

                        Singleton.sendIt(data);

                        direction.setVisibility(View.INVISIBLE);
                        stop.setVisibility(View.VISIBLE);


                    }
                    return true;
                } else {
                    Toast.makeText(getContext(), getText(R.string.no_connection_text), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        //-------------------- END OF BUTTON ACTIONS ---------------------------

   //NO TOCAR
        return slideshowFragment;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
