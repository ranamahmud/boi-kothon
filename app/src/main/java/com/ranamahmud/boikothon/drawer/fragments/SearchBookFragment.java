package com.ranamahmud.boikothon.drawer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.drawer.fragments.dummy.DummyContent;
import com.ranamahmud.boikothon.drawer.fragments.dummy.DummyContent.DummyItem;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                                .inflate(R.layout.book_item_basic, group, false);

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
        void onListFragmentInteraction(DummyItem item);
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

}
