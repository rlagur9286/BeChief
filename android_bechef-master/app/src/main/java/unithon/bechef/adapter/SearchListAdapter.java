package unithon.bechef.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;
import unithon.bechef.R;
import unithon.bechef.util.trans.object.Bechef;

import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bechef> bechefLIst;

    public SearchListAdapter(Context context, ArrayList<Bechef> bechefLIst) {
        this.context = context;
        this.bechefLIst = bechefLIst;

    }

    @Override
    public int getCount() {
        return bechefLIst.size();
    }

    @Override
    public Object getItem(int i) {

        return bechefLIst.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_search_result_list_item, viewGroup, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.search_result_bg_img);
        TextView textView = (TextView) view.findViewById(R.id.search_result_food_name);
        String imgUrl = null;
        String text = null;

        try {
            imgUrl = bechefLIst.get(i).getImg();
            text = bechefLIst.get(i).getTitle();
        } catch (Exception e) {
        }


        if (imgUrl != null && !imgUrl.equals(""))
            Picasso.with(context).load(imgUrl).into(imageView);
        else
            imageView.setImageResource(R.mipmap.home_01);

        if (text != null)
            textView.setText(text);
        else
            textView.setText("");

        return view;

    }
}
