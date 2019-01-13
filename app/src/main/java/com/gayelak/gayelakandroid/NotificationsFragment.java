package com.gayelak.gayelakandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;



  public class NotificationsFragment extends Fragment {

    ListView listView;


      @Override
      public void onAttach(Context context) {
          super.onAttach(context);


      }

      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        // Inflate the layout for this fragment
        NotificationsAdapter adapter = new NotificationsAdapter(getActivity(), (LottieAnimationView) view.findViewById(R.id.loadingAnimationView));
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        return view;

    }

  }




