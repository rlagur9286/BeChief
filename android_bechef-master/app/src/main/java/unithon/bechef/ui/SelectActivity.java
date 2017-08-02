package unithon.bechef.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import unithon.bechef.R;
import unithon.bechef.util.trans.object.BechefLIst;

public class SelectActivity extends AppCompatActivity {
    private TextView mTextView1;
    private ImageView mButton;
    private ImageView mButton1;
    private BechefLIst bechefLIst;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mTextView1 = (TextView) findViewById(R.id.search02);

        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        bechefLIst = (BechefLIst) intent.getSerializableExtra("datas");

        mTextView1.setText(keyword);

        mButton = (ImageView) findViewById(R.id.search04);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent23 = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent23.putExtra("datas", bechefLIst);
                intent23.putExtra("keyword", keyword);
                startActivity(intent23);
            }
        });

        mButton1 = (ImageView) findViewById(R.id.search05);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent23 = new Intent(getApplicationContext(), MapActivity.class);
                intent23.putExtra("datas", bechefLIst);
                intent23.putExtra("keyword", keyword);
                startActivity(intent23);
            }
        });


    }
}
