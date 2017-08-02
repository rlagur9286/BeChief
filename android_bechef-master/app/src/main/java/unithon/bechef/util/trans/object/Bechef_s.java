package unithon.bechef.util.trans.object;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Bechef_s implements Serializable {
    private ArrayList<Bechef> result_set;
}
