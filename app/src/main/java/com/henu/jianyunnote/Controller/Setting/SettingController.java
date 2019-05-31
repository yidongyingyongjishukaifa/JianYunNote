package com.henu.jianyunnote.Controller.Setting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.jianyunnote.R;
import com.henu.jianyunnote.Util.AtyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SettingController extends AppCompatActivity {

    public static final int TAKE_PHONE = 1;
    public static final int CHOOSE_PHOTO = 2;
    
    private Uri uri;
    //private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting );
        // 添加Activity到堆栈
        AtyUtil.getInstance().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        TextView about = findViewById( R.id.info );
        about.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(SettingController.this, About_Setting.class);
                startActivity( intent );
            }
        } );

//        ImageView imageView = (ImageView) findViewById( R.id.heade );
        Button button = findViewById( R.id.take_ );
        Button button1 = findViewById( R.id.chose_ );

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建文件用于保存即将拍摄出的图片
                File outputImage = new File( getExternalCacheDir(),
                        "outputImage.jpg");
                try{
                    if(outputImage.exists())    //如果已存在就删除
                        outputImage.delete();   //避免重复拍照
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){   //SDK版本>24需要使用provider
                    uri = FileProvider.getUriForFile( SettingController.this,
                            "com.henu.jianyunnote.fileprovider",
                            outputImage);
                }
                else uri = Uri.fromFile( outputImage );  //sdk版本低则不需要
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");   //使用Intent打开相机
                intent.putExtra( MediaStore.EXTRA_OUTPUT,uri );
                startActivityForResult( intent,TAKE_PHONE );    //拍摄完照片还要将其显示
            }
        } );

        button1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //首先检查访问权限，如果没有权限就提示用户allow一下
                if(ContextCompat.checkSelfPermission( SettingController.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions( SettingController.this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                //允许后打开相册
                else openAlbum();
            }
        } );
    }

    private void openAlbum() {
        //打开相册的方法，同样是使用的Intent
        Intent intent = new Intent( "android.intent.action.GET_CONTENT" );
        intent.setType( "image/*" );  //打开的是图片
        startActivityForResult( intent, CHOOSE_PHOTO);
    }

    //请求用户访问权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //我们使用startActivityForResult来获得拍摄完的照片，因此要重写此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHONE:    //开始声明的全局变量，用于区分直接拍照或是从相册选取
                if(resultCode == RESULT_OK){
                    try {
                        //通过url获取图片内容并转为Bitmap格式（用于显示图片）
                        Bitmap bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( uri ) );
                        ImageView imageView = findViewById( R.id.heade );
                        imageView.setImageBitmap( bitmap );   //将图片显示到ImageView上
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){    //在sdk版本大于19时采用这种方法获取图片
                        handleImageOnKitKat(data);
                    }
                    else handleImageBeforeKitKat( data );   //低版本则使用此方法（这两个都需要自己创建）
                }
                break;
            default:
                break;
        }
    }

    //4.4版本之前可以直接获取uri
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath( uri, null );
        displayImage(imagePath);
    }

    //将图片显示到imageview控件上
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile( imagePath );
            ImageView imageView = findViewById( R.id.heade );
            imageView.setImageBitmap( bitmap );
        }
        else Toast.makeText( this, "faild to get image", Toast.LENGTH_SHORT ).show();
    }

    //4.4之后的版本，需要对uri进行处理
    @TargetApi( 19 )
    private void handleImageOnKitKat(Intent data) {
        String imagepath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri( this, uri )){
            String docId = DocumentsContract.getDocumentId( uri );
            //document类型处理方法-media文件
            if("com.android.providers.media.documents".equals( uri.getAuthority() )){
                String id = docId.split( ":" )[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagepath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            //downloads文件
            else if("com.android.providers.downloads.documents".equals( uri.getAuthority() )){
                Uri contentUri = ContentUris.withAppendedId( Uri.parse( "content://downloads/public_downloads" ), Long.valueOf( docId ) );
                imagepath = getImagePath(contentUri, null);
            }
        }
        //content类型处理方法
        else if("content".equalsIgnoreCase( uri.getScheme() )){
            imagepath = getImagePath(uri, null);
        }
        //file类型处理方法
        else if("file".equalsIgnoreCase( uri.getScheme() )){
            imagepath = uri.getPath();
        }
        //将图片显示
        displayImage( imagepath );
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return true;
        }
    }
}
