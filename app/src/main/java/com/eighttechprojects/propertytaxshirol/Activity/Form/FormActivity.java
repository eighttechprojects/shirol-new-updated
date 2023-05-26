package com.eighttechprojects.propertytaxshirol.Activity.Form;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Adapter.FileUploadViewAdapter;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FileUploadViewModel;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.Model.LastKeyModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.ImageFileUtils;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityFormBinding;
import com.eighttechprojects.propertytaxshirol.volly.AndroidMultiPartEntity;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.mikelau.croperino.BuildConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormActivity extends AppCompatActivity implements View.OnClickListener, WSResponseInterface {

    // TAG
    private static final String TAG = FormActivity.class.getSimpleName();
    // Binding
    private ActivityFormBinding binding;
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
    private final String selectYesOption = "होय";
    private final String selectNoOption = "नाही";

    // Form Spinner Selected
    private String selectedPropertyUserType = "";
    private String selectedPropertyType = "";
    private String selectedBuildPermission = "";
    private String selectedBuildCompletionForm = "";

    private String selectedDrainageSystemAvailable = "";
    private String selectedMetalRoad = "";
    private String selectedIsToiletAvailable = "";
    private String selectedToiletType = "";
    private String selectedIsStreetlightAvailable = "";
    private String selectedIsWaterLineAvailable = "";
    private String selectedWaterUseType = "";
    private String selectedSolarPanelAvailable = "";
    private String selectedSolarPanelType = "";
    private String selectedRaniWaterHarvesting = "";
    private String selectedTotalWaterLine2 = "";

    // Form Table Model List
    ArrayList<FormTableModel> formTableModels = new ArrayList<>();
    // Adapter
    AdapterFormTable adapterFormTable;

    // Form Data
    FormModel formModel;
    private String latitude = "";
    private String longitude = "";

    // Dialog box value
    String db_form_sp_building_type = "";
    String db_form_sp_building_use_type = "";

    // Camera
    private File cameraDestFileTemp;
    ImageFileUtils imageFileUtils;

    StringBuilder sbCameraImagePathLocal = new StringBuilder();
    StringBuilder sbCameraImagePath = new StringBuilder();
    StringBuilder sbCameraImageName = new StringBuilder();

    // File
    StringBuilder sbFilePath = new StringBuilder();
    StringBuilder sbFileName = new StringBuilder();
    StringBuilder sbFilePathLocal = new StringBuilder();

    // File Upload
    public long totalSize = 0;
    private String unique_number = "";
    private String datetime = "";
    private String formID = "";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_CAMERA = "cameraUploader";
    public static boolean isFileUpload = true;
    public static boolean isCameraUpload = true;

    public String polygonID = "";
    public String gisID = "";

    public String ward_no = "";
    public String generatePropertyIDKey = "";

    public boolean isMultipleForm = false;
    FormFields bin;
    public int lastKey;

    private boolean isSurveyComplete = false;

    // Image Edit ResponseCode
    private static final int IMAGE_EDITOR_RESULT_CODE = 101;
    private static final int REQUEST_LOCATION_SETTINGS = 1001;

    private String form_no = "";

    public boolean isEditMode = false;

    ArrayList<String> localImageView = new ArrayList<>();



//------------------------------------------------------- onCreate ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Tool bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Activity
        mActivity = this;
        // FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        // ImageFileUtils
        imageFileUtils = new ImageFileUtils();
        // init Spinner
        initSpinner();
        // Adapter
        adapterFormTable = new AdapterFormTable(mActivity, formTableModels);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        binding.rvFormTableView.addItemDecoration(dividerItemDecoration);
        Utility.setToVerticalRecycleView(mActivity, binding.rvFormTableView, adapterFormTable);
        // init Database
        initDatabase();
        // setOnClickListener
        setOnClickListener();
        // init Extra
        if (getIntent().getExtras() != null) {
            initExtra();
        }


        // Update PreviewUI
        updatePreviewUI(false);


        // Location Call Back
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location loc : locationResult.getLocations()) {
                    mCurrentLocation = loc;
                    if (mCurrentLocation != null) {
                        latitude = String.valueOf(mCurrentLocation.getLatitude());
                        longitude = String.valueOf(mCurrentLocation.getLongitude());
                    }
                }
            }
        };
        LocationPermission();

        // Photo Upload
        binding.imgCaptured.setOnClickListener(view -> {
            cameraPhotoUpload();
        });


    }

