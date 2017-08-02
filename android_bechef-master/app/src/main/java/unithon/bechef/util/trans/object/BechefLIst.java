package unithon.bechef.util.trans.object;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@Data
public class BechefLIst implements Serializable {
    private boolean success;
    private String keyword;
    private Bechef_s result;
}
