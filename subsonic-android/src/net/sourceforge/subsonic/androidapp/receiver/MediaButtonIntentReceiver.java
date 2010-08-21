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

 Copyright 2010 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import static net.sourceforge.subsonic.androidapp.domain.PlayerState.*;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;

/**
 * @author Sindre Mehus
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private static final String TAG = MediaButtonIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        handleKeyEvent((KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT));
//        if (isOrderedBroadcast()) {
//            abortBroadcast();
//        }
    }

    private void handleKeyEvent(KeyEvent event) {
        Log.i(TAG, "Got MEDIA_BUTTON key event: " + event);
        if (event == null || event.getAction() != KeyEvent.ACTION_DOWN || event.getRepeatCount() > 0) {
            return;
        }

        DownloadService service = DownloadServiceImpl.getInstance();
        if (service == null) {
            return;
        }

        PlayerState state = service.getPlayerState();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (state == PAUSED || state == COMPLETED) {
                    service.start();
                } else if (state == STOPPED || state == IDLE) {
                    int current = service.getCurrentPlayingIndex();
                    if (current == -1) {
                        service.play(0);
                    } else {
                        service.play(current);
                    }
                } else if (state == STARTED) {
                    service.pause();
                }

                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                service.previous();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (service.getCurrentPlayingIndex() < service.size() - 1) {
                    service.next();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                service.reset();
                break;
            default:
        }
    }
}
