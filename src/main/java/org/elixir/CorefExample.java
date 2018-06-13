package org.elixir;

import org.elixir.utils.NLPUtils;

import java.util.ArrayList;

public class CorefExample {
    public static void main(String[] args) throws Exception {
        ArrayList<String> corefs = NLPUtils.getCoref("Lee contends that he can make this showing because he never would " +
                "have accepted a guilty plea had he known the result would be deportation. The Government " +
                "contends that Lee cannot show prejudice from accepting a plea where his only hope at trial" +
                " was that something unexpected and unpredictable might occur that would lead to acquittal." +
                " Anne is a little girl. She goes to school everyday.");

//        for (String s : corefs) {
//            System.out.println(s);
//        }

    }
}
