package com.udemy.sbsapps.wallpaperbrowser.Utils;

import android.support.annotation.NonNull;

import com.udemy.sbsapps.wallpaperbrowser.Models.Photo;

import java.util.List;

import io.realm.Realm;

public class RealmController {
    private final Realm realm;
    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public void savePhoto(Photo photo) {
        realm.beginTransaction();
        realm.copyToRealm(photo);
        realm.commitTransaction();
    }
    public void deletePhoto(final Photo photo) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                Photo resultPhoto = realm.where(Photo.class).equalTo("id", photo.getId()).findFirst();
                resultPhoto.deleteFromRealm();
            }
        });
    }
    public boolean isPhotoExist(String photoId) {
        Photo resultPhoto = realm.where(Photo.class).equalTo("id", photoId).findFirst();
        return resultPhoto != null;
    }
    public List<Photo> getPhotos() {
        return realm.where(Photo.class).findAll();
    }
}