//------------------------------------------------------- initExtra ----------------------------------------------------------------------------------------------------------------------

    //Spinner object in Array Adapter

    ArrayAdapter<CharSequence> adapterIsToiletAvailable;
    ArrayAdapter<CharSequence> adapterPropertyUserType;
    ArrayAdapter<CharSequence> adapterPropertyType;
    ArrayAdapter<CharSequence> adapterBuildPermission;
    ArrayAdapter<CharSequence> adapterBuildCompletionForm;
    ArrayAdapter<CharSequence> adapterMetalRoad;
    ArrayAdapter<CharSequence> adapterToiletType;
    ArrayAdapter<CharSequence> adapterIsStreetLightAvailable;
    ArrayAdapter<CharSequence> adapterIsWaterLineAvailable;
    ArrayAdapter<CharSequence> adapterTotalWaterLine;
    ArrayAdapter<CharSequence> adapterWaterUseType;
    ArrayAdapter<CharSequence> adapterSolarPanelAvailable;
    ArrayAdapter<CharSequence> adapterSolarPanelType;
    ArrayAdapter<CharSequence> adapterRaniWaterHarvesting;
    ArrayAdapter<CharSequence> adapterDrainageSystemAvailable;

    ArrayList<FileUploadViewModel> fileUploadList = new ArrayList<>();

    FormDBModel formDBModel = new FormDBModel();
    boolean isSno6Selected = false;


    private void initExtra() {
        Intent intent = getIntent();

        // Polygon ID Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_POLYGON_ID)) {
            polygonID = intent.getStringExtra(Utility.PASS_POLYGON_ID);
            Log.e(TAG, "Polygon-ID: " + polygonID);
        }
        if (intent.getExtras().containsKey(Utility.PASS_IS_EDIT_MODE)) {
            isEditMode = intent.getBooleanExtra(Utility.PASS_IS_EDIT_MODE, false);
            Log.e(TAG, "isEditMode " + isEditMode);
        }


        if (intent.getExtras().containsKey(Utility.PASS_FORM_NO) && isEditMode) {


            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.btSubmit.setVisibility(View.GONE);

            binding.btnSubmit.setOnClickListener(view -> {
                Toast.makeText(mActivity, "Form Updated", Toast.LENGTH_SHORT).show();

                bin.setOwner_name(Utility.getEditTextValue(binding.formOwnerName));
                bin.setOld_property_no(Utility.getEditTextValue(binding.formOldPropertyNo));

                // 3
                bin.setNew_property_no(Utility.getStringValue(binding.formNewPropertyNo.getText().toString()));
                bin.setProperty_name(Utility.getEditTextValue(binding.formPropertyName));
                bin.setProperty_address(Utility.getEditTextValue(binding.formPropertyAddress));
                bin.setProperty_user(Utility.getEditTextValue(binding.formPropertyUser));
                bin.setResurvey_no(Utility.getEditTextValue(binding.formResurveyNo));
                bin.setGat_no(Utility.getEditTextValue(binding.formGatNo));
                bin.setZone(Utility.getEditTextValue(binding.formZone));
                bin.setWard(ward_no);
                bin.setMobile(Utility.getEditTextValue(binding.formMobile));
                bin.setEmail(Utility.getEditTextValue(binding.formEmail));
                bin.setAadhar_no(Utility.getEditTextValue(binding.formAadharNo));

                // 15
                bin.setGis_id(Utility.getStringValue(binding.formGisId.getText().toString()));
                bin.setNo_of_floor(Utility.getEditTextValue(binding.formNoOfFloors));

                bin.setProperty_release_date(Utility.getEditTextValue(binding.formPropertyReleaseDate));
                bin.setTotal_toilet(Utility.getEditTextValue(binding.formTotalToilet));

                bin.setTotal_water_line1(Utility.getStringValue(Utility.getEditTextValue(binding.formTotalWaterLine1)));

                bin.setPlot_area(Utility.getEditTextValue(binding.formPlotArea));
                bin.setProperty_area(Utility.getEditTextValue(binding.formPropertyArea));
                bin.setTotal_area(Utility.getEditTextValue(binding.formTotalArea));
                //spinners

                bin.setProperty_user_type(Utility.getStringValue(selectedPropertyUserType));
                bin.setIs_toilet_available(Utility.getStringValue(selectedIsToiletAvailable));
                bin.setProperty_type(Utility.getStringValue(selectedPropertyType));
                bin.setBuild_permission(Utility.getStringValue(selectedBuildPermission));
                bin.setBuild_completion_form(Utility.getStringValue(selectedBuildCompletionForm));
                bin.setMetal_road(Utility.getStringValue(selectedMetalRoad));
                bin.setToilet_type(Utility.getStringValue(selectedToiletType));
                bin.setIs_water_line_available(Utility.getStringValue(selectedIsWaterLineAvailable));
                bin.setIs_streetlight_available(Utility.getStringValue(selectedIsStreetlightAvailable));
                bin.setTotal_water_line2(Utility.getStringValue(selectedTotalWaterLine2));
                bin.setWater_use_type(Utility.getStringValue(selectedWaterUseType));
                bin.setSolar_panel_type(Utility.getStringValue(selectedSolarPanelType));
                bin.setSolar_panel_available(Utility.getStringValue(selectedSolarPanelAvailable));
                bin.setRain_water_harvesting(Utility.getStringValue(selectedRaniWaterHarvesting));
                bin.setIs_drainage_available(Utility.getStringValue(selectedDrainageSystemAvailable));

                bin.setProperty_images("");
                bin.setPlan_attachment("");
                formModel.setForm(bin);

                if (SystemPermission.isInternetConnected(mActivity)) {
                    String data = Utility.convertFormModelToString(formModel);
                    Log.e(TAG, data);
                    updateFormToDatabase(data);
                    updateFormToServe(data);

                } else {
                    String data = Utility.convertFormModelToString(formModel);
                    Log.e(TAG, data);
                    updateFormToDatabase(data);
                }
                onFormExit();

            });

            form_no = intent.getStringExtra(Utility.PASS_FORM_NO);

            Log.e(TAG, "Form_No " + form_no);
            formDBModel = dataBaseHelper.getFormByPolygonIDAndID(form_no);
            formModel = new FormModel();
            // Form Model
            formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
            // Form Fields
            bin = formModel.getForm();
            binding.formOwnerName.setText(bin.getOwner_name());
            binding.formOldPropertyNo.setText(bin.getOld_property_no());
            binding.formNewPropertyNo.setText(bin.getNew_property_no());
            binding.formPropertyName.setText(bin.getProperty_name());
            binding.formPropertyAddress.setText(bin.getProperty_address());
            binding.formPropertyUser.setText(bin.getProperty_user());
            binding.formResurveyNo.setText(bin.getResurvey_no());
            binding.formGatNo.setText(bin.getGat_no());
            binding.formZone.setText(bin.getZone());
            binding.formMobile.setText(bin.getMobile());
            binding.formEmail.setText(bin.getEmail());
            binding.formAadharNo.setText(bin.getAadhar_no());
            binding.formGisId.setText(bin.getGis_id());
            binding.formTotalToilet.setText(bin.getTotal_toilet());
            binding.formTotalWaterLine1.setText(bin.getTotal_water_line1());
            binding.formPlotArea.setText(bin.getPlot_area());
            binding.formPropertyArea.setText(bin.getProperty_area());
            binding.formTotalArea.setText(bin.getTotal_area());


            //getting add table in recycler view
            if (formModel.getDetais().size() > 0) {
                formTableModels = formModel.getDetais();
                adapterFormTable = new AdapterFormTable(mActivity, formTableModels, false, isSno6Selected);
                Utility.setToVerticalRecycleView(mActivity, binding.rvFormTableView, adapterFormTable);

            }
            //getting camera ----------------------------------------------------------------------------------------- file upload

            // Camera Image Upload View -----------------------
           binding.llImageCapturedView.setVisibility(View.VISIBLE);
            Log.e(TAG, "Camera Path " + formDBModel.getCameraPath());
            if (!Utility.isEmptyString(formDBModel.getCameraPath())) {
                sbCameraImagePathLocal.append(formDBModel.getCameraPath());
                String[] propertyImages = formDBModel.getCameraPath().split(",");
                Log.e(TAG, "Path" + Arrays.toString(formDBModel.getCameraPath().split(",")));
                for (int i = 0; i < propertyImages.length; i++) {
                    Log.e(TAG, "propertyImages" + propertyImages[i]);
                    if (propertyImages[i].split("#").length > 1) {
                        String imagePath = propertyImages[i].split("#")[1];
                        ImageView imageView = new ImageView(mActivity);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 300);
                        imageView.setLayoutParams(layoutParams);

                        if (propertyImages[i].split("#")[0].startsWith("local")) {
                            Glide.with(mActivity).load(imagePath).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageView);
                        } else {
                            Uri uri = Uri.parse(imagePath);
                            Log.e(TAG, "imagePath" + imagePath);
                            Glide.with(mActivity).load(uri).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageView);
                        }
                        String selImage = propertyImages[i];
                        binding.llImageCapturedView.addView(imageView);
                        imageView.setOnClickListener(view -> {
                            Dialog dialog = new Dialog(mActivity);
                            dialog.setContentView(R.layout.image_zoom_view_layout);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ImageView imageViewDialog = dialog.findViewById(R.id.dialogbox_image);
                            Log.e(TAG, "SelImagePath" + selImage);

                            if (selImage.split("#")[0].startsWith("local")) {
                                Log.e(TAG, "imagePathLocal" + imagePath);
                                Glide.with(mActivity).load(selImage.split("#")[1]).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageViewDialog);
                            } else {
                                Uri uri = Uri.parse(imagePath);
                                Log.e(TAG, "imagePathUri" + imagePath);
                                Glide.with(mActivity).load(uri).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageViewDialog);
                            }
                            dialog.show();

                        });
                    }
                }
            }


            // File Upload View -------------------------

            TextView tv_fileUploadName = findViewById(R.id.tv_fileUploadName);
            if (!Utility.isEmptyString(formDBModel.getFilePath())) {
                sbFilePathLocal.append(formDBModel.getFilePath());
                tv_fileUploadName.setText("File Found");
                int n = formDBModel.getFilePath().split(",").length;
                for (int i = 0; i < n; i++) {
                    if (formDBModel.getFilePath().split(",")[i].split("#")[0].startsWith("local%")) {
                        String filePath = formDBModel.getFilePath().split(",")[i].split("#")[1];
                        File file = new File(filePath);
                        String fileName = file.getName();
                        fileUploadList.add(new FileUploadViewModel(fileName, filePath, false));
                    } else {

                        if (formDBModel.getFilePath().split(",")[i].length() > 1) {
                            String filename = formDBModel.getFilePath().split(",")[i].split("#")[0];
                            String filepath = formDBModel.getFilePath().split(",")[i].split("#")[1];
                            fileUploadList.add(new FileUploadViewModel(filename, filepath, true));
                        }

                    }
                }
            } else {
                tv_fileUploadName.setText("No File Upload");
            }
            //to view the image file stored in File Upload
            Button db_btFileUpload = findViewById(R.id.db_btFileUpload);
            db_btFileUpload.setVisibility(View.VISIBLE);
            db_btFileUpload.setOnClickListener(view -> {
                if (Utility.isEmptyString(formDBModel.getFilePath())) {
                    Toast.makeText(mActivity, "No File Found", Toast.LENGTH_SHORT).show();
                } else {
                    ViewFileUploadDialogBox(fileUploadList);
                }

            });

            //Spinner 22
            List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines.size(); i++) {
                if (bin.getIs_toilet_available().equals(Lines.get(i))) {
                    binding.formSpIsToiletAvailable.setSelection(i);
                    selectedIsToiletAvailable = bin.getIs_toilet_available();
                    if (selectedIsToiletAvailable.equals(selectYesOption)) {
                        binding.ll23.setVisibility(View.VISIBLE);
                        binding.ll24.setVisibility(View.VISIBLE);
                    } else {
                        binding.ll23.setVisibility(View.GONE);
                        binding.ll24.setVisibility(View.GONE);
                        binding.formTotalToilet.setText("");
                        selectedToiletType = "";
                    }
                    break;
                }
            }
            //Spinner 6
            if (bin.getProperty_user_type() != null) {
                List<String> Lines2 = Arrays.asList(getResources().getStringArray(R.array.sp_property_user_type));
                for (int i = 0; i < Lines2.size(); i++) {
                    if (bin.getProperty_user_type().equals(Lines2.get(i))) {
                        binding.formSpPropertyUserType.setSelection(i);
                        selectedPropertyUserType = bin.getProperty_user_type();
                        if (selectedPropertyUserType.equalsIgnoreCase("भोगवटादार")) {
                            binding.ll7.setVisibility(View.VISIBLE);
                        }
                        // Table 6 visible else not
                        else if (selectedPropertyUserType.equalsIgnoreCase("भाडेकरू")) {
                            binding.ll7.setVisibility(View.GONE);
                            binding.formPropertyUser.setText("");
                        } else {
                            binding.ll7.setVisibility(View.GONE);
                            binding.formPropertyUser.setText("");
                        }
                        break;

                    }
                }
            }
            //Spinner 17
            if (bin.getProperty_type() != null) {
                List<String> Lines3 = Arrays.asList(getResources().getStringArray(R.array.sp_property_type));
                for (int i = 0; i < Lines3.size(); i++) {
                    if (bin.getProperty_type().equals(Lines3.get(i))) {
                        binding.formSpPropertyType.setSelection(i);
                        selectedPropertyType = bin.getProperty_type();
                        if (selectedPropertyType.equalsIgnoreCase("इतर")) {
                            binding.ll171.setVisibility(View.VISIBLE);
                        } else {
                            binding.ll171.setVisibility(View.GONE);
                        }
                        break;
                    }
                }

            }
            //Spinner 19
            List<String> Lines4 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines4.size(); i++) {
                if (bin.getBuild_permission().equals(Lines4.get(i))) {
                    binding.formSpBuildPermission.setSelection(i);
                    selectedBuildPermission = bin.getBuild_permission();
                    if (selectedBuildPermission.equals(selectYesOption)) {
                        binding.ll20.setVisibility(View.VISIBLE);
                    } else {
                        binding.ll20.setVisibility(View.GONE);
                        selectedBuildCompletionForm = "";
                    }
                    break;
                }
            }
