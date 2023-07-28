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
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageView;

import local.mahouse.rovercontroller.R;



public class SlideshowFragment extends Fragment {


    /*
    * Documentació (F*cking again per cagar-la)
    * Modes del rover (enviat amb l'array)
    * Index:
    * 0 = mode: 0x00 = Res | 0x01 = Control Manual | 0x02 = Control Automàtic (seguir línea) | 0x03 = Prova amb text
    * 1 (0x01) = direcció/gir: 0x01 = Endevant | 0x02 = Endarrere | 0x03 = Girar Esquerre | 0x04 = Girar Dreta
    * 1 (0x02) = Anar o no: 0x00 = Deixar de seguir | 0x01 = Seguir
    * 2 (0x01) Velocitat motor esquerre (0x00 - 0xFF)
    * 3 (0x01) Velocitat motor dreta (0x00 - 0xFF)
    *
    * */
    byte[] mMessage = new byte[4];

    public byte[] getmMessage() {
        return mMessage;
    }

    Integer buffer;

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




        btnForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop.setVisibility(View.INVISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    direction.setRotation(-90);

                    //Control manual: tirar endevant
                    mMessage[0] = 0x01;
                    mMessage[1] = 0x01;
                    //Speed
                    buffer = speedLeft.getProgress();
                    mMessage[2] = buffer.byteValue();
                    buffer = speedRight.getProgress();
                    mMessage[3] = buffer.byteValue();


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                    mMessage[0] = 0x00;
                    mMessage[1] = 0x00;
                    mMessage[2] = 0x00;
                    mMessage[3] = 0x00;

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

                    //Control manual: tirar endarrere
                    mMessage[0] = 0x01;
                    mMessage[1] = 0x02;
                    //Speed
                    buffer = speedLeft.getProgress();
                    mMessage[2] = buffer.byteValue();
                    buffer = speedRight.getProgress();
                    mMessage[3] = buffer.byteValue();
                    
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                    mMessage[0] = 0x00;
                    mMessage[1] = 0x00;
                    mMessage[2] = 0x00;
                    mMessage[3] = 0x00;

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

                    //Control manual: Girar Esquerre
                    mMessage[0] = 0x01;
                    mMessage[1] = 0x03;
                    //Speed
                    buffer = speedLeft.getProgress();
                    mMessage[2] = buffer.byteValue();
                    buffer = speedRight.getProgress();
                    mMessage[3] = buffer.byteValue();
                    
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                    
                    mMessage[0] = 0x00;
                    mMessage[1] = 0x00;
                    mMessage[2] = 0x00;
                    mMessage[3] = 0x00;

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

                    //Control manual: Girar Dreta
                    mMessage[0] = 0x01;
                    mMessage[1] = 0x04;
                    //Speed
                    buffer = speedLeft.getProgress();
                    mMessage[2] = buffer.byteValue();
                    buffer = speedRight.getProgress();
                    mMessage[3] = buffer.byteValue();
                    
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);

                    mMessage[0] = 0x00;
                    mMessage[1] = 0x00;
                    mMessage[2] = 0x00;
                    mMessage[3] = 0x00;

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
