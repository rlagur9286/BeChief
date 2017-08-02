package unithon.bechef.util.trans.object;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Bechef implements Serializable {
    private String url;
    private String title;
    private String img;
}