//Spinner 20
            List<String> Lines5 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines5.size(); i++) {
                if (bin.getBuild_completion_form().equals(Lines5.get(i))) {
                    binding.formSpBuildCompletionForm.setSelection(i);
                    selectedBuildCompletionForm = bin.getBuild_completion_form();

                    break;
                }
            }
//Spinner 21
            if (bin.getMetal_road() != null) {
                List<String> Lines6 = Arrays.asList(getResources().getStringArray(R.array.sp_metal_road));
                for (int i = 0; i < Lines6.size(); i++) {
                    if (bin.getMetal_road().equals(Lines6.get(i))) {
                        binding.formSpMetalRoad.setSelection(i);
                        selectedMetalRoad = bin.getMetal_road();

                        break;
                    }
                }

            }
//Spinner 24
            if (bin.getToilet_type() != null) {
                List<String> Lines7 = Arrays.asList(getResources().getStringArray(R.array.sp_toilet_type));
                for (int i = 0; i < Lines7.size(); i++) {
                    if (bin.getToilet_type().equals(Lines7.get(i))) {
                        binding.formSpToiletType.setSelection(i);
                        selectedToiletType = bin.getToilet_type();
                        break;
                    }
                }

            }
//Spinner 25
            List<String> Lines8 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines8.size(); i++) {
                if (bin.getIs_streetlight_available().equals(Lines8.get(i))) {
                    binding.formSpIsStreetlightAvailable.setSelection(i);
                    selectedIsStreetlightAvailable = bin.getIs_streetlight_available();
                    break;
                }
            }
//Spinner 26
            List<String> Lines9 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines9.size(); i++) {
                if (bin.getIs_water_line_available().equals(Lines9.get(i))) {
                    binding.formSpIsWaterLineAvailable.setSelection(i);
                    selectedIsWaterLineAvailable = bin.getIs_water_line_available();
                    if (selectedIsWaterLineAvailable.equals(selectYesOption)) {
                        binding.ll27.setVisibility(View.VISIBLE);
                        binding.ll271.setVisibility(View.VISIBLE);
                        binding.ll272.setVisibility(View.VISIBLE);
                        binding.ll28.setVisibility(View.VISIBLE);
                    } else {
                        binding.ll27.setVisibility(View.GONE);
                        binding.ll271.setVisibility(View.GONE);
                        binding.ll272.setVisibility(View.GONE);
                        binding.ll28.setVisibility(View.GONE);
                        selectedWaterUseType = "";
                    }
                    break;
                }
            }
//Spinner 27
            if (bin.getTotal_water_line2() != null) {
                List<String> Lines10 = Arrays.asList(getResources().getStringArray(R.array.sp_total_water_line));
                for (int i = 0; i < Lines10.size(); i++) {
                    if (bin.getTotal_water_line2().equals(Lines10.get(i))) {
                        binding.formTotalWaterLine2.setSelection(i);
                        selectedTotalWaterLine2 = bin.getTotal_water_line2();
                        break;
                    }
                }

            }
//Spinner 28
            if (bin.getWater_use_type() != null) {
                List<String> Lines11 = Arrays.asList(getResources().getStringArray(R.array.sp_water_use_type));
                for (int i = 0; i < Lines11.size(); i++) {
                    if (bin.getWater_use_type().equals(Lines11.get(i))) {
                        binding.formSpWaterUseType.setSelection(i);
                        selectedWaterUseType = bin.getWater_use_type();
                        break;
                    }
                }

            }
//Spinner 29
            List<String> Lines12 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines12.size(); i++) {
                if (bin.getSolar_panel_available().equals(Lines12.get(i))) {
                    binding.formSpSolarPanelAvailable.setSelection(i);
                    selectedSolarPanelAvailable = bin.getSolar_panel_available();
                    if (selectedSolarPanelAvailable.equals(selectYesOption)) {
                        binding.ll30.setVisibility(View.VISIBLE);
                    } else {
                        binding.ll30.setVisibility(View.GONE);
                        selectedSolarPanelType = "";
                    }
                    break;
                }
            }
//Spinner 30
            if (bin.getSolar_panel_type() != null) {
                List<String> Lines13 = Arrays.asList(getResources().getStringArray(R.array.sp_solar_panel_type));
                for (int i = 0; i < Lines13.size(); i++) {
                    if (bin.getSolar_panel_type().equals(Lines13.get(i))) {
                        binding.formSpSolarPanelType.setSelection(i);
                        selectedSolarPanelType = bin.getSolar_panel_type();
                        break;
                    }
                }

            }
