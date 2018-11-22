package app_utility;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.autochip.businesscardocr.DisplayCardFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static app_utility.StaticReferenceClass.DB_NAME;
import static app_utility.StaticReferenceClass.NETWORK_ERROR_CODE;
import static app_utility.StaticReferenceClass.PASSWORD;
import static app_utility.StaticReferenceClass.PORT_NO;
import static app_utility.StaticReferenceClass.SERVER_URL;
import static app_utility.StaticReferenceClass.USER_ID;

public class ScannerAsyncTask extends AsyncTask<String, Void, String> {

    private LinkedHashMap<String, ArrayList<String>> lhmProductsWithID = new LinkedHashMap<>();
    private CircularProgressBar circularProgressBar;
    private Activity aActivity;
    private Context context;
    //private OnAsyncTaskInterface onAsyncTaskInterface;
    private ArrayList<Integer> alPosition = new ArrayList<>();
    private StringBuilder sb;
    private HashMap<String, Object> hmDataList = new HashMap<>();
    private HashMap<String, Object> hmCardInfo = new HashMap<>();
    private int nOrderID = 191;

    /*public ScannerAsyncTask(Activity aActivity, OnAsyncTaskInterface onAsyncTaskInterface) {
        this.aActivity = aActivity;
        this.onAsyncTaskInterface = onAsyncTaskInterface;
    }

    public ScannerAsyncTask(Activity aActivity, OnAsyncTaskInterface onAsyncTaskInterface,
                            HashMap<String, Object> hmDataList) {
        this.aActivity = aActivity;
        this.hmDataList = hmDataList;
        this.onAsyncTaskInterface = onAsyncTaskInterface;
    }*/

    public ScannerAsyncTask(Context context, HashMap<String, Object> hmCardInfo) {
        this.context = context;
        this.hmCardInfo = hmCardInfo;
    }

