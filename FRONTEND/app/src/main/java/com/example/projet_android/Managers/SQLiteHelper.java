package com.example.projet_android.Managers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LivraisonDB.db";
    private static final int DATABASE_VERSION = 14;

    public static final String TABLE_LIVRAISONS = "livraisons";
    public static final String KEY_LOCAL_ID = "local_id";
    public static final String KEY_ID = "id";
    public static final String KEY_NOM = "nomClient";
    public static final String KEY_ETAT = "etat";
    public static final String KEY_ADRESSE = "adresse";
    public static final String KEY_TEL = "telephone";
    public static final String KEY_MONTANT = "montant";
    public static final String KEY_MODE_PAY = "mode_payement";
    public static final String KEY_NB_ARTICLES = "nombres_articles";
    public static final String KEY_SYNC_STATUS = "sync_status";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_LIVRAISONS + "("
                + KEY_LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ID + " INTEGER, "
                + KEY_NOM + " TEXT, "
                + KEY_ETAT + " TEXT, "
                + KEY_ADRESSE + " TEXT, "
                + KEY_TEL + " TEXT, "
                + KEY_MONTANT + " REAL, "
                + KEY_MODE_PAY + " TEXT, "
                + KEY_NB_ARTICLES + " INTEGER, "
                + KEY_SYNC_STATUS + " INTEGER DEFAULT 1" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVRAISONS);
        onCreate(db);
    }
}