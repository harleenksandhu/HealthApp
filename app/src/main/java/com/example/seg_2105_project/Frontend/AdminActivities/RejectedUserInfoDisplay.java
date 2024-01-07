package com.example.seg_2105_project.Frontend.AdminActivities;

import com.example.seg_2105_project.Backend.Status;
import com.example.seg_2105_project.Backend.User;
import com.example.seg_2105_project.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RejectedUserInfoDisplay extends AppCompatActivity {

    Button backButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_user_info_display);

        //initialize the back button
        backButton2 = (Button) findViewById(R.id.backButton2);

        Intent intent = getIntent();
        User user = (User) getIntent().getSerializableExtra("User");

        //call display method to get the stored information of selected rejected user

        TextView userInfo = (TextView) findViewById(R.id.infoDisplayText2);
        userInfo.setText(user.display());

    }


    /**Method is called when the back button is clicked
     *Returns admin to the rejected registrations inbox.
     */
    public void onClickBackButton(View view) {
        startActivity(new Intent(getApplicationContext(), RejectedRegistrations.class));
    }


    /**
     * Method is called when request accept button is clicked
     */
    public void onClickAccept(View view) {
        User user = (User) getIntent().getSerializableExtra("User");
        user.updateRegistrationStatus(Status.APPROVED);
        sendNotification(user);
        startActivity(new Intent(getApplicationContext(), RejectedRegistrations.class));
    }

    /**
     * Method sends a notification to the users screen indicating status of registration
     * Note: the users notifications for the app must be enabled for this to work
     */
    public void sendNotification(User user) {
        String content = "Congratulations! Your registration request has been approved";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "status notification")
                .setContentTitle("HAMS profile status")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background); // Set your notification icon

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(1, builder.build());

    }
}