//Spinner 31
            List<String> Lines14 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
            for (int i = 0; i < Lines14.size(); i++) {
                if (bin.getRain_water_harvesting().equals(Lines14.get(i))) {
                    binding.formSpRainWaterHarvesting.setSelection(i);
                    selectedRaniWaterHarvesting = bin.getRain_water_harvesting();
                    break;
                }
            }
            //Spinner 32
            if (bin.getIs_drainage_available() != null) {
                List<String> Lines15 = Arrays.asList(getResources().getStringArray(R.array.sp_yes_no));
                for (int i = 0; i < Lines15.size(); i++) {
                    if (bin.getIs_drainage_available().equals(Lines15.get(i))) {
                        binding.formSpDrainageSystemAvailable.setSelection(i);
                        selectedDrainageSystemAvailable = bin.getIs_drainage_available();
                        break;
                    }
                }
            }


        }


        // GIS-ID Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_GIS_ID)) {
            gisID = intent.getStringExtra(Utility.PASS_GIS_ID);
            Log.e(TAG, "GIS-ID: " + gisID);
            binding.formGisId.setText(Utility.getStringValue(gisID));
        }

        // Ward No Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_WARD_NO)) {
            ward_no = intent.getStringExtra(Utility.PASS_WARD_NO);
            Log.e(TAG, "Ward No: " + ward_no);
            binding.formWard.setText(Utility.getStringValue(ward_no));
        }

        // Is Multiple Form Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_IS_MULTIPLE)) {
            isMultipleForm = intent.getBooleanExtra(Utility.PASS_IS_MULTIPLE, false);
            Log.e(TAG, "Is Multiple Form: " + isMultipleForm);
        }

        // Last Key Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_LAST_KEY)) {
            lastKey = intent.getIntExtra(Utility.PASS_LAST_KEY, 0);
            Log.e(TAG, "Form last key: " + lastKey);
        }

        // Lat Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_LAT)) {
            latitude = intent.getStringExtra(Utility.PASS_LAT);
            Log.e(TAG, "Form current Lat: " + latitude);
        }

        // Lon Contains or not
        if (intent.getExtras().containsKey(Utility.PASS_LONG)) {
            longitude = intent.getStringExtra(Utility.PASS_LONG);
            Log.e(TAG, "Form current Long: " + longitude);
        }

        if (intent.getExtras().containsKey(Utility.PASS_OWNER_NAME)) {
            String ownerName = intent.getStringExtra(Utility.PASS_OWNER_NAME);
            Log.e(TAG, "Owner Name:" + ownerName);
            binding.formOwnerName.setText(Utility.getStringValue(ownerName));
        }


        // Generate Property ID
        // Single Form then
        if (!isMultipleForm) {
            binding.formNewPropertyNo.setText(Utility.getStringValue(generatePropertyID(polygonID, isMultipleForm, lastKey).toString().split("/")[0]));
        }
        // Multiple Form then
        else {
            binding.formNewPropertyNo.setText(Utility.getStringValue(generatePropertyID(polygonID, isMultipleForm, lastKey)));

        }


    }

    private void ViewFileUploadDialogBox(ArrayList<FileUploadViewModel> fileUploadViewModelArrayList) {
        // DialogBox
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_fileuploadview_layout_dialogbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // TextView
        TextView tvUploadName = dialog.findViewById(R.id.tvUploadName);
        tvUploadName.setText("File Upload View");
        // RecycleView
        RecyclerView recyclerView = dialog.findViewById(R.id.file_upload_view_recycle_view);
        // Cancel Button
        Button cancel_bt = dialog.findViewById(R.id.file_upload_view_cancel_bt);
        cancel_bt.setOnClickListener(view1 -> dialog.dismiss());
        // Adapter
        FileUploadViewAdapter fileUploadViewAdapter = new FileUploadViewAdapter(mActivity, fileUploadViewModelArrayList);
        // Set Adapter
        recyclerView.setAdapter(fileUploadViewAdapter);
        // Set Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        fileUploadViewAdapter.notifyDataSetChanged();
        // Dialog box Show
        dialog.show();
    }

    private void updateFormToDatabase(String formModelStr) {
        dataBaseHelper.updateGeoJsonPolygonForm(form_no, formModelStr, "t", sbFilePathLocal.toString(), sbCameraImagePathLocal.toString());
        Log.e(TAG, "Form Save to Database");
    }

    private void updateFormToServe(String formModel) {
        Map<String, String> params = new HashMap<>();
        params.put("data", formModel);
        Log.e(TAG, "Form -> " + formModel);
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_UPDATE_FORM, URL_Utility.WS_UPDATE_FORM, params, false, false);
    }


    private String generatePropertyID(String polygonID, boolean isMultipleForm, int lastKey) {

        String key = "";
        // Single Form then
        if (!isMultipleForm) {
            key = polygonID + "/" + 1;
        }
        // Multiple Form then
        else {
            key = polygonID + "/" + (lastKey + 1);
        }
        return key;
    }

