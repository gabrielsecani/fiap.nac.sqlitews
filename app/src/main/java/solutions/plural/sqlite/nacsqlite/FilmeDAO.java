package solutions.plural.sqlite.nacsqlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


class FilmeDAO {

    private static final String DATABASE_NAME = "filmedb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "TBFILME";
    private static final String CONTROL_PREFS = "CTRL_TENTS";
    private static final int QTDE_TENTATIVAS = 3;
    private static final String INSERT = "insert into " + TABLE_NAME + " (codigo, tempo, descricao) values (?,?,?)";
    private Context context;
    private SQLiteDatabase db;
    private SharedPreferences prefs;
    private SQLiteStatement insertStmt;
    //private SQLiteStatement addTryStmt;
    //private static final String ADD_TENTATIVAS = "update " + CONTROL_PREFS + " set tentativas = tentativas + 1";

    FilmeDAO(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
        this.insertStmt = this.db.compileStatement(INSERT);
        //this.addTryStmt = this.db.compileStatement(ADD_TENTATIVAS);
        prefs = context.getSharedPreferences(CONTROL_PREFS, Context.MODE_PRIVATE);
    }

    private int getTentativas() {
//        if (this.addTryStmt.executeUpdateDelete() == 0) {
//            db.execSQL("INSERT INTO " + CONTROL_PREFS + " (tentativas) values (1)");
//        }
//        Cursor cursor = this.db.query(CONTROL_PREFS, new String[]{"tentativas"}, null, null, null, null, null);
//        int tents = 0;
//        if (cursor.moveToFirst()) {
//            tents = cursor.getInt(0);
//        }
        int tents = prefs.getInt(CONTROL_PREFS, 0) + 1;
        prefs.edit().putInt(CONTROL_PREFS, tents).apply();
        Log.i("SQLITE", "tentativas: " + tents);
        return tents;
    }

    long insert(Filme filme) {
        Log.i("SQLITE", "insert(" + filme.getCodigo() + "+" + filme.getDescricao() + ")");
        this.insertStmt.bindLong(1, filme.getCodigo());
        this.insertStmt.bindLong(2, filme.getTempo());
        this.insertStmt.bindString(3, filme.getDescricao());
        return this.insertStmt.executeInsert();
    }

    private void deleteAll() {
        Log.i("SQLITE", "deleteAll()");
        this.db.delete(TABLE_NAME, null, null);
        prefs.edit().clear().apply();
    }

    List<Filme> selectAll() {
        Log.i("SQLITE", "selectAll()");
        List<Filme> list = new ArrayList<>();
        if (getTentativas() > QTDE_TENTATIVAS) {
            deleteAll();
            return list;
        }

        Cursor cursor = this.db.query(TABLE_NAME, new String[]{"codigo", "tempo", "descricao"},
                null, null, null, null, "codigo");

        if (cursor.moveToFirst()) {
            do {
                Filme filme = new Filme();
                filme.setCodigo(cursor.getInt(0));
                filme.setTempo(cursor.getInt(1));
                filme.setDescricao(cursor.getString(2));
                list.add(filme);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public void encerrarDB() {
        Log.i("SQLITE", "encerrarDB()");
        this.db.close();
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.i("SQLITE", "onCreate()");
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (codigo INTEGER PRIMARY KEY AUTOINCREMENT, tempo INTEGER, descricao TEXT)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Example", "*** Upgrading database, this will drop tables and recreate. " + oldVersion + "->" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
