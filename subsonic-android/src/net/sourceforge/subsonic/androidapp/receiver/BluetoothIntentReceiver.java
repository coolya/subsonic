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
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * Request media button focus when connected to Bluetooth A2DP.
 *
 * @author Sindre Mehus
 */
public class BluetoothIntentReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("android.bluetooth.a2dp.extra.SINK_STATE", -1);
        boolean connected = state == 2;  // android.bluetooth.BluetoothA2dp.STATE_CONNECTED
        if (connected) {
            Log.i(TAG, "Connected to Bluetooth A2DP, requesting media button focus.");
            Util.registerMediaButtonEventReceiver(context);
        }

        boolean disconnecting = state == 3; // android.bluetooth.BluetoothA2dp.STATE_DISCONNECTING
        if (disconnecting) {
            Log.i(TAG, "Disconnecting from Bluetooth A2DP, requesting pause.");
            Intent pauseIntent = new Intent();
            pauseIntent.putExtra("command", DownloadServiceImpl.CMD_PAUSE);
            context.sendBroadcast(pauseIntent);
        }
    }
}