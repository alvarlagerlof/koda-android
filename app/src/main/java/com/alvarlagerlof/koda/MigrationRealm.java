package com.alvarlagerlof.koda;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * Created by alvar on 2017-04-02.
 */

public class MigrationRealm implements io.realm.RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // To add migrate, create a new if statement with the old version


        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();


        /*
        Migrate form version 0 (example)

        public class RequestQueueItem extends RealmObject {
            private String url;
            // getters and setters left out for brevity
        }

        public class ProjectsRealmObject extends RealmObject {
            private String privateID;
            private String publicID;
            private String title;
            private String updatedRealm;
            private String updatedServer;
            private String description;
            private Boolean isPublic;
            private String code;
            private Boolean synced;
            // getters and setters left out for brevity
        }
        */


        if (oldVersion == 0) {
            /*
            schema.create("RequestQueueItem")
                    .addField("url", String.class);

             schema.create("ProjectsRealmObject")
                    .addField("privateID", String.class);
                    ...
            */

            oldVersion++;
        }


        if (oldVersion == 1) {
            /*
            schema.get("RequestQueueItem")
                    .addField("foo", String.class);
            */

            oldVersion++;
        }

    }


    @Override
    public int hashCode() {
        return 37;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MigrationRealm);
    }
}
