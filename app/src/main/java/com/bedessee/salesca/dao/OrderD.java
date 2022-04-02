package com.bedessee.salesca.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bedessee.salesca.modal.ShoppingCartNew;

import java.util.List;

@Dao
public interface OrderD {
    @Query("SELECT * FROM ShoppingCartNew")
    List<ShoppingCartNew> getAllNotes();
    @Insert
    void insertOrder(ShoppingCartNew order);
    @Update
    void updateOrder(ShoppingCartNew order);
    @Delete
    void deleteOrder(ShoppingCartNew order);
}
