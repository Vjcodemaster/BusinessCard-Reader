package app_utility;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFragmentInteractionListener {
    void onFragmentMessage(String TAG, Bitmap bCardImage, HashMap<String, String> hsCardInfo);
}
