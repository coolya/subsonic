/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.jmeplayer.screens;

import net.sourceforge.subsonic.jmeplayer.Util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 * @author Sindre Mehus
 */
public abstract class Worker implements Runnable {

    private final WaitScreen waitScreen = new WaitScreen();
    private final Display display;
    private final Displayable currentDisplayable;
    private final Displayable nextDisplayable;
    private boolean cancelled;

    protected Worker(Display display, Displayable nextDisplayable, String message) {
        this.display = display;
        this.nextDisplayable = nextDisplayable;

        currentDisplayable = display.getCurrent();
        waitScreen.setMessage(message);

        waitScreen.addCommand(new Command("Cancel", Command.CANCEL, 0));
        waitScreen.setCommandListener(new CommandListener() {
            public void commandAction(Command command, Displayable displayable) {
                cancel();
            }
        });
    }

    public void start() {
        cancelled = false;
        display.setCurrent(waitScreen);
        new Thread(this).start();
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * <p/>
     * Note: this method is executed in a background thread.
     *
     * @return The computed result.
     * @throws Throwable If unable to compute a result.
     */
    protected abstract Object doInBackground() throws Throwable;

    /**
     * Executed on the <i>Event Dispatch Thread</i> after the {@link #doInBackground}
     * method is finished. The default implementation does nothing. Subclasses may override
     * this method to perform completion actions on the <i>Event Dispatch Thread</i>
     *
     * @param result The result that was computed by {@link #doInBackground}.
     */
    protected void done(Object result) {
    }

    /**
     * Executed on the <i>Event Dispatch Thread</i> if the {@link #doInBackground}
     * method throws an exception. The default implementation does nothing.
     * Subclasses may override this method to perform completion actions on the <i>Event Dispatch Thread</i>
     *
     * @param exception The exception thrown from {@link #doInBackground}.
     */
    private void error(Throwable exception) {
        Util.showError(exception, display, currentDisplayable);
    }

    /**
     * Attempts to interrupt the background computation.
     * The default implementation does nothing.
     */
    protected void interrupt() throws Exception {
    }

    /**
     * Cancels this worker.
     */
    public void cancel() {
        cancelled = true;
        try {
            interrupt();
        } catch (Throwable x) {
            x.printStackTrace();
        } finally {
            display.setCurrent(currentDisplayable);
        }
    }

    public final void run() {
        try {
            Object result = doInBackground();
            if (!cancelled) {
                done(result);
                display.setCurrent(nextDisplayable);
            }
        } catch (Throwable x) {
            if (!cancelled) {
                error(x);
            }
        } finally {
            waitScreen.stop();
        }
    }
}
