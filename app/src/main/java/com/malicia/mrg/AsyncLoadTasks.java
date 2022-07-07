package com.malicia.mrg;/*
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

import com.google.api.services.tasks.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously load the tasks.
 *
 * @author Yaniv Inbar
 */
class AsyncLoadTasks extends CommonAsyncTask {

    AsyncLoadTasks(SplashActivity tasksSample) {
        super(tasksSample);
    }

    @Override
    protected void doInBackground() throws IOException {
        List<String> result = new ArrayList<String>();
        List<Task> tasks =
                client.tasks().list("@default").setFields("items/title").execute().getItems();
        if (tasks != null && tasks.size()>0) {
            for (Task task : tasks) {
                result.add(task.getTitle());
            }
        } else {
            result.add("No tasks.");
        }
        activity.tasksList = result;
    }

    static void run(SplashActivity tasksSample) {
        new AsyncLoadTasks(tasksSample).execute();
    }
}