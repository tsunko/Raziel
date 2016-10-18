package ai.seitok.raziel;

import java.awt.geom.AffineTransform;

public class NoCloneAffineTransform extends AffineTransform {

    // do NOT clone the object at all pls
    @Override
    public AffineTransform clone(){
        return this;
    }

}
