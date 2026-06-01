package com.example.projet_android.Managers;

import static com.example.projet_android.Managers.SQLiteHelper.KEY_NB_ARTICLES;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.projet_android.dto.Livraison;
import java.util.ArrayList;
import java.util.List;

public class LivraisonHandler {
    
    public static final String TABLE_LIVRAISONS = "livraisons";
    public static final String KEY_ID = "id";
    public static final String KEY_NOM = "nomClient";
    public static final String KEY_ETAT = "etat";
    public static final String KEY_ADRESSE = "adresse";
    public static final String KEY_TEL = "telephone";
    public static final String KEY_MONTANT = "montant";
    public static final String KEY_MODE_PAY = "mode_payement";
    public static final String KEY_SYNC_STATUS = "sync_status";

    private SQLiteHelper dbHelper;

    public LivraisonHandler(Context context) {
        dbHelper = new SQLiteHelper(context);
    }
    public void supprimerLivraisons() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_LIVRAISONS, null, null);
        db.close();
    }
    public void ajouterLivraison(Livraison liv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_ID, liv.getId());
        v.put(KEY_NOM, liv.getNomClient());
        v.put(KEY_ETAT, liv.getEtat());
        v.put(KEY_ADRESSE, liv.getAdresse());
        v.put(KEY_TEL, liv.getTelephone());
        v.put(KEY_MONTANT, liv.getMontantTotale());
        v.put(KEY_MODE_PAY, liv.getModePayment());
        v.put(KEY_NB_ARTICLES, liv.getNbArticle());
        v.put(KEY_SYNC_STATUS, 1);

        db.insert(TABLE_LIVRAISONS, null, v);
        db.close();
    }
    @SuppressLint("Range")
    public List<Livraison> getAllLivraisons() {
        List<Livraison> list = new ArrayList<>();
        
        String query = "SELECT * FROM " + TABLE_LIVRAISONS + " WHERE etat='EC' ORDER BY id ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Livraison l = new Livraison();
                l.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                l.setNomClient(c.getString(c.getColumnIndex(KEY_NOM)));
                l.setEtat(c.getString(c.getColumnIndex(KEY_ETAT)));
                l.setAdresse(c.getString(c.getColumnIndex(KEY_ADRESSE)));
                l.setTelephone(c.getString(c.getColumnIndex(KEY_TEL)));
                l.setMontantTotale(c.getDouble(c.getColumnIndex(KEY_MONTANT)));
                l.setModePayment(c.getString(c.getColumnIndex(KEY_MODE_PAY)));
                list.add(l);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    public int countLivraisonsAujourdhui() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LIVRAISONS, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return count;
    }
    @SuppressLint("Range")
    public Livraison getLivraisonById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(TABLE_LIVRAISONS, null, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (c != null && c.moveToFirst()) {
            Livraison l = new Livraison();
            l.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            l.setNomClient(c.getString(c.getColumnIndex(KEY_NOM)));
            l.setEtat(c.getString(c.getColumnIndex(KEY_ETAT)));
            l.setAdresse(c.getString(c.getColumnIndex(KEY_ADRESSE)));
            l.setTelephone(c.getString(c.getColumnIndex(KEY_TEL)));
            l.setMontantTotale(c.getDouble(c.getColumnIndex(KEY_MONTANT)));
            l.setModePayment(c.getString(c.getColumnIndex(KEY_MODE_PAY)));
            l.setNbArticle(c.getInt(c.getColumnIndex(KEY_NB_ARTICLES)));
            c.close();
            db.close();
            return l;
        }
        if (c != null) {
            c.close();
        }
        db.close();
        return null;
    }
    public void modifierEtatLocal(int id, String etat) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_ETAT, etat);
        v.put(KEY_SYNC_STATUS, 0);
        db.update(TABLE_LIVRAISONS, v, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void marquerSynchronise(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_SYNC_STATUS, 1);
        db.update(TABLE_LIVRAISONS, v, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void marquerBesoinSync(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SYNC_STATUS, 0);
        db.update(TABLE_LIVRAISONS, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @SuppressLint("Range")
    public List<Livraison> getLivraisonsASync() {
        List<Livraison> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_LIVRAISONS + " WHERE " + KEY_SYNC_STATUS + " = 0";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Livraison l = new Livraison();
                l.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                l.setNomClient(c.getString(c.getColumnIndex(KEY_NOM)));
                l.setEtat(c.getString(c.getColumnIndex(KEY_ETAT)));
                l.setAdresse(c.getString(c.getColumnIndex(KEY_ADRESSE)));
                l.setTelephone(c.getString(c.getColumnIndex(KEY_TEL)));
                l.setMontantTotale(c.getDouble(c.getColumnIndex(KEY_MONTANT)));
                l.setModePayment(c.getString(c.getColumnIndex(KEY_MODE_PAY)));
                list.add(l);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
}