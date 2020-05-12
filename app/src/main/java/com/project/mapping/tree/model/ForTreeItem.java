package com.project.mapping.tree.model;

import java.io.Serializable;

public interface ForTreeItem<T extends NodeModel<?>> extends Serializable {
    void next(int msg, T next);
}
