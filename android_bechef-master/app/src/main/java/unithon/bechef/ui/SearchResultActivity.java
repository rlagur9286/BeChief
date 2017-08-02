package unithon.bechef.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import unithon.bechef.R;
import unithon.bechef.adapter.SearchListAdapter;
import unithon.bechef.util.trans.object.Bechef;
import unithon.bechef.util.trans.object.BechefLIst;

public class SearchResultActivity extends AppCompatActivity {
    private BechefLIst bechefLIst;
    private ListView listView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        bechefLIst = (BechefLIst) intent.getSerializableExtra("datas");

        listView = (ListView) findViewById(R.id.listview_search_result);
        listView.setAdapter(new SearchListAdapter(getApplicationContext(), bechefLIst.getResult().getResult_set()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("url", ((Bechef) adapterView.getAdapter().getItem(i)).getUrl());
                intent.putExtra("title", ((Bechef) adapterView.getAdapter().getItem(i)).getTitle());
                intent.putExtra("img", ((Bechef) adapterView.getAdapter().getItem(i)).getImg());


                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }
}
