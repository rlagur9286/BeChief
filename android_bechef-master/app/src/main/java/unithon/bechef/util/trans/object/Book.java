package unithon.bechef.util.trans.object;

import lombok.Data;

import java.io.Serializable;

@Data
public class Book implements Serializable {
    private String kind;
    private String id;
    private String etag;
    private String selfLink;
    private VolumeInfo volumeInfo;
    private SaleInfo saleInfo;
}
