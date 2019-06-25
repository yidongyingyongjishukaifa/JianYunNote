package com.henu.jianyunnote.activity.noteContent;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.henu.jianyunnote.activity.notePage.NotePageActivity;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.FileUtil;
import com.henu.jianyunnote.util.OCRUtil;
import com.henu.jianyunnote.util.PdfItextUtil;
import com.itextpdf.text.DocumentException;


import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.richeditor.RichEditor;

import static com.henu.jianyunnote.util.JsonUtil.parseJsonToMap;

public class NoteContentActivity extends AppCompatActivity {
    final static String TAG = "OCR";
    private static int local_note_id;
    private static String filePath;
    private static String note_title;
    private RichEditor mEditor;
    private TextView mPreview;
    private int isChange = 0;
    private INoteDao_LitePal noteDao_litePal = new INoteDaoImpl_LitePal();
    private List<Note_LitePal> noteList;
    private Thread mThread;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (noteList != null && noteList.size() != 0) {
                        for (Note_LitePal note : noteList) {
                            note_title = note.getTitle();
                            if (note.getContent() != null) {
                                mEditor.setHtml(note.getContent());
                                mPreview.setText(mEditor.getHtml());
                            }
                        }
                    }
                    break;
                default:
                    //do something
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        AtyUtil.getInstance().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
                Log.e(TAG, result.toString());
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                Log.e(TAG, error.toString());
            }
        }, getApplicationContext());
        mEditor = findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.RED);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");
        isChange = 0;
        mPreview = findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mPreview.setText(text);
                isChange = 1;
                saveNote();
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
                v.setBackgroundColor(Color.parseColor("#3399ff"));
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });

        initNoteContent();
    }

    private void initNoteContent() {
        mThread = new Thread() {
            @Override
            public void run() {
                local_note_id = Integer.parseInt(NoteContentActivity.this.getIntent().getStringExtra("note_id"));
                String noteid = String.valueOf((local_note_id));
                noteList = LitePal.where("id = ?", noteid).find(Note_LitePal.class);
                handler.sendEmptyMessage(0);
            }
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNote();
        setResult();
    }

    private void saveNote() {
        mThread = new Thread() {
            @Override
            public void run() {
                String noteid = String.valueOf((local_note_id));
                String bmob_note_id = "";
                List<Note_LitePal> noteList = LitePal.where("id = ?", noteid).find(Note_LitePal.class);
                if (noteList != null && noteList.size() != 0) {
                    for (Note_LitePal note : noteList) {
                        if (isChange == 1) {
                            note.setIsChange(1);
                            note.setUpdateTime(new Date());
                        }
                        note.setContent(mPreview.getText().toString());
                        if(note.getBmob_note_id()!=null){
                            bmob_note_id = note.getBmob_note_id();
                        }
                        note.save();
                        noteDao_litePal.updateNoteBookByNote(note);
                    }
                }
                if(!"".equals(bmob_note_id)){
                    Note_Bmob note_bmob = new Note_Bmob();
                    note_bmob.setContent(mPreview.getText().toString());
                    note_bmob.update(bmob_note_id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }

                    });
                }
            }
        };
        mThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_forcontent, menu);
        return true;
    }

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.syns:
                //
                break;
            case R.id.photo:
                mEditor.focusEditor();
                ActivityCompat.requestPermissions(NoteContentActivity.this, mPermissionList, 101);
                break;
            case R.id.up_pic:
                mEditor.focusEditor();
                ActivityCompat.requestPermissions(NoteContentActivity.this, mPermissionList, 100);
                break;
            case R.id.pdf:
                mThread = new Thread() {
                    @Override
                    public void run() {
                        Download_PDF();
                        View view = findViewById(R.id.sack);
                        Snackbar.make(view, "PDF下载完成", Snackbar.LENGTH_LONG).setAction("打开", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //点击右侧的按钮之后的操作
                                openFile();
                            }
                        }).show();
                    }
                };
                mThread.start();
                break;
            case android.R.id.home:
                setResult();
                break;
            case R.id.ocr:
                Intent intent = new Intent(NoteContentActivity.this, CameraActivity.class);

                // 设置临时存储
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getApplication()).getAbsolutePath());

                // 调用除银行卡，身份证等识别的activity
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);

                startActivityForResult(intent, 111);
                break;
        }
        return true;
    }

    private void openFile() {
        Intent intent = new Intent();
        File file = new File(filePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
        intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));//设置类型
        startActivity(intent);
    }

    private String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
                    getImagealbum();
                } else {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 101:
                boolean photo1 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean photo2 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && photo1 && photo2) {
                    getImagephoto();
                } else {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getImagealbum() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    100);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }

    Uri photoUri;

    private void getImagephoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data != null) {
                        String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                        mEditor.insertImage(realPathFromUri, realPathFromUri + "\" style=\"max-width:100%");
                    } else {
                        Toast.makeText(this, "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    String filePath = pathFromPhoto();
                    mEditor.insertImage(filePath, filePath + "\" style=\"max-width:100%");
                    break;
            }
        }
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            // 获取调用参数
            String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
            // 通过临时文件获取拍摄的图片
            String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();

            OCRUtil.recognizeAccurateBasic(this, filePath, new OCRUtil.OCRCallBack<GeneralResult>() {
                @Override
                public void succeed(GeneralResult data) {
                    // 调用成功，返回GeneralResult对象
                    String content = OCRUtil.getResult(data);
                    HashMap<String, Object> stringObjectHashMap = parseJsonToMap(content);
                    for (Map.Entry<String, Object> entry : stringObjectHashMap.entrySet()) {
                        if (entry.getKey().equals("words_result")) {
                            String[] strings = entry.getValue().toString().split(",");
                            int count = strings.length;
                            StringBuilder re = new StringBuilder();
                            for (int i = 0; i < count; i++) {
                                if (i == count - 1) {
                                    String s = strings[i].split("words=")[1];
                                    re.append(s.substring(0, s.length() - 2));
                                    re.append("\n");
                                } else {
                                    String s = strings[i].split("words=")[1];
                                    re.append(s.substring(0, s.length() - 1));
                                    re.append("\n");
                                }
                            }
                            mEditor.setHtml(mPreview.getText().toString() + "\n" + re);
                            mPreview.setText(mEditor.getHtml());
                        }
                    }

                    Log.e(TAG, content + "");
                }

                @Override
                public void failed(OCRError error) {
                    // 调用失败，返回OCRError对象
                    Log.e(TAG, "错误信息：" + error.getMessage());
                }
            });
        }
    }

    private String pathFromPhoto() {
        String picPath = null;
        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            try {
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (NumberFormatException e) {

                Log.v("qin", "error:" + e);
            }
        }
        return picPath;
    }

    private void Download_PDF() {
        PdfItextUtil pdfItextUtil = null;
        try {
            pdfItextUtil = new PdfItextUtil(getSavePdfFilePath())
                    .addTitleToPdf(getTvString(note_title))
                    .addTextToPdf(getTvString(mEditor.getHtml()))
            ;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (pdfItextUtil != null)
                pdfItextUtil.close();
        }
    }

    private String getSavePdfFilePath() {
        String filename = note_title + "_" + local_note_id + ".pdf";
        File extDir = Environment.getExternalStorageDirectory();
        File fullFilename = new File(extDir, filename);
        try {
            fullFilename.createNewFile();
            filePath = fullFilename.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fullFilename.setWritable(Boolean.TRUE);
        return fullFilename.toString();
    }

    private String getTvString(String s) {
        return s;
    }

    private void setResult() {
        Intent result = new Intent(NoteContentActivity.this, NotePageActivity.class);
        result.putExtra("note_id", local_note_id);
        if (isChange == 1) {
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED, result);
        }
        startActivity(result);
        finish();
    }
}
