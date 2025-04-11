package com.example.techpatashala;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewUploadActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    EditText edtTitle;
    TextView txtFileSelected;
    Button btnSelectFile, btnUpload;
    Uri fileUri;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_upload);
        edtTitle = findViewById(R.id.edtTitle);
        txtFileSelected = findViewById(R.id.txtFileSelected);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnUpload = findViewById(R.id.btnUpload);
        storageReference = FirebaseStorage.getInstance().getReference("upload");
        databaseReference = FirebaseDatabase.getInstance("https://techpatashala-d6821-default-rtdb.firebaseio.com/").getReference("upload");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
        });
        btnUpload.setOnClickListener(v -> {
            if (fileUri != null && !edtTitle.getText().toString().isEmpty()) {
                uploadFileToFirebase();
            } else {
                Toast.makeText(NewUploadActivity.this, "Please select a file and enter a title", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            txtFileSelected.setText("File Selected: " + fileUri.getLastPathSegment());
        }
    }
    private void uploadFileToFirebase() {
        progressDialog.show();

        String title = edtTitle.getText().toString().trim();
        String fileName = title + "_" + System.currentTimeMillis() + ".pdf";
        StorageReference fileReference = storageReference.child(fileName);
        fileReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveFileToDatabase(title, "pdf", uri.toString());
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewUploadActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void saveFileToDatabase(String title, String type, String fileUrl) {
        String uploadId = databaseReference.push().getKey();
        Upload upload = new Upload(title, type, fileUrl);
        assert uploadId != null;
        databaseReference.child(uploadId).setValue(upload)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewUploadActivity.this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                    resetUI();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewUploadActivity.this, "Failed to save file data", Toast.LENGTH_SHORT).show();
                });
    }
    private void resetUI() {
        edtTitle.setText("");
        txtFileSelected.setText("No file selected");
        fileUri = null;
    }
}
/*package com.example.techpatashala;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewUploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private TextView txtFileSelected;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_upload);

        EditText edtTitle = findViewById(R.id.edt_title);
        //Spinner spinnerType = findViewById(R.id.spinner_type);
        txtFileSelected = findViewById(R.id.txt_file_selected);
        Button btnSelectFile = findViewById(R.id.btn_select_file);
        Button btnUpload = findViewById(R.id.btn_upload);

        // Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("upload");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Set up Spinner options
        /*String[] fileTypes = {"Document", "Audio", "Video"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fileTypes);
        spinnerType.setAdapter(adapter);

        // Open file picker
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf"); // Allow PDFs only, change if needed
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        });

        // Handle upload
        btnUpload.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            //String type = spinnerType.getSelectedItem().toString();

            if (fileUri != null && !title.isEmpty()) {
                uploadFile(title, "Document", fileUri);
            } else {
                txtFileSelected.setText("Please select a file and enter a title.");
            }
        });
    }

    // Handle file selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            txtFileSelected.setText("File Selected: " + fileUri.getLastPathSegment());
        }
    }

    // Upload file to Firebase Storage
    // MODIFIED uploadFile() method
    private void uploadFile(String title, String type, Uri fileUri) {
        progressDialog.show();

        // 1. Add metadata with PDF content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/pdf")
                .build();

        String fileName = System.currentTimeMillis() + ".pdf";
        StorageReference fileRef = storageReference.child(fileName);

        // 2. Include metadata in upload
        fileRef.putFile(fileUri, metadata)
                .addOnSuccessListener(taskSnapshot -> {
                    // 3. Get download URL from task snapshot's reference
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                saveToDatabase(title, type, uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Log.e("URL_Fetch", "Failed: " + e.getMessage()); // Enhanced logging
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Save metadata to Firebase Realtime Database
    private void saveToDatabase(String title, String type, String fileUrl) {
        String uploadId = FirebaseDatabase.getInstance().getReference("upload").push().getKey();
        Upload upload = new Upload(title, type, fileUrl);

        FirebaseDatabase.getInstance().getReference("upload").child(uploadId).setValue(upload)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewUploadActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewUploadActivity.this, UserPageActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewUploadActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                });
    }
}*/
