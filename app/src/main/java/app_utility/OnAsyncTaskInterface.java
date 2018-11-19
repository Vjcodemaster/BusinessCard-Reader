package app_utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface OnAsyncTaskInterface {
    void onAsyncTaskComplete(String sCase, int nFlag);
    void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition);
}
