package unithon.bechef.util.trans.object;


import lombok.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Data
public class BookList {
    private String kind;
    private int totalItems;
    private ArrayList<Book> items;

}
