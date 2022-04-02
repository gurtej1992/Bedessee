package com.bedessee.salesca.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bedessee.salesca.dao.OrderD;
import com.bedessee.salesca.modal.ShoppingCartNew;

@Database(entities = { ShoppingCartNew.class }, version = 1)
public abstract class Db extends RoomDatabase {
    public abstract OrderD orderDeo();
}

