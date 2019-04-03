package com.ranamahmud.boikothon.drawer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.model.Book;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageRequests.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageRequests extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter<Book, ManageRequests.BookViewHolder> adapter;

    public ManageRequests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageRequests.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageRequests newInstance(String param1, String param2) {
        ManageRequests fragment = new ManageRequests();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_manage_requests, container, false);

        //1 get the referece of recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewManageRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 2. reference to database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("books");
//                .orderBy("bookTitle", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Book, ManageRequests.BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ManageRequests.BookViewHolder holder, int position, @NonNull Book book) {
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
            @Override
            public BookViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.book_item_basic, group, false);

                return new BookViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
