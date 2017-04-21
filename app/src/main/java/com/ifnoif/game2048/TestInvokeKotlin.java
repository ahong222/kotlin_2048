package com.ifnoif.game2048;

/**
 * Created by shen on 17/4/21.
 */

public class TestInvokeKotlin {

    public void testInvokeKotlin(){
        try {
            GsonTest.Singlon.testGson();
            GsonTest.Singlon.setName("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
