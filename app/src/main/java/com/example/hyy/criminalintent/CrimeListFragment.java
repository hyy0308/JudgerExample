package com.example.hyy.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//chapter 9 challenge
//public class CrimeListFragment extends Fragment {
//    private RecyclerView mCrimeRecyclerView;
//    private CrimeAdapter mAdapter;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
//        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
//        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        updateUI();
//        return view;
//    }
//    private void updateUI() {
//        CrimeLab crimeLab = CrimeLab.get(getActivity());
//        List<Crime> crimes = crimeLab.getCrimes();
//        mAdapter = new CrimeAdapter(crimes);
//        mCrimeRecyclerView.setAdapter(mAdapter);
//    }
//    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        private TextView mTitleTextView;
//        private TextView mDateTextView;
//        private Crime mCrime;
//        private ImageView mSolvedImageView;
//        public CrimeHolder(View itemView) {
//            super(itemView);
//            itemView.setOnClickListener(this);
//            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
//            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
//            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
//        }
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(getActivity(), mCrime.getTitle() + " klik!", Toast.LENGTH_SHORT).show();
//        }
//
//        public void bind(Crime crime) {
//            mCrime = crime;
//            mTitleTextView.setText(mCrime.getTitle());
//            mDateTextView.setText(mCrime.getDate().toString());
//            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
//            if (this.getItemViewType() == 1) {
//                Button b = (Button) itemView.findViewById(R.id.police_button);
//                b.setOnClickListener(e -> {
//                    Toast.makeText(getActivity(), R.string.make_a_call_to_police,
//                            Toast.LENGTH_SHORT).show();
//                });
//            }
//        }
//    }
//    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
//        private List<Crime> mCrimes;
//        public CrimeAdapter(List<Crime> crimes) {
//
//            mCrimes = crimes;
//        }
//        @Override
//        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            int layout = 0;
//            switch(viewType) {
//                case 0:
//                    layout = R.layout.list_item_crime;
//                    break;
//                case 1:
//                    layout = R.layout.list_item_crime_police;
//                    break;
//            }
//
//            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//            return new CrimeHolder(view);
//        }
//        @Override
//        public void onBindViewHolder(CrimeHolder holder, int position) {
//            Crime crime = mCrimes.get(position);
//            holder.bind(crime);
//        }
//        @Override
//        public int getItemCount() {
//
//            return mCrimes.size();
//        }
//        @Override
//        public int getItemViewType(int position) {
//            boolean isPoliceNeeded = mCrimes.get(position).isPoliceRequired();
//            if (isPoliceNeeded) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }
//    }
//}
    public class CrimeListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mLastClickedCrimePosition = -1;
    private boolean mSubtitleVisible;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastClickedCrimePosition < 0) {
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyItemChanged(mLastClickedCrimePosition);
                mLastClickedCrimePosition = -1;
            }
        }
        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        @Override
        public void onClick(View v) {
            mLastClickedCrimePosition = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            Date date = mCrime.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE', 'dd' 'MMM' 'yyyy");
            String stringDate = dateFormat.format(date);
            mDateTextView.setText(stringDate);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}