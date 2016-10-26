package solutions.plural.sqlite.nacsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class FilmeDAO {

    private static final String DATABASE_NAME = "filmedb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "TBFILME";
    private static final String TB_CONTROL_NAME = "TBCONTROLE";
    private static final int QTDE_TENTATIVAS = 3;

    private Context context;
    private SQLiteDatabase db;

    private SQLiteStatement insertStmt;
    private static final String INSERT = "insert into " + TABLE_NAME + " (codigo, tempo, descricao) values (?,?,?)";
    private SQLiteStatement addTryStmt;
    private static final String ADD_TENTATIVAS = "update " + TB_CONTROL_NAME + " set tentativas = tentativas + 1";

    public FilmeDAO(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
        this.insertStmt = this.db.compileStatement(INSERT);
        this.addTryStmt = this.db.compileStatement(ADD_TENTATIVAS);
    }

    public int getTentativas() {
        if (this.addTryStmt.executeUpdateDelete() == 0) {
            db.execSQL("INSERT INTO " + TB_CONTROL_NAME + " (tentativas) values (1)");
        }
        Cursor cursor = this.db.query(TB_CONTROL_NAME, new String[]{"tentativas"}, null, null, null, null, null);
        int tents = 0;
        if (cursor.moveToFirst()) {
            tents = cursor.getInt(0);
        }
        Log.i("SQLITE", "tentativas " + tents);
        return tents;
    }

    public long insert(Filme filme) {
        Log.i("SQLITE", "insert(" + filme.getCodigo() + "+"+filme.getDescricao() + ")");
        this.insertStmt.bindLong(1, filme.getCodigo());
        this.insertStmt.bindLong(2, filme.getTempo());
        this.insertStmt.bindString(3, filme.getDescricao());
        return this.insertStmt.executeInsert();
    }

    public void deleteAll() {
        Log.i("SQLITE", "deleteAll()");
        this.db.delete(TABLE_NAME, null, null);
        this.db.delete(TB_CONTROL_NAME, null, null);
    }

    public List<Filme> selectAll() {
        Log.i("SQLITE", "selectAll()");
        List<Filme> list = new ArrayList<Filme>();
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

        if (cursor != null && !cursor.isClosed()) {
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
            db.execSQL("CREATE TABLE " + TB_CONTROL_NAME + " (tentativas INTEGER)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Example", "*** Upgrading database, this will drop tables and recreate. " + oldVersion + "->" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TB_CONTROL_NAME);
            onCreate(db);
        }
    }
}
