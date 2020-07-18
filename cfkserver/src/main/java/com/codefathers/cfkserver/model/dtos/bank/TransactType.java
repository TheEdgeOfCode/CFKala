package com.codefathers.cfkserver.model.dtos.bank;

public enum TransactType {
    DEST_YOU {
        @Override
        public String getValue() {
            return "+";
        }
    },
    SOURCE_YOU {
        @Override
        public String getValue() {
            return "-";
        }
    },
    ALL {
        @Override
        public String getValue() {
            return "*";
        }
    };

    public abstract String getValue();
}
