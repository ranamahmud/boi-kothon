package com.ranamahmud.boikothon.drawer.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ranamahmud.boikothon.MainActivity;
import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchBookFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FirestorePagingAdapter<Book, BookViewHolder> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean loggedInStatus;
    private int RC_SIGN_IN = 123;
    private String name;
    private String email;
    private Uri photoUrl;
    private String uid;
    private static final String TAG = "SearchBookFragment";
    private Class fragmentClass;
    private Fragment fragment;
    private FloatingActionButton fab;
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private NavigationView navigationView;
    private Menu menu;
    private Object IMAGE_DIRECTORY = "boikothon";
    private AlertDialog dialogCreate;
    private ImageView mImageViewProfile;
    private TextView mName;
    private TextView mEmail;
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
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchBookFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchBookFragment newInstance(int columnCount) {
        SearchBookFragment fragment = new SearchBookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchbook_list, container, false);
        // if logged in show sign out

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            loggedInStatus = false;
        } else {
            loggedInStatus = true;
        }
        // select options spinner
        String[] fiction=getResources().getStringArray(R.array.fiction);
        String [] nonFiction = getResources().getStringArray(R.array.non_fiction);
        ArrayList<String> bookType = new ArrayList<String>();
        bookType.add("All");
        bookType.addAll(Arrays.asList(fiction));
        bookType.addAll(Arrays.asList(nonFiction));
        final Spinner spinnerGenre =  view.findViewById(R.id.spinnerGenresSearch);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, bookType);

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapterSpinner);
        // spinner end
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();


        // query
        // The "base query" is a query with no startAt/endAt/limit clauses that the adapter can use
// to form smaller queries for each page.  It should only include where() and orderBy() clauses
        Query baseQuery = rootRef.collection("books").orderBy("bookTitle", Query.Direction.ASCENDING);

// This configuration comes from the Paging Support Library
// https://developer.android.com/reference/android/arch/paging/PagedList.Config.html
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(5)
                .build();

// The options for the adapter combine the paging configuration with query information
// and application-specific options for lifecycle, etc.
        FirestorePagingOptions<Book> options = new FirestorePagingOptions.Builder<Book>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Book.class)
                .build();

         adapter =
                new FirestorePagingAdapter<Book, BookViewHolder>(options) {
                    @NonNull
                    @Override
                    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                        // Create a new instance of the ViewHolder, in this case we are using a custom
                        // layout called R.layout.message for each item
                        View view = LayoutInflater.from(group.getContext())
                                .inflate(R.layout.fragment_searchbook, group, false);

                        return new BookViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull BookViewHolder holder,
                                                    int position,
                                                    @NonNull Book book) {
                        holder.mItem = book;
                        Picasso.get().load(book.getBookImageUrl()).into(holder.mImageBook);
                        holder.mTitle.setText(book.getBookTitle());
                        holder.mAuthor.setText(book.getBookAuthor());
                        holder.mBookRating.setRating(book.getBookRating());
                        holder.mGenre.setText(book.getBookGenre());
                        if(book.isBookAvailable()){
                            holder.mAvailibility.setText("Available");
                            holder.mAvailibility.setTextColor(Color.GREEN);
                        } else{
                            holder.mAvailibility.setText("Not Available");
                            holder.mAvailibility.setTextColor(Color.RED);
                        }
                        holder.mBookOwner.setText(book.getBookOwner());

                        holder.likeButton.setOnLikeListener(new OnLikeListener() {
                            @Override
                            public void liked(LikeButton likeButton) {
                                Log.e("search","Liked");
                            }

                            @Override
                            public void unLiked(LikeButton likeButton) {
                                Log.e("search","unLiked");

                            }
                        });

                        holder.requestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // checked logged in status
                                // if not loggged in request to log in
                                if(loggedInStatus==false){
                                    //after successfully login open the contact or sign in button
                                    createSignInIntent();
                                }

                            }
                        });
                        holder.contactButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // checked logged in status
                                // if not loggged in request to log in
                                if(loggedInStatus==false){
                                    //after successfully login open the contact or sign in button
                                    createSignInIntent();
                                }

                            }
                        });
                    }
                };
        //get the referece of recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.listSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the adapter
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Book item);
    }

    // view holder
    public class BookViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageBook;
        public final TextView mTitle;
        public final TextView mAuthor;
        public final RatingBar mBookRating;
        public final TextView mGenre;
        public final TextView mAvailibility;
        public final TextView mBookOwner;
        public Book mItem;
        public LikeButton likeButton;
        public Button requestButton;
        public Button contactButton;

        public BookViewHolder(View view) {
            super(view);
            mView = view;
            mImageBook = mView.findViewById(R.id.imageViewBook_basic);
            mTitle = mView.findViewById(R.id.textViewTitle);
            mAuthor = mView.findViewById(R.id.textViewAuthor);
            mBookRating = mView.findViewById(R.id.ratingBarBook);
            mGenre = mView.findViewById(R.id.textViewGenre);
            mAvailibility = mView.findViewById(R.id.textViewAvailibility);
            mBookOwner = mView.findViewById(R.id.textViewBookOwner);
            likeButton = mView.findViewById(R.id.like_button);
            requestButton =mView.findViewById(R.id.buttonRequest);
            contactButton = mView.findViewById(R.id.buttonContact);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getBookTitle()+" "+mItem.getBookAuthor() + "'";
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // code for image picker
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }


        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                setUserProfile();
            } else {
                Toast.makeText(getActivity(), "Try again please", Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]

    //     user profile related functions
    private void setUserProfile() {
        navigationView = getActivity().findViewById(R.id.nav_view);
        // Name, email address, and profile photo Url,uid
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        name = currentUser.getDisplayName();
        email = currentUser.getEmail();
        photoUrl = currentUser.getPhotoUrl();
        uid = currentUser.getUid();
        // save info to shared preference
        // Check if user's email is verified
        boolean emailVerified = currentUser.isEmailVerified();




        mName   = navigationView.getHeaderView(0).findViewById(R.id.header_name);
        mEmail   = navigationView.getHeaderView(0).findViewById(R.id.header_email);
        mImageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePic);
        //Fetch values from you database child and set it to the specific view object.
        mName.setText(name);
        mEmail.setText(email);
        Picasso.get().load(photoUrl).into(mImageViewProfile);

        fragment = new SearchBookFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager =getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();
        // get the menu items

        menu = navigationView.getMenu();
        menuSearchBook = menu.findItem(R.id.nav_book_search);
        menuGivenBook = menu.findItem(R.id.nav_book_given);
        menuTakenBook = menu.findItem(R.id.nav_book_taken);
        menuWishList = menu.findItem(R.id.nav_book_wish);
        menuManageBook = menu.findItem(R.id.nav_book_manage);
        menuProfile = menu.findItem(R.id.nav_profile);

        menuSignIn =   menu.findItem(R.id.nav_sign_in);
        menuSignOut =   menu.findItem(R.id.nav_sign_out);

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
}
