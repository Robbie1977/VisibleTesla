/*
 * CommandIssuer.java - Copyright(c) 2014 Joe Pasqua
 * Provided under the MIT License. See the LICENSE file for details.
 * Created: Aug 8, 2014
 */
package org.noroomattheinn.visibletesla;

import java.util.concurrent.Callable;
import javafx.scene.control.ProgressIndicator;
import org.noroomattheinn.tesla.Result;
import org.noroomattheinn.utils.Executor;

import static org.noroomattheinn.tesla.Tesla.logger;

/**
 * CommandIssuer: Execute commands in the background.
 *
 * @author Joe Pasqua <joe at NoRoomAtTheInn dot org>
 */
class CommandIssuer extends Executor<CommandIssuer.Request> {
    
/*==============================================================================
 * -------                                                               -------
 * -------              Public Interface To This Class                   ------- 
 * -------                                                               -------
 *============================================================================*/
    
    CommandIssuer(ProgressListener progressListener) {
        super("CommandIssuer", progressListener);
    }
    
    void issueCommand(Callable<Result> command, boolean retry,
                             ProgressIndicator pi, String name) {
        super.produce(new Request(command, retry, pi, name));
    }
    
/*------------------------------------------------------------------------------
 *
 * Internal Methods - Some declared protected since they implement interfaces
 * 
 *----------------------------------------------------------------------------*/

    @Override protected boolean execRequest(Request r) throws Exception {
        Result result = r.command.call();
        if (result.success) { return true; }
        logger.warning("Failed command (" + r.command + "): " + result.explanation);
        return false;
    }
    
    static class Request extends Executor.Request {
        private final Callable<Result> command;
        private final String name;
        private final boolean retry;
        
        Request(Callable<Result> command, boolean retry, ProgressIndicator pi, String name) {
            super(pi);
            this.command = command;
            this.retry = retry;
            this.name = name;
        }
        
        @Override protected String getRequestName() { return name; }
        @Override protected int maxRetries() { return retry ? 2 : 0; }
    }
}