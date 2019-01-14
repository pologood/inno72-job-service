package com.inno72.job.executer.common.util;

import java.io.IOException;

public class UrlUtil {

    //    private final static String gameUrlTest="http://api.game.36solo.com/";
    private final static String gameUrlTest="http://localhost:10080/";
    private final static String gameUrlStage="https://api.game.32solo.com/";
    private final static String gameUrlProd="https://api.game.inno72.com/";

    public static String getGameServerUrl(String env) throws IOException {
        switch(env) {

            case "test": return gameUrlTest;
            case "stage": return gameUrlStage;
            case "prod": return gameUrlProd;
            default:
                throw new IOException("未找到gameurl:" + env);

        }
    }
}
