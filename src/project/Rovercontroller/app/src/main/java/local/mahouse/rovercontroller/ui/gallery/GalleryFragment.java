package local.mahouse.rovercontroller.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.widget.TextView;
import android.widget.Toast;
import com.zerokol.views.joystickView.JoystickView;
import com.zerokol.views.joystickView.JoystickView.*;

import local.mahouse.rovercontroller.R;
import local.mahouse.rovercontroller.Singleton;

public class GalleryFragment extends Fragment {

    private TextView mAngle, mPower, mDirection;
    private int[] data = new int[4];
    private JoystickView joystick;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View galleryFragment = inflater.inflate(R.layout.fragment_gallery, container, false);//You f*ucking idiot

        mAngle = (TextView) galleryFragment.findViewById(R.id.txtAngle);
        mPower = (TextView) galleryFragment.findViewById(R.id.txtPower);
        mDirection = (TextView) galleryFragment.findViewById(R.id.txtDirection);
        //JoystickView is a module created by @alvesoaj on Github, all rights reserved to him
        //His github: https://github.com/alvesoaj/JoystickView
        joystick = (JoystickView) galleryFragment.findViewById(R.id.joystickControll);

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                if(Singleton.isConnected()) {
                    data[0] = 1; //We move

                    mAngle.setText(" " + String.valueOf(angle) + "°");

                    mPower.setText(" " + String.valueOf(power) + "%");

                    //Let's Fucking Rock!
                    switch (direction) {
                        case JoystickView.FRONT:
                            mDirection.setText(R.string.front_lab);
                            break;
                        case JoystickView.FRONT_RIGHT:
                            mDirection.setText(R.string.front_right_lab);
                            break;
                        case JoystickView.RIGHT:
                            mDirection.setText(R.string.right_lab);
                            break;
                        case JoystickView.RIGHT_BOTTOM:
                            mDirection.setText(R.string.right_bottom_lab);
                            break;
                        case JoystickView.BOTTOM:
                            mDirection.setText(R.string.bottom_lab);
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            mDirection.setText(R.string.bottom_left_lab);
                            break;
                        case JoystickView.LEFT:
                            mDirection.setText(R.string.left_lab);
                            break;
                        case JoystickView.LEFT_FRONT:
                            mDirection.setText(R.string.left_front_lab);
                            break;
                        default:
                            mDirection.setText(R.string.center_lab);
                            data[1] = 0;
                            data[2] = 0;
                            data[3] = 0;
                    }

                    //Turns out java doesn't fucking have switch with a range of numbers, so if - else it is
                    //Let's use the power of  M A T H

                    //S'HA DE TENIR EN COMPTE QUE EL 0 MIRA CAP AMUNT
                    if(0 < angle && angle < 45) { //Davant-dreta
                        //Forward Right
                        data[1] = 1;
                        data[2] = power;
                        data[3] = 100 * (int) Math.cos(angle * 2);
                    } else if (45 <= angle && angle < 90) { //Dreta
                        data[1] = 4;
                        data[2] = power;
                        data[3] = power * (int) Math.sin(angle * 2);

                    } else if (90 <= angle && angle < 135) { //Dreta
                        data[1] = 4;
                        data[2] = power * (int)  Math.sin(angle * 2);
                        data[3] = power;
                    } else if (135 <= angle && angle <= 180) { //Dreta - avall
                        data[1] = 2;
                        data[2] = power * (int) - Math.cos(angle * 2);
                        data[3] = power;
                    } else if (-180 <= angle && angle < -135) {
                        data[1] = 2;

                        //TODO: Acabar procesament de l'angle i velocitat
                    }

                } else {

                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);


        return galleryFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}