    private Boolean isConnected = false;
    private int ERROR_CODE = 0;
    private String sMsgResult;
    private int type;
    private int[] IDS = new int[2];

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setProgressBar();
    }

    @Override
    protected String doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        switch (type) {
            case 1:
                loginTask();
                break;
            case 2:
                createContacts();
                //updateTask();
                break;
            case 3:
                placeOrder();
                break;
            case 4:
                readProductAndImageTask();
                //snapRoadTask(params[1]);
                //readProducts();
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (ERROR_CODE != 0) {
            switch (ERROR_CODE) {
                case NETWORK_ERROR_CODE:
                    unableToConnectServer(ERROR_CODE);
                    break;
            }
            ERROR_CODE = 0;
            return;
        }
        switch (type) {
            case 2:
                Toast.makeText(context, "Saved Successfully" + "with ID : " + IDS[0] + "," + IDS[1], Toast.LENGTH_LONG).show();
                DisplayCardFragment.mAsyncInterface.onAsyncTaskComplete("CASE_2", 2);
                break;
            case 3:
                //onAsyncTaskInterface.onAsyncTaskComplete("READ_PRODUCTS", type, lhmProductsWithID, alPosition);
                break;
        }
        if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }
    }

    private void loginTask() {
        //if (isConnected) {
        try {
            isConnected = OdooConnect.testConnection(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            if (isConnected) {
                isConnected = true;
                //return true;
            } else {
                isConnected = false;
                sMsgResult = "Connection error";
            }
        } catch (Exception ex) {
            ERROR_CODE = NETWORK_ERROR_CODE;
            // Any other exception
            sMsgResult = "Error: " + ex;
        }
        // }
        //return isConnected;
    }

    private void createContacts() {
        //240
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            Integer createCustomer = oc.create("res.partner", hmCardInfo);
            IDS[0] = createCustomer;
            createOne2Many(createCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void createContacts(){
        //240
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            Integer createCustomer = oc.create("res.partner", new HashMap() {{
                String sName = hmCardInfo.get("name");
                String sEmail = hmCardInfo.get("email");
                String sWebsite = hmCardInfo.get("website");
                put("name", sName);
                put("email", sEmail);
                *//*put("website", sWebsite);

                ArrayList<String> alNumbers = new ArrayList<>(Arrays.asList(hmCardInfo.get("number").split(",")));
                if(alNumbers.size()>1){
                    String phone = alNumbers.get(0);
                    String mobile = alNumbers.get(1);
                        put("phone", phone);
                        put("mobile", mobile);
                } else {
                    String phone = alNumbers.get(0);
                    put("mobile", phone);
                }*//*
                //put("state", ORDER_STATE[0]);
            }});
            IDS[0] = createCustomer;
            createOne2Many(createCustomer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private void createOrder() {
        //240
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            Integer createCustomer = oc.create("sale.order", new HashMap() {{
                put("partner_id", 562);
                //put("state", ORDER_STATE[0]);
            }});
            IDS[0] = createCustomer;
            createOne2Many(createCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createOne2Many(final int ID) {

        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
/*
            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "Autochip");
                put("mobile", "4103246464");
                put("service_id", ID); //one to many
            }});*/

            sb = new StringBuilder();
            sb.append(hmCardInfo.get("name"));
            sb.append(" ");
            sb.append("has been added");
            //if (alOne2ManyModelNames.size() >= 1) {
            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("crm.lead", new HashMap() {{
                put("partner_id", ID);
                put("name", sb.toString());
            }});
            IDS[1] = one2Many;
            //}

        } catch (Exception e) {
            ERROR_CODE = NETWORK_ERROR_CODE;
            e.printStackTrace();
        }
        //return one2Many;
    }

    private void placeOrder() {
        //240
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            Boolean idC = oc.write("sale.order", new Object[]{nOrderID}, hmDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readImageTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        Object[] conditions = new Object[2];
        conditions[0] = new Object[]{"res_model", "=", "product.template"};
        conditions[1] = new Object[]{"res_field", "=", "image_medium"};
        List<HashMap<String, Object>> data = oc.search_read("ir.attachment", new Object[]{conditions}, "id", "store_fname", "res_name");

        for (int i = 0; i < data.size(); ++i) {

        }
    }

    /*private void readProducts() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("product.template", new Object[]{
                new Object[]{new Object[]{"type", "=", "product"}}}, "id", "name", "list_price");

        for (int i = 0; i < data.size(); ++i) {
            //int id = Integer.valueOf(data.get(i).get("id").toString());
            String sName = String.valueOf(data.get(i).get("name").toString());
            //String sUnitPrice = String.valueOf(data.get(i).get("list_price").toString());
            ArrayList<String> alData = new ArrayList<>();
            alData.add(data.get(i).get("id").toString());
            //alData.add(data.get(i).get("name").toString());
            alData.add(data.get(i).get("list_price").toString());
            lhmProductsWithID.put(sName, alData);
        }
    }*/
    private void readProductAndImageTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> productsData = oc.search_read("product.template", new Object[]{
                new Object[]{new Object[]{"type", "=", "product"}}}, "id", "name", "list_price");

        Object[] conditions = new Object[2];
        conditions[0] = new Object[]{"res_model", "=", "product.template"};
        conditions[1] = new Object[]{"res_field", "=", "image_medium"};
        List<HashMap<String, Object>> imageData = oc.search_read("ir.attachment", new Object[]{conditions},
                "id", "store_fname", "res_name");

        for (int i = 0; i < productsData.size(); ++i) {
            //int id = Integer.valueOf(data.get(i).get("id").toString());
            String sName = String.valueOf(productsData.get(i).get("name").toString());
            //String sUnitPrice = String.valueOf(data.get(i).get("list_price").toString());
            ArrayList<String> alData = new ArrayList<>();
            alData.add(productsData.get(i).get("id").toString());
            //alData.add(data.get(i).get("name").toString());
            alData.add(productsData.get(i).get("list_price").toString());
            for (int j = 0; j < imageData.size(); j++) {
                String base64 = imageData.get(j).get("store_fname").toString();
                if (imageData.get(j).get("res_name").toString().equals(sName)) {
                    alData.add(base64);
                    alPosition.add(i);
                    break;
                }
            }
            lhmProductsWithID.put(sName, alData);
        }
    }

    private void unableToConnectServer(int errorCode) {
        //MainActivity.asyncInterface.onAsyncTaskCompleteGeneral("SERVER_ERROR", 2001, errorCode, "", null);
    }

    private void setProgressBar() {
        if (aActivity == null)
            circularProgressBar = new CircularProgressBar(context);
        else
            circularProgressBar = new CircularProgressBar(aActivity);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

}
