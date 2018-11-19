package com.autochip.businesscardocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import app_utility.CameraSource;
import app_utility.CameraSourcePreview;
import app_utility.GraphicOverlay;
import app_utility.OcrGraphic;
import app_utility.OnFragmentInteractionListener;

import static com.autochip.businesscardocr.MainActivity.onFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int RC_HANDLE_GMS = 9001;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Set good defaults for capturing text.
    boolean autoFocus = true;
    boolean useFlash = false;

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private ImageButton ibCapture;
    TextRecognizer textRecognizer;
    HashMap<String, String> hmCardInfo = new HashMap<>();
    String[] websiteMatches = new String[]{"ww.","vw.","www."};

    boolean hasGotEmailID = false;
    boolean hasGotWebsite = false;
    boolean hasGotName = false;
    //boolean hasGotNumber = false;

    HashSet<String> hsNumbers;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        // Inflate the layout for this fragment
        init(view);

        createCameraSource(autoFocus, useFlash);
        return view;
    }

    void init(View view) {
        preview = view.findViewById(R.id.preview);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);
        ibCapture = view.findViewById(R.id.ib_capture);

        ibCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    private File imageFile;
                    private int rotation = 0;

                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        try {
                            // convert byte array into bitmap
                            Bitmap loadedImage;
                            Bitmap rotatedBitmap;
                            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                    bytes.length);

                            // rotate Image
                            Matrix rotateMatrix = new Matrix();
                            rotateMatrix.postRotate(rotation);
                            rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                    loadedImage.getWidth(), loadedImage.getHeight(),
                                    rotateMatrix, false);
                            String state = Environment.getExternalStorageState();
                            File folder;
                            if (state.contains(Environment.MEDIA_MOUNTED)) {
                                folder = new File(Environment
                                        .getExternalStorageDirectory() + "/Demo");
                            } else {
                                folder = new File(Environment
                                        .getExternalStorageDirectory() + "/Demo");
                            }

                            boolean success = true;
                            if (!folder.exists()) {
                                success = folder.mkdirs();
                            }
                            if (success) {
                                java.util.Date date = new java.util.Date();
                                imageFile = new File(folder.getAbsolutePath()
                                        + File.separator
                                        //+ new Timestamp(date.getTime()).toString()
                                        + "Image.jpg");

                                imageFile.createNewFile();
                            } else {
                                Toast.makeText(getActivity(), "Image Not saved",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // save image into gallery
                            rotatedBitmap = resize(rotatedBitmap, 1024, 768);
                            Frame imageFrame = new Frame.Builder()
                                    .setBitmap(rotatedBitmap)
                                    .build();

                            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);


                            for (int i = 0; i < textBlocks.size(); i++) {

                                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                                String text = textBlock.getValue();
                                extractInfo(text);
                            }

                            ByteArrayOutputStream baOutPutStream = new ByteArrayOutputStream();
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baOutPutStream);

                            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                            fileOutputStream.write(baOutPutStream.toByteArray());
                            fileOutputStream.close();
                            ContentValues values = new ContentValues();

                            values.put(MediaStore.Images.Media.DATE_TAKEN,
                                    System.currentTimeMillis());
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            values.put(MediaStore.MediaColumns.DATA,
                                    imageFile.getAbsolutePath());
                            hmCardInfo.put("image_url", imageFile.getAbsolutePath());

                            getActivity().getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            getActivity().setResult(Activity.RESULT_OK); //add this
                            cameraSource.stop();
                            preview.stop();
                            preview.release();
                            //getActivity().getSupportFragmentManager().popBackStack();
                            //onFragmentInteractionListener.onFragmentMessage("DATA_RECEIVED", rotatedBitmap, hmCardInfo);
                            loadDisplayFragment();
                            //finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void extractInfo(String text) {
        String name;
        //StringBuilder sb = new StringBuilder();
        String email;
        //String firstLine;
        //String secondLine = null;
        //String thirdLine = null;
        //String fourthLine = null;
        //String fifthLine = null;
        String[] lines = text.split(Objects.requireNonNull(System.getProperty("line.separator")));
        String[] matches = new String[]{"Email:", "Email", "email", "E-mail", "e-mail", "-mail"};


        ArrayList<String> alText = new ArrayList<>(Arrays.asList(lines));

        for (int i = 0; i < alText.size(); i++) {
            String sSingleLine = alText.get(i);

            if (sSingleLine != null)
                if (!hasGotWebsite && stringContainsItemFromList(sSingleLine, websiteMatches)){
                    Toast.makeText(getActivity(), "yes website found", Toast.LENGTH_SHORT).show();
                    hmCardInfo.put("website", sSingleLine);
                    hasGotWebsite = true;
                } else
                if (!hasGotEmailID && sSingleLine.contains("@")) {
                    for (String s : matches) {
                        if (sSingleLine.contains(s)) {
                            email = sSingleLine.replace(s, "");
                            sSingleLine = email.replaceAll("[-+^:,]", "").trim();
                            break;
                        }
                    }
                    hmCardInfo.put("email", sSingleLine);
                    hasGotEmailID = true;
                } else {
                    if (!hasGotName && !sSingleLine.contains("@") && !sSingleLine.contains(",") && !sSingleLine.contains(".com")) {
                        name = sSingleLine.replace(".", "");
                        //sb.append(name);
                        hmCardInfo.put("name", name);
                        hasGotName = true;
                    } else {
                        identifyMultipleNumbersWithEmail(sSingleLine);
                    }
                }
        }

        /*firstLine = lines[0];

        if (lines.length > 1)
            secondLine = lines[1];
        if (lines.length > 2)
            thirdLine = lines[2];
        if (lines.length > 3)
            fourthLine = lines[3];
        if (lines.length > 4)
            fifthLine = lines[4];

        if (!hasGotEmailID && firstLine.contains("@")) {
            //String regex_script = "\\Email:\\Email\\email\\E-mail\\e-mail";
            //firstLine = firstLine.replaceAll(regex_script, "").trim();
            //boolean found = false;
            for (String s : matches) {
                if (firstLine.contains(s)) {
                    email = firstLine.replace(s, "");
                    firstLine = email.replaceAll("[-+^:,]", "").trim();
                    break;
                }
            }
            hmCardInfo.put("email", firstLine);
            hasGotEmailID = true;
        } else {
            if (!hasGotName && !firstLine.contains("@") && !firstLine.contains(",") && !firstLine.contains(".com")) {
                name = firstLine.replace(".", "");
                //sb.append(name);
                hmCardInfo.put("name", name);
                hasGotName = true;
            } else {
                identifyMultipleNumbersWithEmail(firstLine);
                *//*String s1 = firstLine.replaceAll("[^0-9]", "");
                if (!hasGotNumber && s1.length() >= 10) {
                    if (s1.length() == 12) {
                        s1 = "+" + s1;
                    } else if (s1.length() > 12) {
                        identifyMultipleNumbersWithEmail(firstLine);
                    }
                    hmCardInfo.put("number", s1);
                    //hasGotNumber = true;
                }*//*
            }
        }
        if (secondLine != null)
            if (!hasGotEmailID && secondLine.contains("@")) {
                for (String s : matches) {
                    if (secondLine.contains(s)) {
                        email = secondLine.replace(s, "");
                        secondLine = email.replaceAll("[-+^:,]", "").trim();
                        break;
                    }
                }
                hmCardInfo.put("email", secondLine);
                hasGotEmailID = true;
            } else {
                if (!hasGotName && !secondLine.contains("@") && !secondLine.contains(",") && !secondLine.contains(".com")) {
                    name = secondLine.replace(".", "");
                    //sb.append(" ").append(name);
                    hmCardInfo.put("name", name);
                    hasGotName = true;
                } else {
                    identifyMultipleNumbersWithEmail(secondLine);
                    *//*String s1 = secondLine.replaceAll("[^0-9]", "");
                    if (!hasGotNumber && s1.length() >= 10) {
                        if (s1.length() == 12) {
                            s1 = "+" + s1;
                        } else if (s1.length() > 12) {
                            identifyMultipleNumbersWithEmail(secondLine);
                        }
                        hmCardInfo.put("number", s1);
                        //hasGotNumber = true;
                    }*//*
                }
            }
        if (thirdLine != null)
            if (!hasGotEmailID && thirdLine.contains("@")) {
                for (String s : matches) {
                    if (thirdLine.contains(s)) {
                        email = thirdLine.replace(s, "");
                        thirdLine = email.replaceAll("[-+^:,]", "").trim();
                        break;
                    }
                }
                hmCardInfo.put("email", thirdLine);
                hasGotEmailID = true;
            } else {
                if (!hasGotName && !thirdLine.contains("@") && !thirdLine.contains(",") && !thirdLine.contains(".com")) {
                    name = thirdLine.replace(".", "");
                    //sb.append(" ").append(name);
                    hmCardInfo.put("name", name);
                    hasGotName = true;
                } else {
                    identifyMultipleNumbersWithEmail(thirdLine);
                    *//*String s1 = thirdLine.replaceAll("[^0-9]", "");
                    if (!hasGotNumber && s1.length() >= 10) {
                        if (s1.length() == 12) {
                            s1 = "+" + s1;
                        } else if (s1.length() > 12) {
                            identifyMultipleNumbersWithEmail(thirdLine);
                        }
                        hmCardInfo.put("number", s1);
                        //hasGotNumber = true;
                    }*//*
                }
            }
        if (fourthLine != null)
            if (!hasGotEmailID && fourthLine.contains("@")) {
                for (String s : matches) {
                    if (fourthLine.contains(s)) {
                        email = fourthLine.replace(s, "");
                        fourthLine = email.replaceAll("[-+^:,]", "").trim();
                        break;
                    }
                }
                hmCardInfo.put("email", fourthLine);
                hasGotEmailID = true;
            } else {
                if (!hasGotName && !fourthLine.contains("@") && !fourthLine.contains(",") && !fourthLine.contains(".com")) {
                    name = fourthLine.replace(".", "");
                    //sb.append(" ").append(name);
                    hmCardInfo.put("name", name);
                    hasGotName = true;
                } else {
                    identifyMultipleNumbersWithEmail(fourthLine);
                }
            }

        if (fifthLine != null)
            if (!hasGotEmailID && fifthLine.contains("@")) {
                for (String s : matches) {
                    if (fifthLine.contains(s)) {
                        email = fifthLine.replace(s, "");
                        fifthLine = email.replaceAll("[-+^:,]", "").trim();
                        break;
                    }
                }
                hmCardInfo.put("email", fifthLine);
                hasGotEmailID = true;
            } else {
                if (!hasGotName && !fifthLine.contains("@") && !fifthLine.contains(",") && !fifthLine.contains(".com")) {
                    name = fifthLine.replace(".", "");
                    //sb.append(" ").append(name);
                    hmCardInfo.put("name", name);
                    hasGotName = true;
                } else {
                    identifyMultipleNumbersWithEmail(fifthLine);
                }
            }*/
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for (String item : items) {
            if (inputStr.contains(item)) {
                return true;
            }
        }
        return false;
    }


    private void identifyMultipleNumbersWithEmail(String str) {
        String sResultByComma[];
        //String sResultBySpace[];
        sResultByComma = str.split(",");
        //sResultBySpace = str.split(" ");
        StringBuilder sb;

        if (sResultByComma.length > 1) {
            hsNumbers = new HashSet<>(Arrays.asList(sResultByComma));
            ArrayList<String> alEmailCheck = new ArrayList<>(hsNumbers);
            for (int i = 0; i < alEmailCheck.size(); i++) {
                String sEmailCheck = alEmailCheck.get(i);
                if (!hasGotWebsite && stringContainsItemFromList(sEmailCheck, websiteMatches)){
                    Toast.makeText(getActivity(), "yes website found", Toast.LENGTH_SHORT).show();
                    hmCardInfo.put("website", sEmailCheck);
                    hasGotWebsite = true;
                } else
                if (sEmailCheck.contains("@")) {
                    String sFinalEmail = sEmailCheck.replaceAll("[-+^:,]", "").trim();
                    hmCardInfo.put("email", sFinalEmail);
                    hasGotEmailID = true;
                }
                String s1 = sEmailCheck.replaceAll("[^0-9]", "").trim();
                /*if (s1.length() >= 10) {
                    if (hmCardInfo.containsKey("number")) {
                        sb.append(hmCardInfo.get("number")).append(",");
                        sb.append(s1);
                        hmCardInfo.put("number", sb.toString());
                    }else if (s1.length() == 12) {
                        s1 = "+" + s1;
                        hmCardInfo.put("number", s1);
                    } else if (s1.length() == 10) {
                        hmCardInfo.put("number", s1);
                    }
                } */

                /*if (s1.length() == 12) {
                    s1 = "+" + s1;
                    if (hmCardInfo.containsKey("number")) {
                        sb = new StringBuilder();
                        sb.append(hmCardInfo.get("number")).append(",");
                        sb.append(s1);
                        hmCardInfo.put("number", sb.toString());
                    } else {
                        hmCardInfo.put("number", s1);
                    }
                } else if (s1.length() >= 10 && s1.length() < 12) {
                    sb = new StringBuilder();
                    if (hmCardInfo.containsKey("number")) {
                        sb.append(hmCardInfo.get("number")).append(",");
                        sb.append(s1);
                        hmCardInfo.put("number", sb.toString());
                    } else {
                        hmCardInfo.put("number", s1);
                    }
                }*/
                if (s1.length()>9 && s1.length() <13) {
                    if(s1.length()== 12) {
                        s1 = "+" + s1;
                    }
                    if (hmCardInfo.containsKey("number")) {
                        sb = new StringBuilder();
                        sb.append(hmCardInfo.get("number")).append(",");
                        sb.append(s1);
                        hmCardInfo.put("number", sb.toString());
                    } else {
                        hmCardInfo.put("number", s1);
                    }
                }
            }
        } else if (sResultByComma.length == 1) {
            String sEmailCheck = sResultByComma[0];
            if (sEmailCheck.contains("@")) {
                String sFinalEmail = sEmailCheck.replaceAll("[-+^:,]", "").trim();
                hmCardInfo.put("email", sFinalEmail);
                hasGotEmailID = true;
            }
            String s1 = sEmailCheck.replaceAll("[^0-9]", "").trim();
            /*if (s1.length() >= 10) {
                if (hmCardInfo.containsKey("number")) {
                    sb.append(hmCardInfo.get("number")).append(",");
                    sb.append(s1);
                    hmCardInfo.put("number", sb.toString());
                } else if (s1.length() == 12) {
                    s1 = "+" + s1;
                    hmCardInfo.put("number", s1);
                } else if (s1.length() == 10) {
                    hmCardInfo.put("number", s1);
                }
            }*/
            /*if (s1.length() == 12) {
                s1 = "+" + s1;
                if (hmCardInfo.containsKey("number")) {
                    sb = new StringBuilder();
                    sb.append(hmCardInfo.get("number")).append(",");
                    sb.append(s1);
                    hmCardInfo.put("number", sb.toString());
                } else {
                    hmCardInfo.put("number", s1);
                }
            } else if (s1.length() >= 10 && s1.length() < 12) {
                if (hmCardInfo.containsKey("number")) {
                    sb = new StringBuilder();
                    sb.append(hmCardInfo.get("number")).append(",");
                    sb.append(s1);
                    hmCardInfo.put("number", sb.toString());
                } else {
                    hmCardInfo.put("number", s1);
                }
            }*/


            if (s1.length()>9 && s1.length() <13) {
                if(s1.length()== 12) {
                    s1 = "+" + s1;
                }
                if (hmCardInfo.containsKey("number")) {
                    sb = new StringBuilder();
                    sb.append(hmCardInfo.get("number")).append(",");
                    sb.append(s1);
                    hmCardInfo.put("number", sb.toString());
                } else {
                    hmCardInfo.put("number", s1);
                }
            } /*else if (s1.length() >= 10 && s1.length() < 12) {
                if (hmCardInfo.containsKey("number")) {
                    sb = new StringBuilder();
                    sb.append(hmCardInfo.get("number")).append(",");
                    sb.append(s1);
                    hmCardInfo.put("number", sb.toString());
                } else {
                    hmCardInfo.put("number", s1);
                }
            }*/
        }

    }

    /*void extractInfo(String text){
        String name;
        StringBuilder sb = new StringBuilder();
        String firstLine;
        String secondLine = null;
        String thirdLine = null;
        String[] lines = text.split(Objects.requireNonNull(System.getProperty("line.separator")));
        firstLine = lines[0];

        if (lines.length > 1)
            secondLine = lines[1];
        if (lines.length > 2)
            thirdLine = lines[2];

        if(firstLine.contains("@")){
            hmCardInfo.put("email", text);
        } else {
            if(!firstLine.contains("@") && !firstLine.contains(".") && !firstLine.contains(",")){
                name = firstLine.replace(".", "");
                sb.append(name);
                hmCardInfo.put("name", sb.toString());
            } else {
                String s1 = firstLine.replaceAll("[^0-9]", "");
                if (s1.length() == 10) {
                    hmCardInfo.put("number", s1);
                }
            }
        }
        if(secondLine!=null)
        if(secondLine.contains("@")){
            hmCardInfo.put("email", text);
        } else {
            if(!secondLine.contains("@") && !secondLine.contains(".") && !secondLine.contains(",")){
                name = secondLine.replace(".", "");
                sb.append(" ").append(name);
                hmCardInfo.put("name", sb.toString());
            } else {
                String s1 = secondLine.replaceAll("[^0-9]", "");
                if (s1.length() == 10) {
                    hmCardInfo.put("number", s1);
                }
            }
        }
        if(thirdLine!=null)
        if(thirdLine.contains("@")){
            hmCardInfo.put("email", text);
        } else {
            if(!thirdLine.contains("@") && !thirdLine.contains(".") && !thirdLine.contains(",")){
                name = thirdLine.replace(".", "");
                sb.append(" ").append(name);
                hmCardInfo.put("name", sb.toString());
            } else {
                String s1 = thirdLine.replaceAll("[^0-9]", "");
                if (s1.length() == 10) {
                    hmCardInfo.put("number", s1);
                }
            }
        }
    }*/

    void loadDisplayFragment() {
        Fragment newFragment;
        FragmentTransaction transaction;
        newFragment = DisplayCardFragment.newInstance("", "");

        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", hmCardInfo);
        newFragment.setArguments(bundle);

        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, newFragment, null);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static String extractNumber(final String str) {

        if (str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
                found = true;
            } else if (found) {
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }

    /*public void extractData(String str) {
        System.out.println("Getting the Name");
        final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            System.out.println(m.group());
            alCardInfo.add(m.group());
            return;
            //displayName.setText(m.group());
        }

        System.out.println("Getting the email");
        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p1 = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m1 = p1.matcher(str);   // get a matcher object
        if (m1.find()) {
            System.out.println(m1.group());
            alCardInfo.add(m1.group());
            return;
            //displayEmail.setText(m.group());
        }

        System.out.println("Getting Phone Number");
        final String PHONE_REGEX = "(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        Pattern p2 = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
        Matcher m2 = p2.matcher(str);   // get a matcher object
        if (m2.find()) {
            System.out.println(m2.group());
            alCardInfo.add(m2.group());
            //displayPhone.setText(m.group());
        }
    }*/

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getActivity();

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        textRecognizer = new TextRecognizer.Builder(context).build();
        //textRecognizer.setProcessor(new OcrDetectorProcessor(graphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            //Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = Objects.requireNonNull(getActivity()).registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getActivity(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                //Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        cameraSource =
                new CameraSource.Builder(getActivity(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();

    }

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                //Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}
