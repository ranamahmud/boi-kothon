package com.ranamahmud.boikothon;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ranamahmud.boikothon.drawer.fragments.GivenBookFragment;
import com.ranamahmud.boikothon.drawer.fragments.ManageBookFragment;
import com.ranamahmud.boikothon.drawer.fragments.Profile;
import com.ranamahmud.boikothon.drawer.fragments.SearchBookFragment;
import com.ranamahmud.boikothon.drawer.fragments.TakenBookFragment;
import com.ranamahmud.boikothon.drawer.fragments.WishBookFragment;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import id.zelory.compressor.Compressor;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GivenBookFragment.OnListFragmentInteractionListener,
ManageBookFragment.OnListFragmentInteractionListener,
SearchBookFragment.OnListFragmentInteractionListener,
TakenBookFragment.OnListFragmentInteractionListener,
WishBookFragment.OnListFragmentInteractionListener,
Profile.OnFragmentInteractionListener{

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";
    private Class fragmentClass;
    private Fragment fragment;
    private FloatingActionButton fab;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private NavigationView navigationView;
    private Menu menu;
    private FirebaseUser currentUser;
    private Object IMAGE_DIRECTORY = "boikothon";
    private String name;
    private AlertDialog dialogCreate;
    private String uid;
    private ImageView mImageViewProfile;
    private Uri photoUrl;
    private String email;
    private TextView mName;
    private TextView mEmail;
    private FirebaseAuth mAuth;
    private MenuItem menuSearchBook;
    private MenuItem menuGivenBook;
    private MenuItem menuTakenBook;
    private MenuItem menuWishList;
    private MenuItem menuManageBook;
    private MenuItem menuProfile;
    private MenuItem menuSignIn;
    private MenuItem menuSignOut;
    private StorageReference mStorageRef;
    private String bookTitle;
    private String bookGenre;
    private FirebaseFirestore firebaseFirestore;
    private String bookWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set the serach fragment
        fragment = new SearchBookFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        // check if user logged in or not
//        In onCreate() initialize mAuth

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // if not logged in show sign in


        // request app permissions
        // Ask for device permissions
        requestMultiplePermissions();

        // get the menu items
        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        menuSearchBook = menu.findItem(R.id.nav_book_search);
        menuGivenBook = menu.findItem(R.id.nav_book_given);
        menuTakenBook = menu.findItem(R.id.nav_book_taken);
        menuWishList = menu.findItem(R.id.nav_book_wish);
        menuManageBook = menu.findItem(R.id.nav_book_manage);
        menuProfile = menu.findItem(R.id.nav_profile);

        menuSignIn =   menu.findItem(R.id.nav_sign_in);
        menuSignOut =   menu.findItem(R.id.nav_sign_out);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mName   = navigationView.getHeaderView(0).findViewById(R.id.header_name);
        mEmail   = navigationView.getHeaderView(0).findViewById(R.id.header_email);
        mImageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePic);
        mImageViewProfile.setImageResource(R.drawable.ic_launcher_background);

        // if logged in show sign out
        if (currentUser == null) {
            // No user is signed in
            removeUserProfile();
        } else {
            // User logged in
            setUserProfile();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_book_search:
                fragmentClass = SearchBookFragment.class;
                fab.hide();
                break;
            case R.id.nav_book_manage:
                fragmentClass = ManageBookFragment.class;
                fab.show();
                break;
            case R.id.nav_book_given:
                fragmentClass = GivenBookFragment.class;
                fab.hide();
                break;
            case R.id.nav_book_taken:
                fragmentClass = TakenBookFragment.class;
                fab.hide();
                break;
            case R.id.nav_book_wish:
                fragmentClass = WishBookFragment.class;
                fab.hide();
                break;
            case R.id.nav_profile:
                fragmentClass = Profile.class;
                fab.hide();
                break;
            case R.id.nav_sign_out:
                signOut();
                Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
                fab.hide();
                break;
            case R.id.nav_sign_in:
                createSignInIntent();
                menuSignOut.setVisible(true);
                menuSignIn.setVisible(false);
                fab.hide();
                break;

            default:
                fragmentClass = SearchBookFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // function to request multiple permission
    // function to request permission
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }




    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // code for image picker
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//                    imageview.setImageBitmap(bitmap);
                    showAddDialog(bitmap, uid);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            showAddDialog(thumbnail, uid);
            saveImage(thumbnail);



        }

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                setUserProfile();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        menuSearchBook.setChecked(true);
                        removeUserProfile();

                    }
                });
        // [END auth_fui_signout]


        mImageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePic);
        fragment = new SearchBookFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();



    }

