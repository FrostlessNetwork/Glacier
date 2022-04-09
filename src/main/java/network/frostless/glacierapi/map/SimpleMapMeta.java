package network.frostless.glacierapi.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleMapMeta implements MapMeta {

    private String name;
    private long lastModified;
    private int version;

}
