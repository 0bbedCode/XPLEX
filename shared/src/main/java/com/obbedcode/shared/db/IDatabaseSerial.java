package com.obbedcode.shared.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.obbedcode.shared.ICopyable;

import java.util.List;

public interface IDatabaseSerial extends ICopyable {
    ContentValues toContentValues();
    List<ContentValues> toContentValuesList();

    void writeQuery(SQLSnake snake, SnakeAction wantedAction);

    void fromCursor(Cursor cursor);
    void fromContentValues(ContentValues contentValues);
    void fromContentValuesList(List<ContentValues> contentValues);
}
