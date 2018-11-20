/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app_utility;

import android.util.Log;
import android.util.SparseArray;

import com.autochip.businesscardocr.CameraFragment;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private ArrayList<String> alCardInfoSeparated = new ArrayList<>();

    private int nCycles = 0;
    private String s1;

    public GraphicOverlay<OcrGraphic> graphicOverlay;
    private HashMap<String, String> hmCardData = new HashMap<>();

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        graphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
                //hmCardData.add(item.getValue());
                OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
                graphicOverlay.add(graphic);
                separateAllInfo(item.getValue());
            }
        }
        nCycles = nCycles + 1;
        if (nCycles >= 2 && items.size() >= 4 && alCardInfoSeparated.size() >= 2) {
            validateInfo();
            CameraFragment.mListener.onFragmentMessage("IMAGE_PROCESS_COMPLETE", null, hmCardData);
        }
    }

    private void separateAllInfo(String sText) {
        String[] lines = sText.split(Objects.requireNonNull(System.getProperty("line.separator")));

        if (lines.length > 1)
            for (String s : lines) {
                if (s.contains(",")) {
                    String[] splitInfo = s.split(",");
                    alCardInfoSeparated.addAll(Arrays.asList(splitInfo));
                } else {
                    alCardInfoSeparated.add(s);
                }
            }
        else
            alCardInfoSeparated.add(sText);
        //alCardInfoSeparated.addAll(Arrays.asList(lines));
    }

    private void validateInfo() {
        StringBuilder sb;
        for (int i = 0; i < alCardInfoSeparated.size(); i++) {
            String sTmp = alCardInfoSeparated.get(i);
            if (isAlpha(sTmp)) {
                if (hmCardData.containsKey("name")) {
                    hmCardData.put("company_name", sTmp);
                } else
                    hmCardData.put("name", sTmp);
            } else if (sTmp.contains("@")) {
                hmCardData.put("email", sTmp);
            } else if (sTmp.contains("w.")) {
                hmCardData.put("website", sTmp);
            } else if (matchesLengthCondition(sTmp)) {
                if (s1.length() == 12) {
                    s1 = "+" + s1;
                }
                if (hmCardData.containsKey("number")) {
                    sb = new StringBuilder();
                    sb.append(hmCardData.get("number")).append(",");
                    sb.append(s1);
                    hmCardData.put("number", sb.toString());
                } else {
                    hmCardData.put("number", s1);
                }
            }
        }
    }

    private boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesLengthCondition(String sNumber) {
        s1 = sNumber.replaceAll("[^0-9]", "").trim();
        return s1.length() > 7 && s1.length() < 13;
    }

    /*public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for (String item : items) {
            if (inputStr.contains(item)) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        graphicOverlay.clear();
    }
}
