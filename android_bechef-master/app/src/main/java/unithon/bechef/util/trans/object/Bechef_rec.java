package unithon.bechef.util.trans.object;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class Bechef_rec {
    private ArrayList<String> texts;
    private ArrayList<String> imgs;
    private ArrayList<Ingre> ingre;
}
