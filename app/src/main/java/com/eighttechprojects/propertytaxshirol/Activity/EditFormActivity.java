package com.eighttechprojects.propertytaxshirol.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityEditFormBinding;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityResurveyFormBinding;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditFormActivity extends AppCompatActivity implements View.OnClickListener, WSResponseInterface {
    //TAG
    private static final String TAG = EditFormActivity.class.getSimpleName();
    //Binding
  private  ActivityEditFormBinding binding;
  // Activity
  private Activity mActivity;
  // Database
  private DataBaseHelper dataBaseHelper;
  // progress Dialog
  private static ProgressDialog progressDialog;
  // Location
  private FusedLocationProviderClient fusedLocationProviderClient;

  private LocationCallback locationCallback;

  private LocationRequest mRequest;

  private Location mCurrentLocation = null;
    ArrayList<FormTableModel> formTableModels = new ArrayList<>();
    // Adapter
    AdapterFormTable adapterFormTable;
    // Form model
    FormFields bin;

    FormModel formModel;
    // Form DB Model
    FormDBModel formDBModel;
  private String polygonID = "";
  public String gisID     = "";

  public String ward_no   = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityEditFormBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

        initForm();
        onFormUpdate();


    }

  private void onFormUpdate() {
    FormFields bin = new FormFields();
    bin.setOwner_name(Utility.getEditTextValue(binding.dbFormOwnerName));
    formModel.setForm(bin);
    formModel.setDetais(adapterFormTable.getFormTableModels());

    if(SystemPermission.isInternetConnected(mActivity)){
      SaveFormToServe(formModel);
    }

  }

  private void SaveFormToServe(FormModel formModel) {
    Map<String, String> params = new HashMap<>();
    params.put("data", Utility.convertFormModelToString(formModel));
    BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_UPDATE_FORM, URL_Utility.WS_UPDATE_FORM, params, false, false);
  }

  private void initExtra(){
    Intent intent = getIntent();

    // Polygon ID Contains or not
    if(intent.getExtras().containsKey(Utility.PASS_POLYGON_ID)) {
      polygonID = intent.getStringExtra(Utility.PASS_POLYGON_ID);
      Log.e(TAG, "Resurvey From Polygon ID: "+polygonID);
    }
    // GIS ID Contains or not
    if(intent.getExtras().containsKey(Utility.PASS_GIS_ID)) {
      gisID = intent.getStringExtra(Utility.PASS_ID);
      Log.e(TAG, "Resurvey From GIS ID: "+gisID);
    }

    // Ward No Contains or not
    if(intent.getExtras().containsKey(Utility.PASS_WARD_NO)) {
      ward_no = intent.getStringExtra(Utility.PASS_WARD_NO);
      Log.e(TAG, "Resurvey From Ward No: "+ward_no);
    }


    // Id
    if(intent.getExtras().containsKey(Utility.PASS_ID)) {
      String id = intent.getStringExtra(Utility.PASS_ID);
      Log.e(TAG, " Form DB ID: " + id);
      // Form DB Model
      formDBModel = dataBaseHelper.getFormByPolygonIDAndID(id);
      // Form Model
      formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
      // Form Fields
      bin = formModel.getForm();
    }

  }



  private void initForm() {
      binding.dbFormOwnerName.setText(Utility.getStringValue(bin.getOwner_name()));
  }

  @Override
    public void onClick(View view) {

    }

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {

    }

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error) {

    }
}