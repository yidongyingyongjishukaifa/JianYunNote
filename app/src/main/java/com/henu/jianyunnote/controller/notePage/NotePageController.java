package com.henu.jianyunnote.controller.notePage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.henu.jianyunnote.dao.LitePal.INoteBookDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteBookDaoImpl_LitePal;
import com.henu.jianyunnote.model.LitePal.NoteBook_LitePal;
import com.henu.jianyunnote.model.LitePal.Note_LitePal;
import com.henu.jianyunnote.R;
import com.henu.jianyunnote.dao.LitePal.INoteDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.IUserDao_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.INoteDaoImpl_LitePal;
import com.henu.jianyunnote.dao.LitePal.impl.IUserDaoImpl_LitePal;
import com.henu.jianyunnote.util.ArrayUtil;
import com.henu.jianyunnote.util.AtyUtil;
import com.henu.jianyunnote.util.NoteAdapter;
import com.henu.jianyunnote.util.TimeUtil;
import com.henu.jianyunnote.controller.noteContent.NoteContentController;
import com.henu.jianyunnote.controller.noteParttion.NoteParttionController;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.henu.jianyunnote.controller.noteParttion.NoteParttionController.local_notebooks_id;

public class NotePageController extends AppCompatActivity {
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static int[] local_notes_id;
    private int local_notebook_id;
    private NoteAdapter myAdapter;
    private String notebookid;
    public static boolean flag = false;
    private IUserDao_LitePal userService = new IUserDaoImpl_LitePal();
    private INoteDao_LitePal noteService = new INoteDaoImpl_LitePal();
    private INoteBookDao_LitePal noteBookService = new INoteBookDaoImpl_LitePal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);
        AtyUtil.getInstance().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flag = false;
        int p = Integer.parseInt(NotePageController.this.getIntent().getStringExtra("position"));
        local_notebook_id = NoteParttionController.local_notebooks_id[p];
        notebookid = String.valueOf(local_notebook_id);
        initNotePage();
        final ListView mListView = findViewById(R.id.note_listview);
        final FloatingActionsMenu menu = findViewById(R.id.fab_menu);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = findViewById(R.id.fab_1);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                Intent intent = new Intent(NotePageController.this, NoteContentController.class);
                intent.putExtra("position", position + "");
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menu.collapse();
                final int p = position;
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePageController.this);
                final View myView = layoutInflater.inflate(R.layout.new_notebook, null);
                builder.setTitle("修改笔记标题")
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                flag = true;
                                final EditText Notebook_Name = myView.findViewById(R.id.notebook_Name);
                                String s = "";
                                if (Notebook_Name.getText() != null) {
                                    s = Notebook_Name.getText().toString();
                                }
                                Note_LitePal note_litePal = noteService.updateNoteTitleById(s, local_notes_id[p]);
                                if (note_litePal != null) {
                                    listItems.remove(p);
                                    local_notes_id = ArrayUtil.deleteIdInArray(local_notes_id, p);
                                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                    addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()));
                                    myAdapter.notifyDataSetChanged();
                                }
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
                return true;
            }
        });

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotePageController.this);
                final LayoutInflater layoutInflater = LayoutInflater.from(NotePageController.this);
                final View myView = layoutInflater.inflate(R.layout.new_note, null);
                builder.setTitle("新建笔记")
                        .setIcon(R.drawable.note)
                        .setView(myView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                flag = true;
                                final EditText Note_Title = myView.findViewById(R.id.note_Title);
                                String s = "";
                                if (Note_Title.getText() != null) {
                                    s = Note_Title.getText().toString();
                                }
                                Note_LitePal note_litePal = noteService.insert2Note(s, null, local_notebook_id, NoteParttionController.local_user_id);
                                if (note_litePal != null) {
                                    userService.updateUserByUser(NoteParttionController.current_user);
                                    local_notes_id = ArrayUtil.insert2Array(local_notes_id, note_litePal.getId());
                                    addListItem(note_litePal.getTitle(), TimeUtil.Date2String(note_litePal.getUpdateTime()));
                                    myAdapter.notifyDataSetChanged();
                                }
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void initNotePage() {
        List<Note_LitePal> notes = LitePal.where("noteBookId = ? and isDelete = ?", notebookid, "0").order("updateTime asc").find(Note_LitePal.class);
        if (notes != null && notes.size() != 0) {
            int local_count = 0;
            local_notes_id = new int[notes.size()];
            for (Note_LitePal note : notes) {
                local_notes_id[local_count] = note.getId();
                local_count++;
                addListItem(note.getTitle(), TimeUtil.Date2String(note.getUpdateTime()));
            }
        }
        myAdapter = new NoteAdapter(NotePageController.this, listItems);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag) {
            noteBookService.updateNoteBookById(notebookid);
            userService.updateUserByUser(NoteParttionController.current_user);
            Intent result = new Intent(NotePageController.this, NoteParttionController.class);
            setResult(RESULT_OK, result);
            startActivity(result);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //查询逻辑，确定
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //查询逻辑，查询中
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(NotePageController.this, "work", Toast.LENGTH_SHORT).show();
                break;
            case R.id.syns:
                //
                break;
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private void addListItem(String NOTE_MESSAGE, Object NOTE_UPDATE_TIME) {
        Map<String, Object> listItem = new HashMap<>();////创建一个键值对的Map集合，用来存笔记描述和更新时间
        listItem.put("NOTE_MESSAGE", NOTE_MESSAGE);
        listItem.put("NOTE_UPDATE_TIME", NOTE_UPDATE_TIME);
        listItems.add(0, listItem);
    }
}
