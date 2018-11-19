package com.autochip.businesscardocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import app_utility.OnAsyncTaskInterface;
import app_utility.OnFragmentInteractionListener;
import app_utility.ScannerAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayCardFragment extends Fragment implements OnAsyncTaskInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static OnFragmentInteractionListener mListener;

    public static OnAsyncTaskInterface mAsyncInterface;

    EditText[] etNumbers;
    LinearLayout llDynamicNumber;
    ArrayList<String> alNumber = new ArrayList<>();
    public TextInputLayout etName, etEmail, etWebsite;
    private ImageView ivBusinessCard;
    String sName, sEmail, sWebsite;
    String sImagePath;
    Button btnSave;
    private HashMap<String, String> mMap;

    public DisplayCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayCardFragment newInstance(String param1, String param2) {
        DisplayCardFragment fragment = new DisplayCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsyncInterface = this;
        //mListener = this;
        /*if (getArguments() != null) {
            mMap = new HashMap<>();
            Bundle b = this.getArguments();
            if (b.getSerializable("hashmap") != null) {
                //noinspection unchecked
                mMap = (HashMap<String, String>) b.getSerializable("hashmap");
                if (mMap.get("number") != null) {
                    //String[] saNumbers = mMap.get("number").split(",");
                    sName = mMap.get("name");
                    sEmail = mMap.get("email");
                    HashSet<String> hsTmp = new HashSet<>(Arrays.asList(mMap.get("number").split(",")));
                    alNumber.addAll(hsTmp);
                }
            }
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_card, container, false);
        llDynamicNumber = view.findViewById(R.id.ll_dynamic_number);
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etWebsite = view.findViewById(R.id.et_website);
        ivBusinessCard = view.findViewById(R.id.iv_business_card);
        btnSave = view.findViewById(R.id.btn_save);

        if (getArguments() != null) {
            mMap = new HashMap<>();
            Bundle b = this.getArguments();
            if (b.getSerializable("hashmap") != null) {
                //noinspection unchecked
                mMap = (HashMap<String, String>) b.getSerializable("hashmap");
                if (mMap.get("number") != null) {
                    //String[] saNumbers = mMap.get("number").split(",");
                    sName = mMap.get("name");
                    sEmail = mMap.get("email");
                    sWebsite = mMap.get("website");
                    sImagePath = mMap.get("image_url");
                    HashSet<String> hsTmp = new HashSet<>(Arrays.asList(mMap.get("number").split(",")));
                    alNumber.addAll(hsTmp);
                }
            }
        }

        etName.getEditText().setText(sName);
        etEmail.getEditText().setText(sEmail);
        etWebsite.getEditText().setText(sWebsite);
        etNumbers = new EditText[alNumber.size()];
        Bitmap bmImg = BitmapFactory.decodeFile(sImagePath);
        //Glide.with(getActivity()).load(bmImg).centerCrop().placeholder(R.drawable.camera_red).into(ivBusinessCard);
        ivBusinessCard.setImageBitmap(bmImg);

        for (int i = 0; i < etNumbers.length; i++) {
            addDynamicContents(i);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScannerAsyncTask scannerAsyncTask = new ScannerAsyncTask(getActivity(), mMap);
                scannerAsyncTask.execute(String.valueOf(2), "");
            }
        });
        return view;
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

    void addDynamicContents(int pos) {
        EditText etDynamicText = new EditText(getActivity());

        if (Build.VERSION.SDK_INT < 23) {
            //noinspection deprecation
            etDynamicText.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Medium);
        } else {
            etDynamicText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }
        etDynamicText.setText(alNumber.get(pos));
        llDynamicNumber.addView(etDynamicText);
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag) {
        switch (nFlag){
            case 2:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition) {

    }
}
