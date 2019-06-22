package com.henu.jianyunnote.activity.noteContent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.jianyunnote.activity.notePage.NotePageActivity;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.model.Bmob.Note_Bmob;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.PdfItextUtil;
import com.itextpdf.text.DocumentException;


import org.litepal.LitePal;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import jp.wasabeef.richeditor.RichEditor;

public class NoteContentActivity extends AppCompatActivity {
    private static int local_note_id;
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
                        bmob_note_id = note.getBmob_note_id();
                        note.save();
                        noteDao_litePal.updateNoteBookByNote(note);
                    }
                }
                Note_Bmob note_bmob = new Note_Bmob();
                note_bmob.setContent(mPreview.getText().toString());
                note_bmob.update(bmob_note_id, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                    }

                });
            }
        };
        mThread.start();
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
                //
                break;
            case R.id.photo:
                //
                break;
            case R.id.pdf:
                Download_PDF();
                break;
            case android.R.id.home:
                setResult();
                break;
        }
        return true;
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
        return "/savedPdf/" + note_title + "_" + local_note_id + ".pdf";
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
