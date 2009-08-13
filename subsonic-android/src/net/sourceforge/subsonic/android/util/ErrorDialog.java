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
package net.sourceforge.subsonic.android.util;

import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import net.sourceforge.subsonic.android.R;

/**
 * @author Sindre Mehus
 */
public class ErrorDialog extends Dialog {

    public ErrorDialog(final Activity activity, String errorMessage) {
        super(activity);
        setContentView(R.layout.error);
        setTitle("An error occurred");
        setOwnerActivity(activity);
        setCancelable(true);
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                activity.finish();
            }
        });
        TextView text = (TextView) findViewById(R.id.error_message);
        text.setText(errorMessage);
    }
}
