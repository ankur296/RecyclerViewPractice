package seedling.corp.recyclerviewpractice.fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.backend.GeofenceTransitionsIntentService;
import seedling.corp.recyclerviewpractice.model.LocReminder;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.FontManager;
import seedling.corp.recyclerviewpractice.utils.Logger;
import seedling.corp.recyclerviewpractice.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateLocReminderFrag extends Fragment implements
//        ResultCallback,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>{


    private static final int SELECT_PICTURE_REQUEST_CODE = 7;
    TextView mPickImageTextView, mRepeatTextView, mSearchLocTextView, mRadiusInfoTextView;
    TextView mTitleAwesomeTextView, mLocAwesomeTextView, mRepeatAwesomeTextView,
            mRadiusAwesomeTextView,mPicAwesomeTextView, mRadiusInfoAwesomeTextView;
    EditText mTitleEditText, mRadiusEditText;
    RadioButton mNextTimeRadio, mEveryTimeRadio;
    PlaceAutocompleteFragment autocompleteFragment;
    ImageView mImageView;
    private int mImageSize;
    private Uri mImageUri;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    double latPos = 0, longPos = 0;
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mGeofenceList ;
    int mRadius = Constants.DEFAULT_RADIUS_IN_METERS; //def value is the minimum 100m
    String mRepeatValue = Constants.REPEAT_NEXT_TIME;
    String mTransitionName;
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 10;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    private LocFragInterface mCallback;
    private FloatingActionButton fabSaveReminder;

    public CreateLocReminderFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load a larger size image to make the activity transition to the detail screen smooth
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;
        Logger.e("onCreateView ENTER");
        final View view = inflater.inflate(R.layout.frag_loc_reminder_create, container, false);
        findViewsByIds(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageView.setTransitionName(mTransitionName);
        }
        //Dont auto open keypad cos of edittext focus
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
        //check if coming from already created reminder or FAB
        // Update values using data attached to the frag while it was dynamically added
        updateValuesFromBundle(getArguments());

        //handle pic
        mPickImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        mSearchLocTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
        handleRepeatRadioGroup(view);
        handleRadiusInfoDialog(view);
        handleSaveFabClicks();
        handleMenuAndAB();
        setAwesomeFonts();
        return view;
    }

    private void setAwesomeFonts() {
        Typeface iconFont = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        mTitleAwesomeTextView.setTypeface(iconFont);
        mLocAwesomeTextView.setTypeface(iconFont);
        mRadiusAwesomeTextView.setTypeface(iconFont);
        mRadiusInfoAwesomeTextView.setTypeface(iconFont);
        mRepeatAwesomeTextView.setTypeface(iconFont);
        mPicAwesomeTextView.setTypeface(iconFont);
    }

    private void handleMenuAndAB() {
        setHasOptionsMenu(true);
        // update the actionbar to show the up carat/affordance
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.create_loc_frag_title);
    }

    private void handleSaveFabClicks() {
        fabSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocReminder mReminder = getReminderData();
                if (mReminder != null) {
                    //save the reminder details in shared preferences
                    SharedPreferences.Editor editor = getActivity()
                            .getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, getActivity().MODE_PRIVATE)
                            .edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(mReminder);
                    editor.putString("" + mReminder.getTitle(), json);
                    editor.apply();
                    mCallback.onSaveLocRemFabClicked();
//                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
    }

    private void handleRadiusInfoDialog(View view) {
        mRadiusInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog radiusInfoDialog =
                        new AlertDialog.Builder(getActivity(), R.style.myDialog)
                                .setCancelable(true)
                                .setMessage(getString(R.string.radius_info))
                                .create();

                radiusInfoDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                radiusInfoDialog.dismiss();
                            }
                        });
                radiusInfoDialog.show();
            }
        });
    }

    private void handleRepeatRadioGroup(View view) {
        RadioGroup repeatRadioGroup=(RadioGroup)view.findViewById(R.id.radio_group_repeat);
        repeatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_repeat_next:
                        mRepeatValue = Constants.REPEAT_NEXT_TIME;
                        break;

                    case R.id.radio_repeat_every:
                        mRepeatValue = Constants.REPEAT_EVERY_TIME;
                        break;
                }
            }
        });
    }

    private void updateValuesFromBundle(Bundle args) {
        if(args != null) {
            loadReminder(new LocReminder(
                    Constants.TYPE_LOC,
                    args.getString(Constants.TITLE_BUNDLE_KEY),
                    args.getDouble(Constants.LAT_BUNDLE_KEY),
                    args.getDouble(Constants.LONG_BUNDLE_KEY),
                    args.getInt(Constants.RADIUS_BUNDLE_KEY),
                    args.getString(Constants.REPEAT_BUNDLE_KEY),
                    args.getString(Constants.PATH_BUNDLE_KEY),
                    args.getBoolean(Constants.ENABLED_BUNDLE_KEY)
            ));
        } else {
            // code to set values if arguments are not set
            mNextTimeRadio.setChecked(true);
        }
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            Logger.e("mGoogleApiClient null so create new");
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void showLocOnMap(LatLng latLng){
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                4000, null);
    }

    private void performSearch() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Logger.e("onActivityCreated ENTER");
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        mapFragment.getMapAsync(this);

        Logger.e("onActivityCreated getMapAsync called");
    }


    public void loadReminder(final LocReminder reminder){

        String path = reminder.getImgPath();
        if (path != null) {
            mImageUri = Uri.parse(path);
            Glide.with(getActivity())
                    .load(mImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .override(mImageSize, mImageSize)
                    .error(R.drawable.notavailable)
                    .into(mImageView);
        }

        String title = reminder.getTitle();
        String repeat = reminder.getRepeat();
        String radius = String.valueOf(reminder.getRadius());
        final double latitude = reminder.getLatitude();
        final double longitude = reminder.getLongitude();

        Logger.e("Load loc reminder with radius = "+radius);
        Logger.e("Load loc reminder with repeat = " + repeat);
        Logger.e("Load loc reminder with lat = " + latitude);
        Logger.e("Load loc reminder with long = " + longitude);
        mTitleEditText.setText(title);

        if (repeat.equalsIgnoreCase(Constants.REPEAT_EVERY_TIME))
            mEveryTimeRadio.setChecked(true);
        else
            mNextTimeRadio.setChecked(true);

        mRadiusEditText.setText(radius);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showLocOnMap(new LatLng(latitude, longitude));
            }
        }, 1500);

    }

    public LocReminder getReminderData(){
        checkLocationSettings();

        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(getActivity(),getResources().getString(R.string.enable_GPS),Toast.LENGTH_SHORT).show();
            return null;
        }

        if (mTitleEditText.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
            return null;
        }
        else if (latPos == 0 || longPos == 0) {//TODO:check when mplace cud be null
            Toast.makeText(getActivity(),getResources().getString(R.string.empty_loc),Toast.LENGTH_SHORT).show();
            return null;
        }

        //Its ensured at this point that we have enuff data so set geofence
        //TODO: Check for null lat n long values
        mRadius = Integer.parseInt(mRadiusEditText.getText().toString());

        Logger.e("Create Loc rem with radius = " + mRadius);
        Logger.e("Create Loc rem with lat = " + latPos);
        Logger.e("Create Loc rem with long = " + longPos);
        addGeofence();
        return new LocReminder(
                Constants.TYPE_LOC,
                mTitleEditText.getText().toString(),
                latPos,
                longPos,
                mRadius,
                mRepeatValue,
                mImageUri == null ? null : mImageUri.toString(),
                true
        );
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
        intent.putExtra("title", mTitleEditText.getText().toString());
        intent.putExtra("path", mImageUri == null ? null : mImageUri.toString());
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void addGeofence(){
        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this geofence.
                .setRequestId(mTitleEditText.getText().toString())
                .setCircularRegion(latPos, longPos, mRadius)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setLoiteringDelay(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());

        Logger.e("Trying to add Geofence at " + latPos + " ," + longPos);

        //////
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess())
                    Toast.makeText(getActivity(), "SUCCESSFULLY ADDED GEOFENCE", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(), "FAILed to add geofence", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void findViewsByIds(View view){
        mPickImageTextView = (TextView) view.findViewById(R.id.tv_choose_img);
        mRepeatTextView = (TextView) view.findViewById(R.id.tv_choose_img);
        mTitleEditText = (EditText) view.findViewById(R.id.edit_title);
        mRadiusEditText = (EditText) view.findViewById(R.id.edit_radius);
        mImageView = (ImageView) view.findViewById(R.id.iv_pic);
        mRadiusInfoTextView = (TextView) view.findViewById(R.id.tv_radius_info_icon);
        mSearchLocTextView = (TextView) view.findViewById(R.id.tv_search_loc);
        mNextTimeRadio = (RadioButton) view.findViewById(R.id.radio_repeat_next);
        mEveryTimeRadio = (RadioButton) view.findViewById(R.id.radio_repeat_every);
        fabSaveReminder = (FloatingActionButton) view.findViewById(R.id.fab_save_loc);
        mPicAwesomeTextView = (TextView) view.findViewById(R.id.tv_attach_icon);
        mLocAwesomeTextView = (TextView) view.findViewById(R.id.tv_loc_icon);
        mRepeatAwesomeTextView = (TextView) view.findViewById(R.id.tv_repeat_icon);
        mTitleAwesomeTextView = (TextView) view.findViewById(R.id.tv_title_icon);
        mRadiusAwesomeTextView = (TextView) view.findViewById(R.id.tv_radius_icon);
        mRadiusInfoAwesomeTextView = (TextView) view.findViewById(R.id.tv_radius_info_icon);
    }


    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DCIM), albumName);
        return file;
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final String fname = Utils.getUniqueImageFilename();
        final File root = getAlbumStorageDir(getActivity() , fname);
        mImageUri = Uri.fromFile(root);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    public void setTransitionName(String name){
        this.mTransitionName = name;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            //SUCCESS CASES
            if (resultCode == Activity.RESULT_OK) {

                if (requestCode == SELECT_PICTURE_REQUEST_CODE) {

                    final boolean isCamera;

                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri selectedImageUri;
                    if (isCamera) {
                        selectedImageUri = mImageUri;
                    } else {
                        selectedImageUri = data == null ? null : data.getData();
                        mImageUri = selectedImageUri;
                    }

                    Logger.e("img sent to popup" + mImageUri.toString());
                    Glide.with(getActivity())
                            .load(selectedImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .placeholder(R.drawable.empty_photo)
                            .override(mImageSize, mImageSize)
                            .error(R.drawable.notavailable)
                            .into(mImageView);

                    mPickImageTextView.setText(getString(R.string.field_pic_hint2));
                }
                //case:sucessfull execution of places API
                else if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    showLocOnMap(place.getLatLng());
                    latPos = place.getLatLng().latitude;
                    longPos = place.getLatLng().longitude;
                    Logger.e("Place Searched: " + place.getName());
                    Logger.e("Place Searched, Lat: " + latPos);
                    Logger.e("Place Searched, Long: " + longPos);
                }
                // Check for the integer request code originally supplied to startResolutionForResult().
                else if (requestCode == REQUEST_CHECK_SETTINGS) {
                    Logger.e("User agreed to make required location settings changes.");
//                startLocationUpdates();
                }
            }

            //FAILURE CASES
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Logger.e("Places API error : " + status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.

                //only handling request settings failure case now
                if (requestCode == REQUEST_CHECK_SETTINGS)
                    Logger.e("User chose not to make required location settings changes.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.e("onMapReady ENTER");
        mMap = googleMap;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Logger.e( "All location settings are satisfied.");
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Logger.e("Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Logger.e("PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Logger.e("Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onAttach(Context activity) {
        Logger.e("Main frag ATTACHED !!");
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (LocFragInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case android.R.id.home:
                //called when the up affordance/carat in actionbar is pressed
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    // Container Activity must implement this interface
    public interface LocFragInterface {
        void onSaveLocRemFabClicked();
    }
}
