package com.ranamahmud.boikothon.drawer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView.Adapter<BookViewHolder> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean loggedInStatus;
    private int RC_SIGN_IN = 123;
    private static final String TAG = "SearchBookFragment";
    private SearchView searchView;
    private RadioGroup radioGroupsearch;
    private int searchTypeId;
    private String searchGenre;
    private ArrayList<Book> bookArrayList = new ArrayList<>();

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
        searchView = view.findViewById(R.id.searchView);
        radioGroupsearch = view.findViewById(R.id.searchType);


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

        radioGroupsearch = view.findViewById(R.id.searchType);
        //Grab de EditText from the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("search",query);
                searchTypeId = radioGroupsearch.getCheckedRadioButtonId();
                searchGenre = spinnerGenre.getSelectedItem().toString();
                Log.e("search",searchGenre);
                switch (searchTypeId){
                    case R.id.radioButtonAll:
                        Log.e("search","All");
                        break;
                    case R.id.radioButtonTitle:
                        Log.e("search","Titlte");
                        break;
                    case R.id.radioButtonAuthor:
                        Log.e("search","Author");
                        break;
                    case R.id.radioButtonLocation:
                        Log.e("search","Location");
                        break;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




        // query
        // The "base query" is a query with no startAt/endAt/limit clauses that the adapter can use
// to form smaller queries for each page.  It should only include where() and orderBy() clauses
        Query baseQuery = rootRef.collection("books").orderBy("bookTitle", Query.Direction.ASCENDING);
// Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e(TAG, document.getId() + " => " + document.getData());
                                Book book = document.toObject(Book.class);
                                Log.e(TAG,"books "+book.getBookTitle());
                                bookArrayList.add(book);

                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                        Log.e("book size","Size of "+ String.valueOf(bookArrayList.size()));

                    }
                });



         adapter =
                new RecyclerView.Adapter<BookViewHolder>() {

                    // List to store all the contact details
                    private ArrayList<Book> bookList = bookArrayList;

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
                    public void onBindViewHolder(@NonNull BookViewHolder holder,
                                                    int position) {
                        holder.mItem = bookList.get(position);
                        Picasso.get().load(bookList.get(position).getBookImageUrl()).into(holder.mImageBook);
                        holder.mTitle.setText(bookList.get(position).getBookTitle());
                        holder.mAuthor.setText(bookList.get(position).getBookAuthor());
                        holder.mBookRating.setRating(bookList.get(position).getBookRating());
                        holder.mGenre.setText(bookList.get(position).getBookGenre());
                        if(bookList.get(position).isBookAvailable()){
                            holder.mAvailibility.setText("Available");
                            holder.mAvailibility.setTextColor(Color.GREEN);
                        } else{
                            holder.mAvailibility.setText("Not Available");
                            holder.mAvailibility.setTextColor(Color.RED);
                        }
                        holder.mBookOwner.setText(bookList.get(position).getBookOwner());

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

                    @Override
                    public int getItemCount() {
                        return bookList.size();
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
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
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




}
