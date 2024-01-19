package org.jenjetsu.com.finalproject.model;

import java.io.Serializable;

public interface Model<ID extends Serializable> {
    
    public String getModelName();
    public ID getModelId();
    public Model patchModel(Model another);
}
