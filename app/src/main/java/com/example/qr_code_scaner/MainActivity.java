package com.example.qr_code_scaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {
    private static final int RC_PERMISSION = 10;
    private CodeScanner mCodeScanner;
   // TextView showtext;
    private boolean mPermissionGranted;
    LottieAnimationView lottieAnimationView,lottieAnimationView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lottieAnimationView=findViewById(R.id.lottie);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView1=findViewById(R.id.lottie1);
        lottieAnimationView1.setRepeatCount(LottieDrawable.INFINITE);


        CodeScannerView scannerView = findViewById(R.id.scanner_v);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                       // showtext.setText(result.getText());
                    //    SpannableString url = SpannableString.valueOf(result.getText());
                     //   Linkify.addLinks(url, Linkify.WEB_URLS);
//                        showtext.setText(url);
//                        showtext.setMovementMethod(LinkMovementMethod.getInstance());

                        if (result.getText().isEmpty()){
                            System.out.println("empty>>>>>>>>>>>>>>>>>>>");
                        }else {
                            System.out.println("not empty>>>>>>>>>>>>>>>>>>>");
                            ImageView cancel;
                            Button shr,copy;
                            TextView show_info;
                            //will create a view of our custom dialog layout
                            View alertCustomdialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_dialog,null);
                            //initialize alert builder.
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setCancelable(false);
                            //set our custom alert dialog to tha alertdialog builder
                            alert.setView(alertCustomdialog);
                            cancel = (ImageView)alertCustomdialog.findViewById(R.id.cancel_button);
                            final AlertDialog dialog = alert.create();
                            //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            //finally show the dialog box in android all

                            show_info = (TextView) alertCustomdialog.findViewById(R.id.show_info);

                            SpannableString url = SpannableString.valueOf(result.getText());
                            Linkify.addLinks(url, Linkify.WEB_URLS);
                            show_info.setText(url);
                            show_info.setMovementMethod(LinkMovementMethod.getInstance());
                           // showtext.setText(url);
                            dialog.show();
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            shr = (Button) alertCustomdialog.findViewById(R.id.share);
                            shr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent shareintent = new Intent();
                                    shareintent.setAction(Intent.ACTION_SEND);
                                    shareintent.putExtra(Intent.EXTRA_TEXT,result.getText());
                                    shareintent.setType("text/plain");

                                    if (shareintent.resolveActivity(getPackageManager()) != null){
                                        startActivity(shareintent);
                                    }
                                }
                            });
                            show_info.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ClipboardManager cm = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                  // cm.setText(textView.getText());
                                    Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                                }
                            });
                            copy = (Button) alertCustomdialog.findViewById(R.id.copy);
                            copy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager cm = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                    // cm.setText(textView.getText());
                                    Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        mCodeScanner.setErrorCallback(error -> runOnUiThread(
                () -> Toast.makeText(this, getString(R.string.scanner_error, error), Toast.LENGTH_LONG).show()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionGranted) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


}
