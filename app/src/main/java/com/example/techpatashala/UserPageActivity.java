package com.example.techpatashala;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class UserPageActivity extends AppCompatActivity {

    private LinearLayout dynamicContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Get username from intent
        String username = getIntent().getStringExtra("username");
        TextView txtUsername = findViewById(R.id.txt_username);
        if (username != null) {
            txtUsername.setText(username);
        } else {
            txtUsername.setText("Alice"); // Default username
        }

        dynamicContentLayout = findViewById(R.id.dynamic_content_layout);

        // Load static content
        loadDummyContent();

        // Handle button click to navigate to NewUploadActivity
        ImageView btnNewUpload = findViewById(R.id.imageView);
        btnNewUpload.setOnClickListener(v -> {
            Intent intent = new Intent(UserPageActivity.this, NewUploadActivity.class);
            startActivity(intent);
        });
    }

    private void loadDummyContent() {
        // Dummy PDF
        displayContent("Introduction to Java", "document", "https://www.example.com/sample.pdf");
        displayContent("Android Development Basics", "document", "https://www.example.com/android_basics.pdf");

        // Dummy Video
        displayContent("Introduction to Firebase", "document", "https://www.example.com/sample_video.pdf");
        displayContent("Building a Chat App", "document", "https://www.example.com/chat_app_tutorial.pdf");

        displayContent("Introduction to Kotlin", "document", "https://www.example.com/sample.pdf");
        displayContent("Android Development Advanced", "document", "https://www.example.com/android_basics.pdf");

        // Dummy Video
        displayContent("Introduction to C++", "document", "https://www.example.com/sample_video.pdf");
        displayContent("Building a Chatbot", "document", "https://www.example.com/chat_app_tutorial.pdf");

        displayContent("Introduction to Python", "document", "https://www.example.com/sample.pdf");
        displayContent("Developing basic Websites", "document", "https://www.example.com/android_basics.pdf");

        // Dummy Video
        displayContent("Complete React Documentation", "document", "https://www.example.com/sample_video.pdf");
        displayContent("Key Notes of Web Develpment", "document", "https://www.example.com/chat_app_tutorial.pdf");
    }

    private void displayContent(String title, String fileType, String fileUrl) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setPadding(10, 10, 10, 10);
        dynamicContentLayout.addView(titleView);

        if (fileType.equalsIgnoreCase("document")) {
            TextView docView = new TextView(this);
            docView.setText("Open Document: " + title);
            docView.setTextSize(16);
            docView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            docView.setPadding(10, 10, 10, 10);
            docView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(UserPageActivity.this, "No application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            });
            dynamicContentLayout.addView(docView);
        } else if (fileType.equalsIgnoreCase("video")) {
            LinearLayout videoContainer = new LinearLayout(this);
            videoContainer.setOrientation(LinearLayout.VERTICAL);
            videoContainer.setPadding(10, 10, 10, 10);

            VideoView videoView = new VideoView(this);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    600
            ));
            videoView.setVideoURI(Uri.parse(fileUrl));

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            TextView playButton = new TextView(this);
            playButton.setText("▶ Play Video");
            playButton.setTextSize(16);
            playButton.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            playButton.setPadding(10, 10, 10, 10);
            playButton.setOnClickListener(v -> videoView.start());

            videoContainer.addView(playButton);
            videoContainer.addView(videoView);
            dynamicContentLayout.addView(videoContainer);
        }
    }
}

/*package com.example.techpatashala;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPageActivity extends AppCompatActivity {

    private DatabaseReference uploadRef;
    private LinearLayout dynamicContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        String username = getIntent().getStringExtra("username");
        TextView txtUsername = findViewById(R.id.txt_username);
        if (username != null) {
            txtUsername.setText(username);
        }
        uploadRef = FirebaseDatabase.getInstance().getReference("upload");
        dynamicContentLayout = findViewById(R.id.dynamic_content_layout);
        loadUploadedContent();
        ImageView btnNewUpload = findViewById(R.id.imageView);
        btnNewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPageActivity.this, NewUploadActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loadUploadedContent() {
        uploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dynamicContentLayout.removeAllViews(); // Clear old content
                for (DataSnapshot data : snapshot.getChildren()) {
                    String fileUrl = data.child("fileUrl").getValue(String.class);
                    String title = data.child("title").getValue(String.class);
                    String fileType = data.child("type").getValue(String.class);

                    if (title != null && fileType != null && fileUrl != null ) {
                        displayContent(title, fileType, fileUrl);
                    } else {
                        Log.w("FirebaseData", "Incomplete data for file");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database read failed: " + error.getMessage());
                Toast.makeText(UserPageActivity.this, "Failed to load content", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayContent(String title, String fileType, String fileUrl) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setPadding(10, 10, 10, 10);
        dynamicContentLayout.addView(titleView);
        if (fileType.equalsIgnoreCase("document")) {
            TextView docView = new TextView(this);
            docView.setText("Open Document: " + title);
            docView.setTextSize(16);
            docView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            docView.setPadding(10, 10, 10, 10);
            docView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // Prevents saving history of opened files
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(UserPageActivity.this, "No application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            });
            dynamicContentLayout.addView(docView);
        } else if (fileType.equalsIgnoreCase("video")) {
            // Video container layout
            LinearLayout videoContainer = new LinearLayout(this);
            videoContainer.setOrientation(LinearLayout.VERTICAL);
            videoContainer.setPadding(10, 10, 10, 10);

            // VideoView setup
            VideoView videoView = new VideoView(this);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    600 // Set height for better visibility
            ));
            videoView.setVideoURI(Uri.parse(fileUrl));

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            // Play button
            TextView playButton = new TextView(this);
            playButton.setText("▶ Play Video");
            playButton.setTextSize(16);
            playButton.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            playButton.setPadding(10, 10, 10, 10);
            playButton.setOnClickListener(v -> videoView.start());
            videoContainer.addView(playButton);
            videoContainer.addView(videoView);
            dynamicContentLayout.addView(videoContainer);
        }
    }
}*/