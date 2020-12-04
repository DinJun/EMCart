package student.inti.com.emcart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public class UploadItemActivity extends AppCompatActivity {
    private Button mBtnAddProductItem;
    private ImageView mUploadItemImage;
    private EditText mUploadItemName, mUploadItemDesc, mUploadItemPrice, mUploadItemQuantity;
    private static final int ImageSelectInt = 1;
    private Uri imageUri;
    private String productName, productDesc, productPrice, productQuantity, productImageUrl;
    private StorageReference imageStorageRef;
    private DatabaseReference productUploadRef;
    private UUID productItemUniqueID;
    private ProgressBar mUploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);
        productItemUniqueID = UUID.randomUUID();
        imageStorageRef = FirebaseStorage.getInstance().getReference().child("images");
        productUploadRef = FirebaseDatabase.getInstance().getReference().child("Items");

        mUploadProgressBar = (ProgressBar)findViewById(R.id.uploadProgressBar);
        mBtnAddProductItem = (Button)findViewById(R.id.btnAddProductItem);
        mUploadItemImage = (ImageView)findViewById(R.id.uploadItemImage);
        mUploadItemName = (EditText)findViewById(R.id.uploadItemName);
        mUploadItemDesc = (EditText)findViewById(R.id.uploadItemDesc);
        mUploadItemPrice = (EditText)findViewById(R.id.uploadItemPrice);
        mUploadItemQuantity = (EditText)findViewById(R.id.uploadItemQuantity);

        mUploadItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mBtnAddProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductItemInformation();
            }
        });

    }

    private void validateProductItemInformation() {
        productName = mUploadItemName.getText().toString();
        productDesc = mUploadItemDesc.getText().toString();
        productPrice = mUploadItemPrice.getText().toString();
        productQuantity = mUploadItemQuantity.getText().toString();

        if(imageUri == null){
            Toast.makeText(UploadItemActivity.this, "Product image is not uploaded", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productName)){
            mUploadItemName.setError("Field is required!");
        }
        else if(TextUtils.isEmpty(productDesc)){
            mUploadItemDesc.setError("Field is required!");
        }
        else if(TextUtils.isEmpty(productPrice)){
            mUploadItemPrice.setError("Field is required!");
        }
        else if(TextUtils.isEmpty(productQuantity)){
            mUploadItemQuantity.setError("Field is required!");
        }
        else{
            mUploadProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            final StorageReference filePath = imageStorageRef.child(imageUri.getLastPathSegment() + productItemUniqueID + ".png");
            final UploadTask uploadProductImage = filePath.putFile(imageUri);

            uploadProductImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> getUrlTask = uploadProductImage.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            productImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                productImageUrl = task.getResult().toString();
                                uploadProductDetailsDatabase();
                                startActivity(new Intent(UploadItemActivity.this, MainActivity.class));
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage = e.toString();
                    Toast.makeText(UploadItemActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    mUploadProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }
    }

    private void uploadProductDetailsDatabase(){
        HashMap<String, Object> addProductHashMap = new HashMap<>();
        String itemIDRepeat = productItemUniqueID.toString();
        addProductHashMap.put("itemID", itemIDRepeat);
        addProductHashMap.put("itemName", productName);
        addProductHashMap.put("description", productDesc);
        addProductHashMap.put("price", productPrice);
        addProductHashMap.put("quantity", productQuantity);
        addProductHashMap.put("itemImage", productImageUrl);

        productUploadRef.child(productItemUniqueID.toString()).updateChildren(addProductHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mUploadProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(UploadItemActivity.this, "Product successfully added", Toast.LENGTH_SHORT).show();
                }
                else{
                    mUploadProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(UploadItemActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image from here..."), ImageSelectInt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageSelectInt && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mUploadItemImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}