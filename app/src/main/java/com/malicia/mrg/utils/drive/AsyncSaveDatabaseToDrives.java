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

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.malicia.mrg.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Asynchronously load the Drives.
 *
 * @author Yaniv Inbar
 */
class AsyncSaveDatabaseToDrives extends CommonAsyncTask {
    AsyncSaveDatabaseToDrives(DrivePopUp drivePopUp) {
        super(drivePopUp);
    }

    static void run(DrivePopUp drivePopUp) {
        new AsyncSaveDatabaseToDrives(drivePopUp).execute();
    }

    @Override
    protected void doInBackground() throws IOException {

        File fileMetadata = new File();
        String database = "toDoListDatabase";
        fileMetadata.setName(database);
        fileMetadata.setMimeType("application/vnd.sqlite3");

        java.io.File filePath = new java.io.File("/data/data/com.malicia.mrg/databases/toDoListDatabase");
        FileContent mediaContent = new FileContent("application/vnd.sqlite3", filePath);

        FileList result = service.files().list()
                //                  .setQ("mimeType='"+ fileMetadata.getMimeType() + "'")
                .setQ("name='" + fileMetadata.getName() + "' and trashed = false")
                .setSpaces("drive")
                //                   .setFields("id,title")
                //                   .setPageToken(pageToken)
                .execute();

        List<File> files = result.getFiles();
        if (files != null && files.size() > 0) {

            File file = files.get(0);
            switch (drivePopUp.getAction()) {
                case SAVE:
                    service.files().update(file.getId(), fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();

                    System.out.println("File ID: " + file.getId());
                    break;
                case LOAD:
                    OutputStream outputStream = new FileOutputStream(filePath.getPath());
                    service.files().get(file.getId())
                            .executeMediaAndDownloadTo(outputStream);
//                    try(OutputStream fileOutputStream = new FileOutputStream(filePath.getPath())) {
//                        outputStream.writeTo(fileOutputStream);
//                    }
//
//                    OutputStream outputStream = new ByteArrayOutputStream();
//                    service.files().get(files.get(0).getId())
//                            .executeMediaAndDownloadTo(outputStream);
//                    try(OutputStream fileOutputStream = new FileOutputStream(filePath.getPath())) {
//                        outputStream.writeTo(fileOutputStream);
//                    }
                    break;
                case DELETE:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + drivePopUp.getAction());
            }

        } else {


            switch (drivePopUp.getAction()) {
                case SAVE:
                    File file = service.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                    System.out.println("File ID: " + file.getId());
                    break;
                case LOAD:
                    break;
                case DELETE:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + drivePopUp.getAction());
            }


        }
    }


}