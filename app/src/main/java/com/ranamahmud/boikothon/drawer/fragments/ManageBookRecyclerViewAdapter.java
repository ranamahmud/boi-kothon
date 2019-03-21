package com.ranamahmud.boikothon.drawer.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranamahmud.boikothon.R;
import com.ranamahmud.boikothon.drawer.fragments.ManageBookFragment.OnListFragmentInteractionListener;
import com.ranamahmud.boikothon.model.Book;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Book} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ManageBookRecyclerViewAdapter extends RecyclerView.Adapter<ManageBookRecyclerViewAdapter.ViewHolder> {

    private final List<Book> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ManageBookRecyclerViewAdapter(List<Book> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_managebook, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mImageBook.setImageBitmap(mValues.get(position).getBookImageUrl());
        holder.mTitle.setText(mValues.get(position).getBookTitle());
        holder.mAuthor.setText(mValues.get(position).getBookAuthor());
        holder.mBookRating.setRating(mValues.get(position).getBookRating());
        holder.mGenre.setText(mValues.get(position).getBookGenre());
        if(mValues.get(position).isBookAvailable()){
            holder.mAvailibility.setText("Available");
            holder.mAvailibility.setTextColor(Color.GREEN);
        } else{
            holder.mAvailibility.setText("Not Available");
            holder.mAvailibility.setTextColor(Color.RED);
        }
        holder.mBookOwner.setText(mValues.get(position).getBookOwner());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageBook;
        public final TextView mTitle;
        public final TextView mAuthor;
        public final RatingBar mBookRating;
        public final TextView mGenre;
        public final TextView mAvailibility;
        public final TextView mBookOwner;
        public Book mItem;

        public ViewHolder(View view) {
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
}
