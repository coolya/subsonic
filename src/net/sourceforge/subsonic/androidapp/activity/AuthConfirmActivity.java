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

package net.sourceforge.subsonic.androidapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;
import net.sourceforge.subsonic.u1m.R;

public class AuthConfirmActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        saveCredentials();
    }

    private void saveCredentials() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null &&
                intent.getData().toString().startsWith("x-ubuntuone-music://")) {
            Uri uri = intent.getData();
            String username = uri.getQueryParameter("u");
            String password = uri.getQueryParameter("p");
            if (username != null && password != null) {
                SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.PREFERENCES_KEY_USERNAME + 1, username);
                editor.putString(Constants.PREFERENCES_KEY_PASSWORD + 1, password);
                editor.commit();

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.authconfirm_title)
                        .setMessage(R.string.authconfirm_text)
                        .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent(AuthConfirmActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Util.startActivityWithoutTransition(AuthConfirmActivity.this, intent);
                                finish();
                            }
                        })
                        .show();
            } else {
                finish();
            }

        } else {
            finish();
        }

    }
}