//     user profile related functions
    private void setUserProfile() {
        getSupportActionBar().setTitle("Search Book");

        // Name, email address, and profile photo Url,uid
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name = currentUser.getDisplayName();
        email = currentUser.getEmail();
        photoUrl = currentUser.getPhotoUrl();
        uid = currentUser.getUid();
        // save info to shared preference
        // Check if user's email is verified
        boolean emailVerified = currentUser.isEmailVerified();





        //Fetch values from you database child and set it to the specific view object.
        mName.setText(name);
        mEmail.setText(email);
        Picasso.get().load(photoUrl).into(mImageViewProfile);

        fragment = new SearchBookFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();
        menuSearchBook.setChecked(true);
        // hide unnecessary menu items
        menuGivenBook.setVisible(true);
        menuTakenBook.setVisible(true);
        menuWishList.setVisible(true);
        menuManageBook.setVisible(true);
        menuProfile.setVisible(true);
        menuSignIn.setVisible(false);
        menuSignOut.setVisible(true);


        // get firebase storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }
    private void removeUserProfile() {
        // set search to checked
        menuSearchBook.setChecked(true);
        getSupportActionBar().setTitle("Search Book");
        // Check if user's email is verified

        // The user's ID, unique to the Firebase project.


        //Fetch values from you database child and set it to the specific view object.
        mName.setText("");
        mEmail.setText("");
        Picasso.get().load(photoUrl).into((ImageView) mImageViewProfile);
        // hide unnecessary menu items
        menuGivenBook.setVisible(false);
        menuTakenBook.setVisible(false);
        menuWishList.setVisible(false);
        menuManageBook.setVisible(false);
        menuProfile.setVisible(false);
        menuSignIn.setVisible(true);
        menuSignOut.setVisible(false);

//        change fragment to serach


        mImageViewProfile.setImageResource(R.drawable.ic_launcher_background);

    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void showAddDialog(Bitmap bitmap, final String uid) {

        //create a file to write bitmap data
        File f = new File(getBaseContext().getCacheDir(), "filename.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // compress the image
        File compressedImageFile = null;
        try {
          compressedImageFile = new Compressor(this).compressToFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bookBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

        // get book type strings


        String[] fiction=getResources().getStringArray(R.array.fiction);
        String [] nonFiction = getResources().getStringArray(R.array.non_fiction);
        ArrayList<String> bookType = new ArrayList<String>();
        bookType.addAll(Arrays.asList(fiction));
        bookType.addAll(Arrays.asList(nonFiction));


        // objects
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_book_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextWriter = dialogView.findViewById(R.id.editTextWriter);
        final Spinner spinnerGenre =  dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateArtist);
        final ImageView bookImage = dialogView.findViewById(R.id.imageViewBook);
        bookImage.setImageBitmap(bookBitmap);
        //    final Button buttonChoosePicture = dialogView.findViewById(R.id.choose_picture_button);
        dialogCreate = dialogBuilder.create();

        final File finalCompressedImageFile = compressedImageFile;
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookTitle = editTextName.getText().toString().trim();
                bookWriter = editTextWriter.getText().toString().trim();
                bookGenre = spinnerGenre.getSelectedItem().toString();
                // store the image and get image url
                uplaodBook(Uri.fromFile(finalCompressedImageFile));
                // create book object

            }
        });

//        imageView.setImageBitmap(bitmap);

        // genre spinner items


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, bookType);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);
        dialogCreate.show();

    }



    @Override
    public void onListFragmentInteraction(Book item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    private String uplaodBook(Uri filePath) {
        final String uuidImage = UUID.randomUUID().toString();


        if( filePath!= null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = mStorageRef.child("images/"+ uuidImage);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Book bookUpload = new Book(task.getResult().toString(), bookTitle,bookWriter, bookGenre,true,0, name,uid );
                                    firebaseFirestore = FirebaseFirestore.getInstance();
                                    // upload book
                                    firebaseFirestore.collection("books").add(bookUpload)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                    // change books uid
                                                    dialogCreate.dismiss();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

        return uuidImage;

    }
}
