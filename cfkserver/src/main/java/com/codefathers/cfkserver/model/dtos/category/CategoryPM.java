package com.codefathers.cfkserver.model.dtos.category;

import lombok.Data;

@Data
public class CategoryPM {
    private String name;
    private int indent;
    private int id;

    public CategoryPM(String name, int id, int indent) {
        this.name = name;
        this.id = id;
        this.indent = indent;
    }

    @Override
    public String toString() {
        return "  ".repeat(Math.max(0, indent)) + name;
    }
}
