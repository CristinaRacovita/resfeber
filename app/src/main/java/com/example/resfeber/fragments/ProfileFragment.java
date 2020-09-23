package com.example.resfeber.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.resfeber.R;
import com.example.resfeber.activities.LoginActivity;
import com.example.resfeber.activities.SignInActivity;
import com.example.resfeber.db.MyDatabase;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment implements LocationListener {

    @BindView(R.id.profileImage)
    ImageView mPhoto;
    @BindView(R.id.emailText)
    TextView emailText;
    @BindView(R.id.phoneText)
    EditText phoneText;
    @BindView(R.id.addressText)
    EditText addressText;
    @BindView(R.id.currentLocationText)
    TextView currentLocationText;
    @BindView(R.id.editPhoto)
    ImageView editPhoto;
    @BindView(R.id.saveProfile)
    Button saveProfileButton;
    @BindView(R.id.editProfile)
    Button editProfileButton;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private MyDatabase database;
    private Context context;
    private String userID;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;

    public static final String COLLECTION_NAME = "users/";
    public static final String PROFILE = "/profile.jpg";

    private Location lastLocation;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View menuView = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, menuView);

        database = MyDatabase.getInstance(getApplicationContext());

        editPhoto.setVisibility(View.GONE);
        saveProfileButton.setVisibility(View.GONE);

        phoneText.setEnabled(false);
        addressText.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        if (firebaseAuth.getCurrentUser() != null) {
            StorageReference profileRef = storageReference.child(COLLECTION_NAME + firebaseAuth.getCurrentUser().getUid() + PROFILE);
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getContext()).load(uri).into(mPhoto));
            emailText.setText(firebaseAuth.getCurrentUser().getEmail());
        }

        userID = firebaseAuth.getUid();

        documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    phoneText.setText(documentSnapshot.getString("Phone"));
                    addressText.setText(documentSnapshot.getString("Address"));
                }
            }
        });

        getLocation();

        return menuView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        boolean network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!network_enabled) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, ((DialogInterface.OnClickListener) (paramDialogInterface, paramInt) -> getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }

        lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            currentLocationText.setText(getLocationForDisplay(lastLocation));
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    private String getLocationForDisplay(Location location) {
        if (location != null) {
            String locationDisplay = location.getLatitude() + ", " + location.getLongitude();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String cityName = addresses.get(0).getAddressLine(0);
                locationDisplay += "\n" + cityName;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return locationDisplay;
        }
        return getString(R.string.unknown);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                mPhoto.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }


    public void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child(COLLECTION_NAME + firebaseAuth.getCurrentUser().getUid() + PROFILE);
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getContext()).load(uri).into(mPhoto))).addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show());
    }


    @OnClick(R.id.editPhoto)
    public void editImage(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @OnClick(R.id.logOut)
    public void submitLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        if (SignInActivity.getGoogleSignInClient() != null) {
            SignInActivity.getGoogleSignInClient().signOut();
        }
        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
        startActivity(loginIntent);

        database.getAppDatabase().eventDao().deleteData();

        getActivity().finish();
    }

    @OnClick(R.id.editProfile)
    public void editProfile(View view) {
        saveProfileButton.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);

        phoneText.setEnabled(true);
        addressText.setEnabled(true);

        editPhoto.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.saveProfile)
    public void saveProfile(View view) {
        phoneText.setEnabled(false);
        addressText.setEnabled(false);

        String phoneNumber = phoneText.getText().toString();
        String address = addressText.getText().toString();

        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> userObj = new HashMap<>();
        userObj.put("Phone", phoneNumber);
        userObj.put("Address", address);
        documentReference.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, getString(R.string.edited), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        editPhoto.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        editProfileButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!location.equals(lastLocation)) {
            currentLocationText.setText(getLocationForDisplay(location));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}