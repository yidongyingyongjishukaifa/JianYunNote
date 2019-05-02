package com.henu.jianyunnote.Parttion;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.henu.jianyunnote.Index.Login;
import com.henu.jianyunnote.Page.NotePage;
import com.henu.jianyunnote.R;

import java.util.ArrayList;
import java.util.List;

public class NoteParttion extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Parttion> parttionList = new ArrayList<>();
    private ParttionAdapter parttionAdapter;
    private SharedPreferences pref;
    private TextView login_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_parttion);
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Parttion parttion = new Parttion("新建分区");
                parttionList.add(parttion);
                parttionAdapter.notifyDataSetChanged();
                //Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        login_Email = navigationView.inflateHeaderView( R.layout.nav_header_main ).findViewById(R.id.login_email);
        String login_email = pref.getString("login_email", "未登录");
        login_Email.setText(login_email);

//        View view = navigationView.inflateHeaderView( R.layout.nav_header_main );
//        ImageView imageView = view.findViewById( R.id.imageView111 );
//        imageView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //此处可跳转用户设置
//            }
//        } );

        initParttion();
        parttionAdapter = new ParttionAdapter( NoteParttion.this,R.layout.parttion_item, parttionList);
        ListView listView = findViewById( R.id.parttion_listview );
        listView.setAdapter( parttionAdapter );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotePage.ActionStart( NoteParttion.this );
            }
        } );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.toolbar,menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.backup:
                //
                break;
            case R.id.delete:
                //
                break;
            case R.id.setting:
                //
                break;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Login.ActionStart( NoteParttion.this );
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void initParttion() {
        Parttion parttion1 = new Parttion( "笔记本一" );
        parttionList.add( parttion1 );
        Parttion parttion2 = new Parttion( "笔记本二" );
        parttionList.add( parttion2 );
        Parttion parttion3 = new Parttion( "笔记本三" );
        parttionList.add( parttion3 );
    }

}
