package org.jenjetsu.com.finalproject.model;

import java.io.Serializable;

public interface Model<ID extends Serializable> {
    public void merge(Model another);
    public String getModelName();
    public ID getModelId();
}
