package com.malicia.mrg.utils.drive;/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.malicia.mrg.R;
import com.malicia.mrg.Utils;

import java.io.IOException;

import static com.malicia.mrg.utils.drive.DrivePopUp.*;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 *
 * @author Yaniv Inbar
 */
abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {

    final DrivePopUp drivePopUp;
    final Drive service;
    private final EditText progressBar;

    CommonAsyncTask(DrivePopUp drivePopUp) {
        this.drivePopUp = drivePopUp;
        service = drivePopUp.service;
        progressBar = drivePopUp.getView().findViewById(R.id.driveText);
        switch (drivePopUp.getAction()) {
            case SAVE:
                progressBar.setText(R.string.drive_text_save);
                break;
            case LOAD:
                progressBar.setText(R.string.drive_text_load);
                break;
            case DELETE:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + drivePopUp.getAction());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        drivePopUp.numAsyncDrives++;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected final Boolean doInBackground(Void... ignored) {
        try {
            doInBackground();
            return true;
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            drivePopUp.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            drivePopUp.startActivityForResult(
                    userRecoverableException.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException e) {
            Utils.logAndShow(drivePopUp.getActivity(), TAG, e);
        }
        return false;
    }

    @Override
    protected final void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (0 == --drivePopUp.numAsyncDrives) {
            progressBar.setVisibility(View.GONE);
            drivePopUp.dismiss();
        }
        if (success) {
            drivePopUp.refreshView();
        }
    }

    abstract protected void doInBackground() throws IOException;
}