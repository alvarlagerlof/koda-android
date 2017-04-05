package com.alvarlagerlof.koda;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by alvar on 2017-04-02.
 */

public class MyMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {


        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // Migrate to version 1: Add a new class.
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     private int age;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 0) {
            schema.get("ProjectsRealmObject")
                    .addField("hoho", String.class)
                    .addField("hoha", String.class);
            oldVersion++;
        }

    }
}
