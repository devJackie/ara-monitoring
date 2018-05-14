package com.kthcorp.daisy.ams.executor;

import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 8..
 */
public interface CommonExecutor {

    CompletableFuture<String> executeTask() throws Exception;
}
