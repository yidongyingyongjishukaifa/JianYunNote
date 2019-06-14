package com.henu.jianyunnote.controller.noteContent;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.TimeUtil;
import com.henu.jianyunnote.controller.notePage.NotePageController;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;


import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class NoteContentController extends AppCompatActivity {
    private final String IMAGE_TYPE = "image/*";
    private int editTextHeight;
    //private EditText noteContent;
    private MoreResourceEditText noteContent;
    Bitmap bitmap;
    FileOutputStream b;
    MoreResourceEditText met;
    ScrollView scrollView;
    private static final String TAG = "NOTE_CONTENT_DEBUG";
    private Uri fileUri;
    private int local_note_id;
    private EditText noteTitle;
    //private EditText noteContent;
    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        AtyUtil.getInstance().addActivity(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteTitle = findViewById(R.id.note_title);
        noteContent = (MoreResourceEditText) findViewById(R.id.note_content);//自动换行
        timeView = findViewById(R.id.timeView);
        noteContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        noteContent.setGravity(Gravity.TOP);
        noteContent.setSingleLine(false);
        noteContent.setHorizontallyScrolling(false);
        initNoteContent();
    }
    private void initNoteContent() {
        boolean flag = NoteContentController.this.getIntent().getBooleanExtra("is_note", false);
        int p = Integer.parseInt(NoteContentController.this.getIntent().getStringExtra("position"));
        if (flag) {
            local_note_id = NoteParttionController.local_notes_id[p];
        } else {
            local_note_id = NotePageController.local_notes_id[p];
        }
        String noteid = String.valueOf((local_note_id));
        List<Note_LitePal> noteList = LitePal.where("id=?", noteid).find(Note_LitePal.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note_LitePal note : noteList) {
                //noteTitle.setText(note.getTitle());
                //noteContent.setText(note.getContent());
                timeView.setText(TimeUtil.Date2String(note.getUpdateTime()));
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String noteid = String.valueOf((local_note_id));
        List<Note_LitePal> noteList = LitePal.where("id = ?", noteid).find(Note_LitePal.class);
        if (noteList != null && noteList.size() != 0) {
            for (Note_LitePal note : noteList) {
                note.setUpdateTime(new Date());
                note.setContent(noteContent.getText().toString());
                note.setTitle(noteTitle.getText().toString());
                note.save();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_forcontent, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syns:

                break;
            case R.id.photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent,1);
                break;
            case R.id.up_pic:
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType(IMAGE_TYPE);
                startActivityForResult(getAlbum, 2);
                break;
            //case android.R.id.home:
               // finish();
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent=getIntent();
        ContentResolver resolver = getContentResolver();
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case 1:
                    String pathphoto = fileUri.getPath();
                    noteContent.insertBitmap(pathphoto);
                    break;
                case 2:
                    String pathalbum = GalleryUtil.getPath(this, data.getData());
                    noteContent.insertBitmap(pathalbum);
                    break;
                default:
                    break;
            }
        }
    }
    private static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type)    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = null;
        try{
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs())
            {
                // 在SD卡上创建文件夹需要权限：
                // <uses-permission
                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");        }
        else if (type == MEDIA_TYPE_VIDEO)        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");        }
        else        {
            return null;
        }
        return mediaFile;
    }
}