//------------------------------------------------------- initSpinner ----------------------------------------------------------------------------------------------------------------------

    private void initSpinner() {

        // 6 - Spinner -----------------------------------------------------------------------------
        adapterPropertyUserType = ArrayAdapter.createFromResource(mActivity, R.array.sp_property_user_type, android.R.layout.simple_spinner_item);
        adapterPropertyUserType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpPropertyUserType.setAdapter(adapterPropertyUserType);


        binding.formSpPropertyUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedPropertyUserType = parent.getItemAtPosition(position).toString();

                // 7 visible
                if (selectedPropertyUserType.equalsIgnoreCase("भोगवटादार")) {
                    binding.ll7.setVisibility(View.VISIBLE);
                }
                // Table 6 visible else not
                else if (selectedPropertyUserType.equalsIgnoreCase("भाडेकरू")) {
                    binding.ll7.setVisibility(View.GONE);
                    binding.formPropertyUser.setText("");
                } else {
                    binding.ll7.setVisibility(View.GONE);
                    binding.formPropertyUser.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 17 - Spinner -----------------------------------------------------------------------------
        adapterPropertyType = ArrayAdapter.createFromResource(mActivity, R.array.sp_property_type, android.R.layout.simple_spinner_item);
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpPropertyType.setAdapter(adapterPropertyType);
        binding.formSpPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedPropertyType = parent.getItemAtPosition(position).toString();
                if (selectedPropertyType.equalsIgnoreCase("इतर")) {
                    binding.ll171.setVisibility(View.VISIBLE);
                } else {
                    binding.ll171.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 19 - Spinner -----------------------------------------------------------------------------
        adapterBuildPermission = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterBuildPermission.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpBuildPermission.setAdapter(adapterBuildPermission);
        binding.formSpBuildPermission.setSelection(1);
        binding.formSpBuildPermission.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedBuildPermission = parent.getItemAtPosition(position).toString();
                // select Yes Option
                if (selectedBuildPermission.equals(selectYesOption)) {
                    binding.ll20.setVisibility(View.VISIBLE);
                } else {
                    binding.ll20.setVisibility(View.GONE);
                    selectedBuildCompletionForm = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 20 - Spinner -----------------------------------------------------------------------------
        adapterBuildCompletionForm = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterBuildCompletionForm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpBuildCompletionForm.setAdapter(adapterBuildCompletionForm);
        binding.formSpBuildCompletionForm.setSelection(1);
        binding.formSpBuildCompletionForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedBuildCompletionForm = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 21 - Spinner -----------------------------------------------------------------------------
        adapterMetalRoad = ArrayAdapter.createFromResource(mActivity, R.array.sp_metal_road, android.R.layout.simple_spinner_item);
        adapterMetalRoad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpMetalRoad.setAdapter(adapterMetalRoad);
        binding.formSpMetalRoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedMetalRoad = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 22 - Spinner -----------------------------------------------------------------------------
        adapterIsToiletAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterIsToiletAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsToiletAvailable.setAdapter(adapterIsToiletAvailable);
        binding.formSpIsToiletAvailable.setSelection(1);
        binding.formSpIsToiletAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsToiletAvailable = parent.getItemAtPosition(position).toString();
                if (selectedIsToiletAvailable.equals(selectYesOption)) {
                    binding.ll23.setVisibility(View.VISIBLE);
                    binding.ll24.setVisibility(View.VISIBLE);
                } else {
                    binding.ll23.setVisibility(View.GONE);
                    binding.ll24.setVisibility(View.GONE);
                    binding.formTotalToilet.setText("");
                    selectedToiletType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 24 - Spinner -----------------------------------------------------------------------------
        adapterToiletType = ArrayAdapter.createFromResource(mActivity, R.array.sp_toilet_type, android.R.layout.simple_spinner_item);
        adapterToiletType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpToiletType.setAdapter(adapterToiletType);
        binding.formSpToiletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedToiletType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 25 - Spinner -----------------------------------------------------------------------------
        adapterIsStreetLightAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterIsStreetLightAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsStreetlightAvailable.setAdapter(adapterIsStreetLightAvailable);
        binding.formSpIsStreetlightAvailable.setSelection(1);
        binding.formSpIsStreetlightAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsStreetlightAvailable = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 26 - Spinner -----------------------------------------------------------------------------
        adapterIsWaterLineAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterIsWaterLineAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsWaterLineAvailable.setAdapter(adapterIsWaterLineAvailable);
        binding.formSpIsWaterLineAvailable.setSelection(1);
        binding.formSpIsWaterLineAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsWaterLineAvailable = parent.getItemAtPosition(position).toString();

                if (selectedIsWaterLineAvailable.equals(selectYesOption)) {
                    binding.ll27.setVisibility(View.VISIBLE);
                    binding.ll271.setVisibility(View.VISIBLE);
                    binding.ll272.setVisibility(View.VISIBLE);
                    binding.ll28.setVisibility(View.VISIBLE);
                } else {
                    binding.ll27.setVisibility(View.GONE);
                    binding.ll271.setVisibility(View.GONE);
                    binding.ll272.setVisibility(View.GONE);
                    binding.ll28.setVisibility(View.GONE);
                    selectedWaterUseType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 27 - Spinner -----------------------------------------------------------------------------
        adapterTotalWaterLine = ArrayAdapter.createFromResource(mActivity, R.array.sp_total_water_line, android.R.layout.simple_spinner_item);
        adapterTotalWaterLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formTotalWaterLine2.setAdapter(adapterTotalWaterLine);
        binding.formTotalWaterLine2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedTotalWaterLine2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 28 - Spinner -----------------------------------------------------------------------------
        adapterWaterUseType = ArrayAdapter.createFromResource(mActivity, R.array.sp_water_use_type, android.R.layout.simple_spinner_item);
        adapterWaterUseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpWaterUseType.setAdapter(adapterWaterUseType);
        binding.formSpWaterUseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedWaterUseType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 29 - Spinner -----------------------------------------------------------------------------
        adapterSolarPanelAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterSolarPanelAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpSolarPanelAvailable.setAdapter(adapterSolarPanelAvailable);
        binding.formSpSolarPanelAvailable.setSelection(1);
        binding.formSpSolarPanelAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedSolarPanelAvailable = parent.getItemAtPosition(position).toString();
                if (selectedSolarPanelAvailable.equals(selectYesOption)) {
                    binding.ll30.setVisibility(View.VISIBLE);
                } else {
                    binding.ll30.setVisibility(View.GONE);
                    selectedSolarPanelType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 30 - Spinner -----------------------------------------------------------------------------
        adapterSolarPanelType = ArrayAdapter.createFromResource(mActivity, R.array.sp_solar_panel_type, android.R.layout.simple_spinner_item);
        adapterSolarPanelType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpSolarPanelType.setAdapter(adapterSolarPanelType);
        binding.formSpSolarPanelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedSolarPanelType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 31 - Spinner -----------------------------------------------------------------------------
        adapterRaniWaterHarvesting = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterRaniWaterHarvesting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpRainWaterHarvesting.setAdapter(adapterRaniWaterHarvesting);
        binding.formSpRainWaterHarvesting.setSelection(1);
        binding.formSpRainWaterHarvesting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedRaniWaterHarvesting = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // 32 - Spinner -----------------------------------------------------------------------------
        adapterDrainageSystemAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no, android.R.layout.simple_spinner_item);
        adapterDrainageSystemAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpDrainageSystemAvailable.setAdapter(adapterDrainageSystemAvailable);
        binding.formSpDrainageSystemAvailable.setSelection(1);
        binding.formSpDrainageSystemAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedDrainageSystemAvailable = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

//------------------------------------------------------- initDatabase ----------------------------------------------------------------------------------------------------------------------

    private void initDatabase() {
        dataBaseHelper = new DataBaseHelper(mActivity);
    }

//------------------------------------------------------- setOnClickListener ----------------------------------------------------------------------------------------------------------------------

    private void setOnClickListener() {
        binding.btSubmit.setOnClickListener(this);
        binding.btExit.setOnClickListener(this);
        binding.btAddFormTable.setOnClickListener(this);
        binding.btFileUpload.setOnClickListener(this);
    }

//---------------------------------------------- Location Permission ------------------------------------------------------------------------------------------------------------------------

    private void LocationPermission() {
        if (SystemPermission.isLocation(mActivity)) {
            location();
        }
    }

    @SuppressLint("MissingPermission")
    private void location() {
        //now for receiving constant location updates:
        mRequest = LocationRequest.create();
        mRequest.setInterval(2000);//time in ms; every ~2 seconds
        mRequest.setFastestInterval(1000);
        mRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, 500);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(mRequest, locationCallback, null);
    }

    protected void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

//------------------------------------------------------- Menu ----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return false;
    }

//------------------------------------------------------- onClickView ----------------------------------------------------------------------------------------------------------------------

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btSubmit:
                // Context context,String title,String str, DialogBoxYesClick dialogBoxYesClick, DialogBoxNoClick dialogBoxNoCl
                // Form
                formModel = new FormModel();
                // 1 -----------------------------
                bin = new FormFields();
                // Default Fields

                // Single Form
                if(!isMultipleForm){

                    Utility.showYesNoDialogBox(mActivity, "Info", "Survey Complete or Not?", yesDialogBox -> {
                        yesDialogBox.dismiss();
                        bin.setForm_status(Utility.SurveyCompleted);
                        bin.setPolygon_status(Utility.PolygonStatusCompleted);
                        isSurveyComplete = true;
                        onFormSubmit();
                    }, noDialogBox -> {
                        noDialogBox.dismiss();
                        isSurveyComplete = false;
                        bin.setForm_status(Utility.SurveyNotComplete);
                        bin.setPolygon_status(Utility.PolygonStatusNotComplete);
                        onFormSubmit();
                            }
                    );

                }
                // Multiple Form
                else{
                    Utility.showYesNoDialogBox(mActivity, "Info", "Survey Complete or Not?", yesDialogBox -> {
                        yesDialogBox.dismiss();
                        bin.setForm_status(Utility.SurveyCompleted);
                        bin.setPolygon_status(Utility.PolygonStatusCompleted);
                        isSurveyComplete = true;
                        onFormSubmit();
                    }, noDialogBox -> {
                        noDialogBox.dismiss();
                        isSurveyComplete = false;
                        bin.setForm_status(Utility.SurveyNotComplete);
                        bin.setPolygon_status(Utility.PolygonStatusNotComplete);
                        onFormSubmit();
                    });
                }
                break;

            case R.id.btExit:
                onFormExit();
                break;

            case R.id.btAddFormTable:
                addFormTable();
                break;

            case R.id.btFileUpload:
                fileUpload();
                break;

        }
    }

    private void showAlertDialog(Activity mActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("Please capture a photo with GPS location enabled.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

//------------------------------------------------------- File Upload ----------------------------------------------------------------------------------------------------------------------

    private void fileUpload(){
        try{
            Utility.openMultipleFilePicker(mActivity);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

//------------------------------------------------------- Camera Photo Upload ----------------------------------------------------------------------------------------------------------------------

    private void updatePreviewUI(boolean isUpdate) {
        binding.llMain.setVisibility(isUpdate ? View.GONE : View.VISIBLE);
        binding.llPreview.setVisibility(isUpdate ? View.VISIBLE : View.GONE);
    }

    private void cameraPhotoUpload(){
        // Camera
        try{
            Utility.openCamera(mActivity, imageFileUtils, path -> {
                if(path != null  ){
                    cameraDestFileTemp = new File(path);

                }
            });
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    private StringBuilder getGeoTagData() {
        StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Latitude : " + "" + latitude);
            stringBuilder.append("\n");
            stringBuilder.append("Longitude : " + "" + longitude);
            stringBuilder.append("\n");
            stringBuilder.append("Date: " + Utility.getRecordDate());
            return stringBuilder;
        }


//------------------------------------------------------- Add Form Table ----------------------------------------------------------------------------------------------------------------------

    @SuppressLint("NotifyDataSetChanged")
    private void addFormTable() {
        Dialog fDB = new Dialog(this);
        fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fDB.setCancelable(false);
        fDB.setContentView(R.layout.dialogbox_form_table_item);
        fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // Exit Button
        Button btExit = fDB.findViewById(R.id.dbExit);
        btExit.setOnClickListener(view -> fDB.dismiss());
        // Linear Layout
        LinearLayout ll_table6 = fDB.findViewById(R.id.ll_table6);
        // Init Edit Text
        EditText sr_no = fDB.findViewById(R.id.form_table_sr_no);
        EditText floor = fDB.findViewById(R.id.form_table_floor);
        EditText length = fDB.findViewById(R.id.form_table_length);
        EditText height = fDB.findViewById(R.id.form_table_height);
        EditText area = fDB.findViewById(R.id.form_table_area);
        EditText building_age = fDB.findViewById(R.id.form_table_building_age);
        EditText annual_rent = fDB.findViewById(R.id.form_table_annual_rent);
        EditText tag_no = fDB.findViewById(R.id.form_table_tag_no);
        // Spinner
        Spinner building_type = fDB.findViewById(R.id.form_sp_building_type);
        Spinner building_use_type = fDB.findViewById(R.id.form_sp_building_use_type);

        if (selectedPropertyUserType.equalsIgnoreCase("भाडेकरू")) {
            ll_table6.setVisibility(View.VISIBLE);
        } else {
            ll_table6.setVisibility(View.GONE);
            annual_rent.setText("");
        }


        // Building Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_type, android.R.layout.simple_spinner_item);
        adapterBuildingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_type.setAdapter(adapterBuildingType);
        building_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // Building Use Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingUseType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_use_type, android.R.layout.simple_spinner_item);
        adapterBuildingUseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_use_type.setAdapter(adapterBuildingUseType);
        building_use_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_use_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Add Button
        Button addFormTable = fDB.findViewById(R.id.dbAdd);
        Log.e(TAG, "Add Button clicked");
        addFormTable.setOnClickListener(view -> {
            if (adapterFormTable != null) {
                formTableModels.add(new FormTableModel(
                        sr_no.getText().toString(),
                        floor.getText().toString(),
                        db_form_sp_building_type,
                        db_form_sp_building_use_type,
                        length.getText().toString(),
                        height.getText().toString(),
                        area.getText().toString(),
                        building_age.getText().toString(),
                        annual_rent.getText().toString(),
                        tag_no.getText().toString()
                ));
                if(isEditMode){
                    formModel.setDetais(adapterFormTable.getFormTableModels());
                }
                adapterFormTable.notifyDataSetChanged();


                db_form_sp_building_type = "";
                db_form_sp_building_use_type = "";

                Toast.makeText(mActivity, "Details Added", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Details Added");
            }
            fDB.dismiss();
            Log.e(TAG, "FORM CLOSED");

        });
        fDB.show();
        Log.e(TAG, "FORM SHOW");

    }
    //function for update data binding


//------------------------------------------------------- Submit ----------------------------------------------------------------------------------------------------------------------

    private void onFormSubmit() {
        // GPS
        Log.e(TAG, "onFormSubmit: found" + latitude + "<-->" + longitude);
        if (latitude == null || latitude.isEmpty() && longitude == null || longitude.isEmpty()) {
            showAlertDialog(mActivity);
            return;
        }

        Log.e(TAG, "Form Submitted");

        // Geom Array not Null
        if (!Utility.isEmptyString(polygonID)) {

            unique_number = String.valueOf(Utility.getToken());
            datetime = Utility.getDateTime();

            bin.setUnique_number(unique_number);
            bin.setForm_number(Utility.getStringValue(binding.formNewPropertyNo.getText().toString()));
            bin.setFid(Utility.getStringValue(generatePropertyID(polygonID, isMultipleForm, lastKey)).split("/")[1]);


            // Single Mode
            if (!isMultipleForm) {
                bin.setForm_mode(Utility.isSingleMode);
            }
            // Multiple Mode
            else {
                bin.setForm_mode(Utility.isMultipleMode);
            }
            bin.setPolygon_id(polygonID);
            bin.setForm_id(formID);
            bin.setUser_id(Utility.getSavedData(mActivity, Utility.LOGGED_USERID));
            bin.setLatitude(latitude);
            bin.setLongitude(longitude);
            bin.setCreated_on(Utility.getDateTime());

            // Form Fields
            bin.setOwner_name(Utility.getEditTextValue(binding.formOwnerName));
            bin.setOld_property_no(Utility.getEditTextValue(binding.formOldPropertyNo));

            // 3
            bin.setNew_property_no(Utility.getStringValue(binding.formNewPropertyNo.getText().toString()));

            bin.setProperty_name(Utility.getEditTextValue(binding.formPropertyName));
            bin.setProperty_address(Utility.getEditTextValue(binding.formPropertyAddress));
            bin.setProperty_user(Utility.getEditTextValue(binding.formPropertyUser));
            bin.setResurvey_no(Utility.getEditTextValue(binding.formResurveyNo));
            bin.setGat_no(Utility.getEditTextValue(binding.formGatNo));
            bin.setZone(Utility.getEditTextValue(binding.formZone));
            bin.setWard(ward_no);
            bin.setMobile(Utility.getEditTextValue(binding.formMobile));
            bin.setEmail(Utility.getEditTextValue(binding.formEmail));
            bin.setAadhar_no(Utility.getEditTextValue(binding.formAadharNo));

            // 15
            bin.setGis_id(Utility.getStringValue(binding.formGisId.getText().toString()));

            bin.setProperty_type(Utility.getStringValue(selectedPropertyType));
            bin.setNo_of_floor(Utility.getEditTextValue(binding.formNoOfFloors));

            bin.setProperty_release_date(Utility.getEditTextValue(binding.formPropertyReleaseDate));
            bin.setBuild_permission(Utility.getStringValue(selectedBuildPermission));
            bin.setBuild_completion_form(Utility.getStringValue(selectedBuildCompletionForm));
            bin.setMetal_road(Utility.getStringValue(selectedMetalRoad));
            bin.setIs_toilet_available(Utility.getStringValue(selectedIsToiletAvailable));
            bin.setTotal_toilet(Utility.getEditTextValue(binding.formTotalToilet));
            bin.setToilet_type(Utility.getStringValue(selectedToiletType));
            bin.setIs_streetlight_available(Utility.getStringValue(selectedIsStreetlightAvailable));
            bin.setIs_water_line_available(Utility.getStringValue(selectedIsWaterLineAvailable));

            bin.setTotal_water_line1(Utility.getStringValue(Utility.getEditTextValue(binding.formTotalWaterLine1)));
            bin.setTotal_water_line2(Utility.getStringValue(Utility.getStringValue(selectedTotalWaterLine2)));

            bin.setWater_use_type(Utility.getStringValue(selectedWaterUseType));
            bin.setSolar_panel_available(Utility.getStringValue(selectedSolarPanelAvailable));
            bin.setSolar_panel_type(Utility.getStringValue(selectedSolarPanelType));
            bin.setRain_water_harvesting(Utility.getStringValue(selectedRaniWaterHarvesting));
            bin.setIs_drainage_available(Utility.getStringValue(selectedDrainageSystemAvailable));


            bin.setPlot_area(Utility.getEditTextValue(binding.formPlotArea));
            bin.setProperty_area(Utility.getEditTextValue(binding.formPropertyArea));
            bin.setTotal_area(Utility.getEditTextValue(binding.formTotalArea));
            bin.setVersion(BuildConfig.VERSION_NAME);

            bin.setProperty_images("");
            bin.setPlan_attachment("");
            formModel.setForm(bin);
            formModel.setDetais(adapterFormTable.getFormTableModels());

            // Upload Form
            if (SystemPermission.isInternetConnected(mActivity)) {
                SaveFormToServe(formModel);
            } else {
                SaveFormToDatabase(formModel);
            }
        } else {
            // When FID is Null then!
            Utility.showToast(mActivity, "Field Is Required");
        }
    }

//------------------------------------------------------- SaveFormToServe/Local ----------------------------------------------------------------------------------------------------------------------

    private void submitForm() {
        dismissProgressBar();
        String generateID = dataBaseHelper.getGenerateID(polygonID);
        if (!Utility.isEmptyString(generateID)) {
            dataBaseHelper.updateGenerateID(polygonID, generatePropertyID(polygonID, isMultipleForm, lastKey).split("/")[1]);
        } else {
            dataBaseHelper.insertGenerateID(polygonID, generatePropertyID(polygonID, isMultipleForm, lastKey).split("/")[1]);
        }

        if (isSurveyComplete) {
            dataBaseHelper.updateGeoJsonPolygon(polygonID, Utility.PolygonStatusCompleted);
        } else {
            dataBaseHelper.updateGeoJsonPolygon(polygonID, Utility.PolygonStatusNotComplete);
        }
        Log.e(TAG, "is Survey Complete - >" + isSurveyComplete);

        Intent intent = new Intent();
        intent.putExtra(Utility.PASS_POLYGON_ID, polygonID);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void SaveFormToServe(FormModel formModel) {
        showProgressBar("Form Uploading...");
        Map<String, String> params = new HashMap<>();
        params.put("data", Utility.convertFormModelToString(formModel));
        Log.e(TAG, "Form -> " + Utility.convertFormModelToString(formModel));
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }


    private void processUploadLastKeyToServer(String polygonID, String lastKey) {
        showProgressBar("Form Uploading...");
        Map<String, String> params = new HashMap<>();
//        //poly_id, counter
        LastKeyModel lastKeyModel = new LastKeyModel();
        lastKeyModel.setCounter(lastKey);
        lastKeyModel.setPoly_id(polygonID);
        params.put("data", Utility.convertlastKeyModelToString(lastKeyModel));
        // params.put("data",);
        Log.e(TAG, "Last key  Uploaded -> " + params.toString());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_SET_COUNTER, URL_Utility.WS_SET_COUNTER, params, false, false);
    }

    private void processUploadLastKeyToServer1(String polygonID, String lastKey) {
        showProgressBar("Form Uploading...");
        Map<String, String> params = new HashMap<>();
//        //poly_id, counter
        LastKeyModel lastKeyModel = new LastKeyModel();
        lastKeyModel.setCounter(lastKey);
        lastKeyModel.setPoly_id(polygonID);
        params.put("data", Utility.convertlastKeyModelToString(lastKeyModel));
        // params.put("data",);
        Log.e(TAG, "Last key  Uploaded -> " + params.toString());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_SET_COUNTER1, URL_Utility.WS_SET_COUNTER1, params, false, false);
    }

    private void SaveFormToDatabase(FormModel formModel) {
        dataBaseHelper.insertGeoJsonPolygonForm(polygonID, Utility.convertFormModelToString(formModel), "f", sbFilePathLocal.toString(), sbCameraImagePathLocal.toString());
        Log.e(TAG, "Form Save To Local Database");
        dismissProgressBar();
        String generateID = dataBaseHelper.getGenerateID(polygonID);
        Log.e(TAG, "last Key ID - > " + generateID);
        if (!Utility.isEmptyString(generateID) && !generateID.equals("0")) {
            dataBaseHelper.updateGenerateIDLocal(polygonID, generatePropertyID(polygonID, isMultipleForm, lastKey).split("/")[1]);
        } else {
            dataBaseHelper.insertGenerateIDLocal(polygonID, generatePropertyID(polygonID, isMultipleForm, lastKey).split("/")[1]);
        }
        showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
            okDialogBox.dismiss();
            submitForm();
        });
    }

//------------------------------------------------------- onSuccessResponse ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {
        Log.e(TAG,"Response: " + response);
        // Form
        if(responseCode == URL_Utility.ResponseCode.WS_FORM || responseCode == URL_Utility.ResponseCode.WS_UPDATE_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){

                        // Changes
                        boolean isFile   = false;
                        boolean isCamera = false;

                            String unique_number = formModel.getForm().getUnique_number();

                            // File
                            if (sbFilePath != null && !Utility.isEmptyString(sbFilePath.toString())) {
                                Log.e(TAG, "Form Contain File");
                                isFile = true;
                            }

                        // Camera
                        if(sbCameraImagePath != null && !Utility.isEmptyString(sbCameraImagePath.toString())){
                            Log.e(TAG, "Form Contain Camera Image");
                            isCamera = true;
                        }

                        // When User Select Any File or Camera
                        if(isFile || isCamera){

                            if(isFile){
                                new FormActivity.FileUploadServer(sbFilePath,formID,unique_number,TYPE_FILE,false).execute();
                            }
                            else{
                                isFileUpload = true;
                            }

                            if(isCamera){
                                new FormActivity.FileUploadServer(sbCameraImagePath,formID,unique_number, TYPE_CAMERA,true).execute();
                            }
                            else{
                                isCameraUpload = true;
                            }
                        }
                        // When User Select Only Form
                        else{
                            Log.e(TAG,"User Upload only Form not Camera File or File");
                            Log.e(TAG,"Form Upload to Server SuccessFully");
                            processUploadLastKeyToServer1(polygonID,generatePropertyID(polygonID,isMultipleForm,lastKey).split("/")[1]);
                        }
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }
            }
            else{
                Log.e(TAG, "Form Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }
        // Set Counter
        if(responseCode == URL_Utility.ResponseCode.WS_SET_COUNTER){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "SET Counter Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                            dismissProgressBar();
                            showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
                                okDialogBox.dismiss();
                                if(formModel != null){
                                    dataBaseHelper.insertGeoJsonPolygonForm(polygonID,Utility.convertFormModelToString(formModel),"t",sbFilePathLocal.toString(),sbCameraImagePathLocal.toString());
                                }
                                submitForm();
                            });
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }
            }
            else{
                Log.e(TAG, "SET Counter Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }

        // Set Counter 1
        if(responseCode == URL_Utility.ResponseCode.WS_SET_COUNTER1){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "SET Counter Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        dismissProgressBar();
                        showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
                            okDialogBox.dismiss();
                            if(formModel != null)
                            {
                                dataBaseHelper.insertGeoJsonPolygonForm(polygonID,Utility.convertFormModelToString(formModel),"t","","");
                            }
                            submitForm();
                        });
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }
            }
            else{
                Log.e(TAG, "SET Counter Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }


    }

    public void showOKDialogBox(Context context, String msg, Utility.DialogBoxOKClick dialogBoxOKClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> dialogBoxOKClick.OkClick(dialog))
                .create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

//------------------------------------------------------- onErrorResponse ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error) {
        dismissProgressBar();
        Log.e(TAG,"Response Code: "+ responseCode);
        Log.e(TAG,"Error: "+ error.getMessage());
        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
    }

//------------------------------------------------------- Exit ----------------------------------------------------------------------------------------------------------------------

    private void onFormExit(){
        setResult(RESULT_CANCELED);
        finish();
    }

//------------------------------------------------------- progressBar ----------------------------------------------------------------------------------------------------------------------

    private void showProgressBar(String message) {
        if (progressDialog == null && !isFinishing()) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

    private void dismissProgressBar() {
        if (progressDialog != null && !isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

//------------------------------------------------------- onBackPressed ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        finish();
    }

//------------------------------------------------------- onActivity Result ----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "PHOTO CLICKED" + sbCameraImagePath);

        // Camera Photo/Image Request
        if(requestCode == Utility.REQUEST_TAKE_PHOTO ){
            if(resultCode == Activity.RESULT_OK ){
                //if data is not null it will to take picture
                if ( data != null){
                    String ownerName = data.getStringExtra(Utility.PASS_OWNER_NAME);
                    binding.formOwnerName.setText(ownerName);
                }

                try{
                    sbCameraImagePath = new StringBuilder();
                    sbCameraImageName = new StringBuilder();
                    binding.txtGeoTag.setText(getGeoTagData());

                    Bitmap bitmapPreview = ImageFileUtils.handleSamplingAndRotationBitmap(mActivity, Uri.fromFile(cameraDestFileTemp));
                    File destFileTemp2 = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity) );
                    ImageFileUtils.saveBitmapToFile(bitmapPreview, destFileTemp2);
                    binding.imgPreview.setImageBitmap(bitmapPreview);
                    updatePreviewUI(true);

                    new Handler().postDelayed(() -> {

                        File destFile = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity));
                        if (ImageFileUtils.takeScreenshot(binding.llPreview, destFile)) {
                            Log.e("Picture", "screenshot capture success");
                        } else {
                            destFile = destFileTemp2;
                            Log.e("Picture", "screenshot capture failed");
                        }
                        sbCameraImagePath.append(destFile.getPath());
                        //to set multiple images
                        if (sbCameraImagePathLocal.length() > 0){
                            sbCameraImagePathLocal.append(",");
                            formDBModel.setCameraPath(sbCameraImagePathLocal.toString());

                        }
                        sbCameraImagePathLocal.append("local").append("#").append(destFile.getAbsolutePath());
                        sbCameraImageName.append(destFile.getName());
                        updatePreviewUI(false);
                        Log.e(TAG,"Camera Image Path: " + destFile.getAbsolutePath());
                        // Set  Multiple Image
                        binding.llImageCapturedView.setVisibility(View.VISIBLE);
                        ImageView imageView = new ImageView(mActivity);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 300);
                        imageView.setLayoutParams(layoutParams);

                        Glide.with(mActivity).load(destFile.getAbsolutePath()).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageView);
                        binding.llImageCapturedView.addView(imageView);
// set Image
//                        Bitmap bitmap =  (ImageFileUtils.getBitmapFromFilePath(destFile.getAbsolutePath()));
//                        Glide.with(mActivity).load(bitmap).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(binding.imgCaptured);
                    },100);
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }


            }
        }

        else if(requestCode == IMAGE_EDITOR_RESULT_CODE){

            try{
                assert data != null;
                Uri uri = data.getData();
                File sourceFile = new File(imageFileUtils.getPathUri(mActivity, uri));
                File destFileTemp = imageFileUtils.getDestinationFile(imageFileUtils.getRootDirFile(mActivity));
                imageFileUtils.copyFile(sourceFile, destFileTemp);

                sbCameraImagePath = new StringBuilder();
//                sbCameraImagePathLocal = new StringBuilder();
                sbCameraImageName = new StringBuilder();

                binding.txtGeoTag.setText(getGeoTagData());

                Bitmap bitmapPreview = ImageFileUtils.handleSamplingAndRotationBitmap(mActivity, Uri.fromFile(destFileTemp));
                File destFileTemp2 = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity) );
                ImageFileUtils.saveBitmapToFile(bitmapPreview, destFileTemp2);
                binding.imgPreview.setImageBitmap(bitmapPreview);

                updatePreviewUI(true);

                new Handler().postDelayed(() -> {
                    File destFile = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity));
                    if (ImageFileUtils.takeScreenshot(binding.llPreview, destFile)) {
                        Log.e("Picture", "screenshot capture success");
                    } else {
                        destFile = destFileTemp2;
                        Log.e("Picture", "screenshot capture failed");
                    }
                    sbCameraImagePath.append(destFile.getPath());
                    sbCameraImagePathLocal.append("local").append("#").append(destFile.getAbsolutePath());
                    sbCameraImageName.append(destFile.getName());
                    updatePreviewUI(false);
                    Log.e(TAG,"Camera Image Path: " + destFile.getAbsolutePath());
                    // Set Image
                    Bitmap bitmap =  (ImageFileUtils.getBitmapFromFilePath(destFile.getAbsolutePath()));
                    Glide.with(mActivity).load(bitmap).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(binding.imgCaptured);
                }, 100);
            }
            catch (Exception e){
                Glide.with(mActivity).load(R.drawable.ic_no_image).into(binding.imgCaptured);
                Log.e(TAG, e.getMessage());
            }
        }

        // File Request
        else if(requestCode == Utility.PICK_FILE_RESULT_CODE){
            if(resultCode == Activity.RESULT_OK){
                assert data != null;
                Uri uri = data.getData();
                // Multiple Files Selected
                if(null != data.getClipData()){
                    Log.e(TAG,"Multiple File Selected");
                    ArrayList<Uri> multipleFileList = new ArrayList<>();
                    int n = data.getClipData().getItemCount(); // size
                    for(int i=0; i<n; i++){
                        Uri multipleUri = data.getClipData().getItemAt(i).getUri();
                        multipleFileList.add(multipleUri);
                    }
                    sbFileName = new StringBuilder();
                    sbFilePath = new StringBuilder();
                    sbFilePathLocal = new StringBuilder();
                    for(int i=0; i<multipleFileList.size(); i++){
                        File sourceFile = new File(imageFileUtils.getPathUri(mActivity, multipleFileList.get(i)));
                        File destFile = imageFileUtils.getDestinationFileDoc(imageFileUtils.getRootDirFileDoc(mActivity), ImageFileUtils.getExtFromUri(this, multipleFileList.get(i)));
                        try{
                            imageFileUtils.copyFile(sourceFile, destFile);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if(destFile != null){
                            sbFileName.append(destFile.getName());
                            sbFilePath.append(destFile.getPath());
                            sbFilePathLocal.append("local").append("%").append(destFile.getName()).append("#").append(destFile.getPath());
                            if(i < multipleFileList.size() - 1){
                                sbFilePath.append(",");
                                sbFileName.append(",");
                                sbFilePathLocal.append(",");
                            }
                        }

                    }
                    //for file upload [existing and new file both seen together]
                    Log.e(TAG, "fileUploadList" + fileUploadList.size());
                    for (int i = 0; i < fileUploadList.size(); i++) {
                        Log.e(TAG, "fileUploadList" + fileUploadList.size());

                        sbFilePath.append(",");
                        sbFileName.append(",");
                        sbFilePathLocal.append(",");
                        sbFileName.append(fileUploadList.get(i).getName());
                        sbFilePath.append(fileUploadList.get(i).getPath());
                        sbFilePathLocal.append("local").append("%").append(fileUploadList.get(i).getName()).append("#").append(fileUploadList.get(i).getPath());

                        if (i < fileUploadList.size() - 1) {
                            sbFilePath.append(",");
                            sbFileName.append(",");
                            sbFilePathLocal.append(",");
                        }

                    }

                    binding.tvFileUploadName.setText("File Selected");

                    binding.tvformTotalNoFileSelected.setText("Total File Upload : " + multipleFileList.size() );

                }
                // Single File Selected
                else{
                    Log.e(TAG,"Single File Selected");
                    File sourceFile = new File(imageFileUtils.getPathUri(mActivity, uri));
                    File destFile = imageFileUtils.getDestinationFileDoc(imageFileUtils.getRootDirFileDoc(mActivity), ImageFileUtils.getExtFromUri(this, uri));
                    try{
                        imageFileUtils.copyFile(sourceFile, destFile);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    if(destFile != null){
                        sbFileName.append(destFile.getName());
                        sbFilePath.append(destFile.getPath());
                        sbFilePathLocal.append("local").append("%").append(destFile.getName()).append("#").append(destFile.getPath());

                        for (int i = 0; i < fileUploadList.size(); i++) {
                            Log.e(TAG, "fileUploadList" + fileUploadList.size());

                            sbFilePath.append(",");
                            sbFileName.append(",");
                            sbFilePathLocal.append(",");
                            sbFileName.append(fileUploadList.get(i).getName());
                            sbFilePath.append(fileUploadList.get(i).getPath());
                            sbFilePathLocal.append("local").append("%").append(fileUploadList.get(i).getName()).append("#").append(fileUploadList.get(i).getPath());

                            if (i < fileUploadList.size() - 1) {
                                sbFilePath.append(",");
                                sbFileName.append(",");
                                sbFilePathLocal.append(",");
                            }

                        }
                        binding.tvFileUploadName.setText("File Selected");
                        binding.tvformTotalNoFileSelected.setText("Total File Upload : 1");
                    }
                }

                Log.e(TAG, "File Path: "+sbFilePath.toString());
            }

        }
    }

//------------------------------------------------- File Upload ------------------------------------------------------------------------------------------------------------------------

    private class FileUploadServer extends AsyncTask<Void, Integer, String> {
        StringBuilder filePathData;
        String form_id;
        String unique_number;
        String type;
        boolean isCameraFileUpload = false;


        public FileUploadServer(StringBuilder filePathData, String form_id, String unique_number,String type, boolean isCameraFileUpload) {
            this.filePathData = filePathData;
            this.form_id = form_id;
            this.unique_number = unique_number;
            this.type = type;
            this.isCameraFileUpload = isCameraFileUpload;

            if(type.equals(TYPE_FILE)){
                Log.e(TAG, "File Type ");
                isFileUpload = false;
            }
            else if(type.equals(TYPE_CAMERA) ){
                Log.e(TAG, "Camera Type ");
                isCameraUpload = false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }
        @Override
        protected
        String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile(){
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_Utility.WS_FORM_FILE_UPLOAD);
            Log.e(TAG,"File-Upload API -> " + URL_Utility.WS_FORM_FILE_UPLOAD);

            try {
                if(filePathData != null ){
                    if(!Utility.isEmptyString(filePathData.toString())){
                        // File Path!
                        String[] path = filePathData.toString().split(",");
                        Log.e(TAG, "path: "+ filePathData.toString());
                        for (String filepath : path) {
                            File sourceFile = new File(filepath);
                            String data = "";
                            JSONObject params = new JSONObject();
                            try {
                                if(isCameraFileUpload){
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PROPERTY_IMAGES);
                                }
                                else{
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PLAN_ATTACHMENT);
                                }
                                params.put(Utility.PASS_UNIQUE_NUMBER, unique_number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Encrypt Data!
                            data = params.toString();
                            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                            entity.addPart(URL_Utility.PARAM_FILE_UPLOAD, new FileBody(sourceFile));
                            entity.addPart("data", new StringBody(data));
                            Log.e(TAG, "File-Upload Data -> "+ data);

                            totalSize = entity.getContentLength();
                            httppost.setEntity(entity);
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity r_entity = response.getEntity();
                            int statusCode = response.getStatusLine().getStatusCode();

                            if (statusCode == 200) {
                                responseString = EntityUtils.toString(r_entity);
                            } else {
                                dismissProgressBar();
                                responseString = "Error occurred! Http Status Code: " + statusCode;
                                Log.e(TAG, responseString);
                                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                            }
                        }
                    }
                    else{
                        Log.e(TAG,"filePathData is Empty");
                    }
                }
                else{
                    Log.e(TAG,"filePathData null");
                    //  Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }

            } catch (IOException e) {
                dismissProgressBar();
                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                dismissProgressBar();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            String response = result;
            Log.e(TAG, response);
            if(!response.equals("")){
                try {
                    JSONObject mLoginObj = new JSONObject(response);
                    String status = mLoginObj.optString("status");
                    if (status.equalsIgnoreCase("Success")){

                        switch (type) {
                            case TYPE_FILE:
                                Log.e(TAG, "File Upload Successfully");
                                isFileUpload = true;
                                break;

                            case TYPE_CAMERA:
                                Log.e(TAG, "Camera File Upload Successfully");
                                isCameraUpload = true;
                                break;
                        }

                        if((isCameraUpload && isFileUpload )){
                            Log.e(TAG,"Save File Successfully");
                            processUploadLastKeyToServer(polygonID,generatePropertyID(polygonID,isMultipleForm,lastKey).split("/")[1]);
                        }
                    }
                    else{
                        Log.e(TAG,status);
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }

                } catch (JSONException e) {
                    Log.e(TAG,e.getMessage());
                    dismissProgressBar();
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                Log.e(TAG, response);
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
            super.onPostExecute(result);
        }
    }

//---------------------------------------------- onPause ------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

//---------------------------------------------- onResume ------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

}
