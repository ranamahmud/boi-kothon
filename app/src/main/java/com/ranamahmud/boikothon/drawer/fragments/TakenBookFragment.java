package com.ranamahmud.boikothon.drawer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TakenBookFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter<Book, BookViewHolder> adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TakenBookFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TakenBookFragment newInstance(int columnCount) {
        TakenBookFragment fragment = new TakenBookFragment();
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
        View view = inflater.inflate(R.layout.fragment_takenbook_list, container, false);

        //1 get the referece of recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTakenBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 2. reference to database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("books");
//                .orderBy("bookTitle", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Book, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book book) {
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
            }
            @Override
            public BookViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.fragment_takenbook, group, false);

                return new BookViewHolder(view);
            }
        };
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
