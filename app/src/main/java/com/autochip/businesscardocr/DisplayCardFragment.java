package com.autochip.businesscardocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Objects;

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

    EditText[] etNumbers, etEmails, etWebsites;
    LinearLayout llDynamicNumber, llDynamicEmail, llDynamicWebsite;
    ArrayList<String> alNumber = new ArrayList<>();
    ArrayList<String> alEmail = new ArrayList<>();
    ArrayList<String> alWebsite = new ArrayList<>();
    public TextInputLayout etName, etDesignation, etAddress;
    private ImageView ivBusinessCard;
    String sName, sDesignation, sAddress;
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_card, container, false);
        llDynamicNumber = view.findViewById(R.id.ll_dynamic_number);
        llDynamicEmail = view.findViewById(R.id.ll_dynamic_email);
        llDynamicWebsite = view.findViewById(R.id.ll_dynamic_website);

        etName = view.findViewById(R.id.et_name);
        etDesignation = view.findViewById(R.id.et_designation);
        etAddress = view.findViewById(R.id.et_address);
        //etEmail = view.findViewById(R.id.et_email);
        //etWebsite = view.findViewById(R.id.et_website);
        ivBusinessCard = view.findViewById(R.id.iv_business_card);
        btnSave = view.findViewById(R.id.btn_save);

        if (getArguments() != null) {
            mMap = new HashMap<>();
            Bundle b = this.getArguments();
            if (b.getSerializable("hashmap") != null) {
                //noinspection unchecked
                mMap = (HashMap<String, String>) b.getSerializable("hashmap");
                sName = mMap.get("name");
                sDesignation = mMap.get("designation");

                sAddress = mMap.get("address");
                sImagePath = mMap.get("image_url");
                if (mMap.get("number") != null) {
                    HashSet<String> hsTmp = new HashSet<>(Arrays.asList(mMap.get("number").split(",")));
                    alNumber.addAll(hsTmp);
                }
                if (mMap.get("email") != null) {
                    HashSet<String> hsTmp = new HashSet<>(Arrays.asList(mMap.get("email").split(",")));
                    alEmail.addAll(hsTmp);
                }
                if (mMap.get("website") != null) {
                    HashSet<String> hsTmp = new HashSet<>(Arrays.asList(mMap.get("website").split(",")));
                    alWebsite.addAll(hsTmp);
                }
            }
        }

        etName.getEditText().setText(sName);
        etDesignation.getEditText().setText(sDesignation);
        etNumbers = new EditText[alNumber.size()];
        etEmails = new EditText[alEmail.size()];
        etWebsites = new EditText[alWebsite.size()];
        etAddress.getEditText().setText(sAddress);


        etName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sName = etName.getEditText().getText().toString();
                mMap.put("name", sName);
            }
        });

        etDesignation.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sDesignation = etDesignation.getEditText().getText().toString();
                mMap.put("designation", sDesignation);
            }
        });

        etAddress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sAddress = etAddress.getEditText().getText().toString();
                mMap.put("address", sAddress);
            }
        });

        Bitmap bmImg = BitmapFactory.decodeFile(sImagePath);
        //Glide.with(getActivity()).load(bmImg).centerCrop().placeholder(R.drawable.camera_red).into(ivBusinessCard);
        ivBusinessCard.setImageBitmap(bmImg);

        for (int i = 0; i < etNumbers.length; i++) {
            addDynamicContents(i, llDynamicNumber, alNumber, 1);
            final int finalI = i;
            etNumbers[i].addTextChangedListener(new TextWatcher() {
                String sPreviousWebsite;
                String sName;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    sPreviousWebsite = charSequence.toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sName = etNumbers[finalI].getText().toString();
                    String sTmp = Objects.requireNonNull(mMap.get("number")).replace(sPreviousWebsite, sName);
                    mMap.put("number", sTmp);
                }
            });
        }

        for (int i = 0; i < etEmails.length; i++) {
            addDynamicContents(i, llDynamicEmail, alEmail, 2);
            final int finalI = i;
            etEmails[i].addTextChangedListener(new TextWatcher() {
                String sPreviousWebsite;
                String sName;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    sPreviousWebsite = charSequence.toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sName = etEmails[finalI].getText().toString();
                    String sTmp = Objects.requireNonNull(mMap.get("email")).replace(sPreviousWebsite, sName);
                    mMap.put("email", sTmp);
                }
            });
        }

        for (int i = 0; i < etWebsites.length; i++) {
            addDynamicContents(i, llDynamicWebsite, alWebsite, 3);
            final int finalI = i;
            etWebsites[i].addTextChangedListener(new TextWatcher() {
                String sPreviousWebsite;
                String sName;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    sPreviousWebsite = charSequence.toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    sName = etWebsites[finalI].getText().toString();
                    String sTmp = Objects.requireNonNull(mMap.get("website")).replace(sPreviousWebsite, sName);
                    mMap.put("website", sTmp);
                }
            });
        }

        /*
        this code will extract data and assign different key name with respect to odoo requirements
        this converts string values to objects
         */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedHashMap<String, String> lhmData = new LinkedHashMap<>(mMap);
                HashMap<String, Object> hmFinalData = new HashMap<>();
                ArrayList<String> alKeys = new ArrayList<>(lhmData.keySet());

                ArrayList<String> alValues = new ArrayList<>(lhmData.values());
                String sTmp;

                for (int i = 0; i < alValues.size(); i++) {
                    boolean isMultipleContent = false;
                    switch (alKeys.get(i)) {
                        case "number":
                            String sNumber = alValues.get(i);
                            String[] saValues = sNumber.split(",");
                            if (saValues.length > 1) {
                                sTmp = "mobile";
                                Object obj = saValues[1];
                                hmFinalData.put(sTmp, obj);
                                sTmp = "phone";
                                Object obj1 = saValues[0];
                                hmFinalData.put(sTmp, obj1);
                                isMultipleContent = true;
                            } else {
                                sTmp = "phone";
                            }
                            break;
                        case "address":
                            sTmp = "street";
                            break;
                        case "designation":
                            sTmp = "function";
                            break;
                        case "email":
                            sTmp = "email";
                            String sEmail = alValues.get(i);
                            String[] saEmailValues = sEmail.split(",");
                            if (saEmailValues.length > 1) {
                                hmFinalData.put(sTmp, saEmailValues[0]);
                                isMultipleContent = true;
                            }
                            break;
                        default:
                            sTmp = alKeys.get(i);
                            break;
                    }
                    if (!isMultipleContent) {
                        Object obj = alValues.get(i);
                        hmFinalData.put(sTmp, obj);
                    }
                }

                ScannerAsyncTask scannerAsyncTask = new ScannerAsyncTask(getActivity(), hmFinalData);
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

    /*void addDynamicContents(int pos) {
        EditText etDynamicText = new EditText(getActivity());

        if (Build.VERSION.SDK_INT < 23) {
            //noinspection deprecation
            etDynamicText.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Medium);
        } else {
            etDynamicText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }
        etDynamicText.setText(alNumber.get(pos));
        llDynamicNumber.addView(etDynamicText);
    }*/

    void addDynamicContents(int pos, LinearLayout llDynamicParent, ArrayList<String> alDynamicContents, int nCase) {
        EditText etDynamicText = new EditText(getActivity());
        ArrayList<String> alContents;
        LinearLayout llDynamicView;

        alContents = alDynamicContents;
        llDynamicView = llDynamicParent;

        if (Build.VERSION.SDK_INT < 23) {
            //noinspection deprecation
            etDynamicText.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Medium);
        } else {
            etDynamicText.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        }
        etDynamicText.setText(alContents.get(pos));
        llDynamicView.addView(etDynamicText);
        switch (nCase){
            case 1:
                etNumbers[pos] = etDynamicText;
                break;
            case 2:
                etEmails[pos] = etDynamicText;
                break;
            case 3:
                etWebsites[pos] = etDynamicText;
                break;
        }
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag) {
        switch (nFlag) {
            case 2:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition) {

    }
}
