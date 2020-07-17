package com.codefathers.cfkclient;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public abstract class BackAbleController {
    private List<String> backFxmlS;

    protected List<String> backForForward(String thisFxml) {
        ArrayList<String> bax = new ArrayList<>(backFxmlS);
        bax.add(thisFxml);
        return bax;
    }

    protected List<String> backForBackward() {
        ArrayList<String> bax = new ArrayList<>(backFxmlS);
        bax.remove(bax.size() - 1);
        return bax;
    }

    protected String back() {
        return backFxmlS.get(backFxmlS.size() - 1);
    }
}
