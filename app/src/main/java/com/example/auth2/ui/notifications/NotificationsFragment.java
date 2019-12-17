package com.example.auth2.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.auth2.Main2Activity;
import com.example.auth2.MainActivity;
import com.example.auth2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    Button btn2Logout;
    GoogleSignInClient mGoogleClient;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        btn2Logout = root.findViewById(R.id.button_logout2);
        Main2Activity activity = (Main2Activity) getActivity();
        mGoogleClient = activity.getMyData();
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });
        btn2Logout.setOnClickListener(v -> Logout2());
        return root;
    }

    private void Logout2() {
        FirebaseAuth.getInstance().signOut();
        mGoogleClient.signOut();
        Intent intent2 =  new Intent(getActivity(), MainActivity.class);
        startActivity(intent2);
    }
}