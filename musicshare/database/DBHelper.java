package com.sms.musicshare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.playlist.PlayList;

import java.util.ArrayList;
import java.util.Iterator;

public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "db_musicshare_playlist";
    private static final int DB_VERSION = 1;

    private final String PLAYLIST_TABLE_NAME = "table_playlist";
    private final String PLAYLIST_COL_ID = "playlist_id";
    private final String PLAYLIST_COL_NAME = "playlist_name";

    private final String TRACKS_TABLE_NAME = "table_tracks";
    private final String TRACK_COL_ID = "track_id";
    private final String TRACK_COL_PATH = "track_path";
    private final String TRACK_COL_TITLE = "track_title";
    private final String TRACK_COL_ARTIST = "track_artist";
    private final String TRACK_COL_ALBUM = "track_album";
    private final String TRACK_COL_GENRE = "track_genre";
    private final String TRACK_COL_FAVORITES = "track_favorites";
    private final String TRACK_COL_SHARED = "track_shared";
    private final String TRACK_COL_DURATION = "track_duration";

    private final String FAVORITES_TABLE_NAME = "table_favorites";
    private final String FAVORITE_COL_ID = "favorite_id";
    private final String FAVORITE_COL_PATH = "favorite_path";
    private final String FAVORITE_COL_TITLE = "favorite_title";
    private final String FAVORITE_COL_ARTIST = "favorite_artist";
    private final String FAVORITE_COL_ALBUM = "favorite_album";
    private final String FAVORITE_COL_GENRE = "favorite_genre";
    private final String FAVORITE_COL_FAVORITES = "favorite_favorites";
    private final String FAVORITE_COL_SHARED = "favorite_shared";
    private final String FAVORITE_COL_DURATION = "favorite_duration";

    private final String MUSICSHARED_TABLE_NAME = "table_musicshared";
    private final String MUSICSHARED_COL_ID = "musicshared_id";
    private final String MUSICSHARED_COL_PATH = "musicshared_path";
    private final String MUSICSHARED_COL_TITLE = "musicshared_title";
    private final String MUSICSHARED_COL_ARTIST = "musicshared_artist";
    private final String MUSICSHARED_COL_ALBUM = "musicshared_album";
    private final String MUSICSHARED_COL_GENRE = "musicshared_genre";
    private final String MUSICSHARED_COL_FAVORITES = "musicshared_favorites";
    private final String MUSICSHARED_COL_SHARED = "musicshared_shared";
    private final String MUSICSHARED_COL_DURATION = "musicshared_duration";

    private final String ARTIST_TABLE_NAME = "table_artists";
    private final String ARTIST_COL_ID = "artist_id";
    private final String ARTIST_COL_NAME = "artist_name";
    private final String ARTIST_COL_NALBUMS = "artist_Nalbums";
    private final String ARTIST_COL_NTRACKS = "artist_Ntracks";

    private final String ALBUM_TABLE_NAME = "table_albums";
    private final String ALBUM_COL_ID = "album_id";
    private final String ALBUM_COL_NAME = "album_name";
    private final String ALBUM_COL_ALBUM_ART_PATH = "album_album_art_path";
    private final String ALBUM_COL_NTRACKS = "album_Ntracks";
    private final String ALBUM_COL_ARTIST = "album_artist_name";

    private final String GENRE_TABLE_NAME = "table_genre";
    private final String GENRE_COL_ID = "genre_id";
    private final String GENRE_COL_NAME = "genre_name";
    private final String GENRE_COL_NTRACKS = "artist_Ntracks";

    private final String ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME = "table_associate_track_playlist";
    private final String ASSOCIATE_TRACK_PLAYLIST_ID = "associate_track_playlist_id";
    private final String ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST = "associate_track_playlist_id_playlist";
    private final String ASSOCIATE_TRACK_PLAYLIST_ID_TRACK = "associate_track_playlist_id_track";

    SQLiteDatabase mDB;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String create_table_playlist = "CREATE TABLE IF NOT EXISTS " + PLAYLIST_TABLE_NAME + " (" +
                                        PLAYLIST_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        PLAYLIST_COL_NAME + " TEXT NOT NULL UNIQUE" +
                                        ");";

        String create_table_tracks = "CREATE TABLE IF NOT EXISTS " + TRACKS_TABLE_NAME + " (" +
                                      TRACK_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      TRACK_COL_PATH + " TEXT NOT NULL UNIQUE," +
                                      TRACK_COL_TITLE + " TEXT," +
                                      TRACK_COL_ARTIST + " INTEGER," +
                                      TRACK_COL_ALBUM + " INTEGER," +
                                      TRACK_COL_GENRE + " INTEGER," +
                                      TRACK_COL_FAVORITES + " BOOLEAN," +
                                      TRACK_COL_SHARED + " BOOLEAN," +
                                      TRACK_COL_DURATION + " BIGINT" +
                                      ");";

        String create_table_favorites = "CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE_NAME + " (" +
                                      FAVORITE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      FAVORITE_COL_PATH + " TEXT NOT NULL UNIQUE," +
                                      FAVORITE_COL_TITLE + " TEXT," +
                                      FAVORITE_COL_ARTIST + " INTEGER," +
                                      FAVORITE_COL_ALBUM + " INTEGER," +
                                      FAVORITE_COL_GENRE + " INTEGER," +
                                      FAVORITE_COL_FAVORITES + " BOOLEAN," +
                                      FAVORITE_COL_SHARED + " BOOLEAN,"+
                                      FAVORITE_COL_DURATION + " BIGINT" +
                                      ");";

        String create_table_musicshared = "CREATE TABLE IF NOT EXISTS " + MUSICSHARED_TABLE_NAME + " (" +
                                      MUSICSHARED_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      MUSICSHARED_COL_PATH + " TEXT NOT NULL UNIQUE," +
                                      MUSICSHARED_COL_TITLE + " TEXT," +
                                      MUSICSHARED_COL_ARTIST + " INTEGER," +
                                      MUSICSHARED_COL_ALBUM + " INTEGER," +
                                      MUSICSHARED_COL_GENRE + " INTEGER," +
                                      MUSICSHARED_COL_FAVORITES + " BOOLEAN," +
                                      MUSICSHARED_COL_SHARED + " BOOLEAN,"+
                                      MUSICSHARED_COL_DURATION + " BIGINT" +
                                      ");";

        String create_table_artist = "CREATE TABLE IF NOT EXISTS " + ARTIST_TABLE_NAME + " (" +
                                      ARTIST_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      ARTIST_COL_NAME + " TEXT NOT NULL UNIQUE," +
                                      ARTIST_COL_NALBUMS + " TEXT," +
                                      ARTIST_COL_NTRACKS + " TEXT" +
                                      ");";

        String create_table_album = "CREATE TABLE IF NOT EXISTS " + ALBUM_TABLE_NAME + " (" +
                                     ALBUM_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     ALBUM_COL_NAME + " TEXT NOT NULL UNIQUE," +
                                     ALBUM_COL_NTRACKS + " TEXT," +
                                     ALBUM_COL_ALBUM_ART_PATH + " TEXT," +
                                     ALBUM_COL_ARTIST + " TEXT" +
                                     ");";

        String create_table_genre = "CREATE TABLE IF NOT EXISTS " + GENRE_TABLE_NAME + " (" +
                                     GENRE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     GENRE_COL_NAME + " TEXT NOT NULL UNIQUE," +
                                     GENRE_COL_NTRACKS + " TEXT" +
                                     ");";

        String create_table_associate_track_playlist = "CREATE TABLE IF NOT EXISTS " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME + " (" +
                                                       ASSOCIATE_TRACK_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                       ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " INTEGER REFERENCES " + PLAYLIST_TABLE_NAME + "(" + PLAYLIST_COL_ID + ") ON DELETE CASCADE," +
                                                       ASSOCIATE_TRACK_PLAYLIST_ID_TRACK + " INTEGER REFERENCES " + TRACKS_TABLE_NAME + "(" + TRACK_COL_ID + ") ON DELETE CASCADE"
                                                        + ");";

        sqLiteDatabase.execSQL(create_table_playlist);
        sqLiteDatabase.execSQL(create_table_tracks);
        sqLiteDatabase.execSQL(create_table_album);
        sqLiteDatabase.execSQL(create_table_artist);
        sqLiteDatabase.execSQL(create_table_genre);
        sqLiteDatabase.execSQL(create_table_favorites);
        sqLiteDatabase.execSQL(create_table_associate_track_playlist);
        sqLiteDatabase.execSQL(create_table_musicshared);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        super.onOpen(sqLiteDatabase);

        String create_table_playlist = "CREATE TABLE IF NOT EXISTS " + PLAYLIST_TABLE_NAME + " (" +
                PLAYLIST_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PLAYLIST_COL_NAME + " TEXT NOT NULL UNIQUE" +
                ");";

        String create_table_tracks = "CREATE TABLE IF NOT EXISTS " + TRACKS_TABLE_NAME + " (" +
                TRACK_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TRACK_COL_PATH + " TEXT NOT NULL UNIQUE," +
                TRACK_COL_TITLE + " TEXT," +
                TRACK_COL_ARTIST + " INTEGER," +
                TRACK_COL_ALBUM + " INTEGER," +
                TRACK_COL_GENRE + " INTEGER," +
                TRACK_COL_FAVORITES + " BOOLEAN," +
                TRACK_COL_SHARED + " BOOLEAN," +
                TRACK_COL_DURATION + " BIGINT" +
                ");";

        String create_table_favorites = "CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE_NAME + " (" +
                FAVORITE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FAVORITE_COL_PATH + " TEXT NOT NULL UNIQUE," +
                FAVORITE_COL_TITLE + " TEXT," +
                FAVORITE_COL_ARTIST + " INTEGER," +
                FAVORITE_COL_ALBUM + " INTEGER," +
                FAVORITE_COL_GENRE + " INTEGER," +
                FAVORITE_COL_FAVORITES + " BOOLEAN," +
                FAVORITE_COL_SHARED + " BOOLEAN,"+
                FAVORITE_COL_DURATION + " BIGINT" +
                ");";

        String create_table_musicshared = "CREATE TABLE IF NOT EXISTS " + MUSICSHARED_TABLE_NAME + " (" +
                MUSICSHARED_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MUSICSHARED_COL_PATH + " TEXT NOT NULL UNIQUE," +
                MUSICSHARED_COL_TITLE + " TEXT," +
                MUSICSHARED_COL_ARTIST + " INTEGER," +
                MUSICSHARED_COL_ALBUM + " INTEGER," +
                MUSICSHARED_COL_GENRE + " INTEGER," +
                MUSICSHARED_COL_FAVORITES + " BOOLEAN," +
                MUSICSHARED_COL_SHARED + " BOOLEAN,"+
                MUSICSHARED_COL_DURATION + " BIGINT" +
                ");";

        String create_table_artist = "CREATE TABLE IF NOT EXISTS " + ARTIST_TABLE_NAME + " (" +
                ARTIST_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ARTIST_COL_NAME + " TEXT NOT NULL UNIQUE," +
                ARTIST_COL_NALBUMS + " TEXT," +
                ARTIST_COL_NTRACKS + " TEXT" +
                ");";

        String create_table_album = "CREATE TABLE IF NOT EXISTS " + ALBUM_TABLE_NAME + " (" +
                ALBUM_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ALBUM_COL_NAME + " TEXT NOT NULL UNIQUE," +
                ALBUM_COL_NTRACKS + " TEXT," +
                ALBUM_COL_ALBUM_ART_PATH + " TEXT," +
                ALBUM_COL_ARTIST + " TEXT" +
                ");";

        String create_table_genre = "CREATE TABLE IF NOT EXISTS " + GENRE_TABLE_NAME + " (" +
                GENRE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GENRE_COL_NAME + " TEXT NOT NULL UNIQUE," +
                GENRE_COL_NTRACKS + " TEXT" +
                ");";

        String create_table_associate_track_playlist = "CREATE TABLE IF NOT EXISTS " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME + " (" +
                ASSOCIATE_TRACK_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " INTEGER REFERENCES " + PLAYLIST_TABLE_NAME + "(" + PLAYLIST_COL_ID + ") ON DELETE CASCADE," +
                ASSOCIATE_TRACK_PLAYLIST_ID_TRACK + " INTEGER REFERENCES " + TRACKS_TABLE_NAME + "(" + TRACK_COL_ID + ") ON DELETE CASCADE"
                + ");";

        sqLiteDatabase.execSQL(create_table_playlist);
        sqLiteDatabase.execSQL(create_table_tracks);
        sqLiteDatabase.execSQL(create_table_album);
        sqLiteDatabase.execSQL(create_table_artist);
        sqLiteDatabase.execSQL(create_table_genre);
        sqLiteDatabase.execSQL(create_table_favorites);
        sqLiteDatabase.execSQL(create_table_associate_track_playlist);
        sqLiteDatabase.execSQL(create_table_musicshared);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     *
     * RESTITUISCE TUTTE LE PLAYLIST ESISTENTI
     */


    public ArrayList<PlayList> getAllPlayLists() {
        ArrayList<PlayList> mListPlaylist = new ArrayList<>();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + PLAYLIST_TABLE_NAME;

        Cursor mCursor = mDB.rawQuery(select_all, null);

        if(mCursor.moveToFirst())
            do{
                PlayList mPlaylist = new PlayList(mCursor.getInt(0), mCursor.getString(1));
                mListPlaylist.add(mPlaylist);
            }while(mCursor.moveToNext());

        mDB.close();

        return mListPlaylist;
    }

    /**
     *
     * RESTITUISCE TUTTE LE CANZONI PREFERITE
     */

    public ArrayList<InfoTrack> getAllFavoritesTracks() {
        return getAllTracks(FAVORITES_TABLE_NAME);
    }

    /**
     *
     * RESTITUISCE TUTTE LE CANZONI DISPONIBILI PER LA CONDIVISIONE
     */

    public ArrayList<InfoTrack> getAllSharedTracks() {
        return getAllTracks(MUSICSHARED_TABLE_NAME);
    }

    private ArrayList<InfoTrack> getAllTracks(String table) {
        ArrayList<InfoTrack> tracks = new ArrayList<>();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + table;

        Cursor mCursor = mDB.rawQuery(select_all, null);
        boolean bool, bool2;

        if(mCursor.moveToFirst())
            do{
                if(mCursor.getInt(6) == 1)
                    bool = true;
                else
                    bool = false;

                if(mCursor.getInt(7) == 1)
                    bool2 = true;
                else
                    bool2 = false;

                InfoTrack mTrack = new InfoTrack(mCursor.getString(1), mCursor.getString(2), getArtistFromTrackID(mCursor.getInt(3)), getAlbumFromTrackID(mCursor.getInt(4)), getGenreFromTrackID(mCursor.getInt(5)), bool, bool2, mCursor.getLong(8));
                tracks.add(mTrack);
            }while(mCursor.moveToNext());

        mDB.close();

        return tracks;
    }

    /**
     *
     *      INSERISCE UNA LISTA DI TRACCE NELLA TABELLA DEI PREFERITI
     *
     */

    public void insertTracksInFavorites(ArrayList<InfoTrack> tracks){
        mDB = this.getWritableDatabase();

       for(InfoTrack temp : tracks)
            insertSingleTrackInFavorites(temp, temp.getArtist(), temp.getAlbum(), temp.getGenre());
        mDB.close();
    }

    /**
     *      INSERISCE UNA NUOVA TRACCIA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *
     */
    public void insertSingleTrackInFavorites(InfoTrack track){
        mDB = this.getWritableDatabase();

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(FAVORITE_COL_PATH, track.getPath());
        mContentValues.put(FAVORITE_COL_TITLE, track.getTitle());
        mContentValues.put(FAVORITE_COL_ARTIST, insertArtist(track.getArtist()));
        mContentValues.put(FAVORITE_COL_ALBUM, insertAlbum(track.getAlbum()));
        mContentValues.put(FAVORITE_COL_GENRE, insertGenre(track.getGenre()));
        mContentValues.put(FAVORITE_COL_FAVORITES, true);
        mContentValues.put(FAVORITE_COL_SHARED, track.isShared());
        mContentValues.put(FAVORITE_COL_DURATION, track.getDuration());

        mDB.insert(FAVORITES_TABLE_NAME, null, mContentValues);

        mDB.close();
    }

    /**
     *      INSERISCE UNA NUOVA TRACCIA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *
     */
    private void insertSingleTrackInFavorites(InfoTrack track, Artist artist, Album album, Genre genre){

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(FAVORITE_COL_PATH, track.getPath());
        mContentValues.put(FAVORITE_COL_TITLE, track.getTitle());
        mContentValues.put(FAVORITE_COL_ARTIST, insertArtist(artist));
        mContentValues.put(FAVORITE_COL_ALBUM, insertAlbum(album));
        mContentValues.put(FAVORITE_COL_GENRE, insertGenre(genre));
        mContentValues.put(FAVORITE_COL_FAVORITES, track.isFavorite());
        mContentValues.put(FAVORITE_COL_SHARED, track.isShared());
        mContentValues.put(FAVORITE_COL_DURATION, track.getDuration());

        mDB.insert(FAVORITES_TABLE_NAME, null, mContentValues);
    }

    /**
     *
     *  RIMUOVE UNA TRACCIA DAL DATABASE DELLE CANZONI PREFERITE
     */

    public void removeTrackFromFavorites(String track_path){
        mDB = this.getWritableDatabase();

        String remove = "DELETE FROM " + FAVORITES_TABLE_NAME +
                " WHERE " + FAVORITE_COL_PATH + " = " + "\"" + track_path + "\"";

        mDB.execSQL(remove);
        mDB.close();
    }

    /**
     *
     *      INSERISCE UNA LISTA DI TRACCE NELLA TABELLA DELLE CANZONI CONDIVISE
     *
     */

    public void insertTracksInSharedMusic(ArrayList<InfoTrack> tracks){
        mDB = this.getWritableDatabase();

        for(InfoTrack temp : tracks)
            insertSingleTrackInSharedMusic(temp, temp.getArtist(), temp.getAlbum(), temp.getGenre());
        mDB.close();
    }

    /**
     *      INSERISCE UNA NUOVA TRACCIA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *
     */
    public void insertSingleTrackInSharedMusic(InfoTrack track){
        mDB = this.getWritableDatabase();

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(MUSICSHARED_COL_PATH, track.getPath());
        mContentValues.put(MUSICSHARED_COL_TITLE, track.getTitle());
        mContentValues.put(MUSICSHARED_COL_ARTIST, insertArtist(track.getArtist()));
        mContentValues.put(MUSICSHARED_COL_ALBUM, insertAlbum(track.getAlbum()));
        mContentValues.put(MUSICSHARED_COL_GENRE, insertGenre(track.getGenre()));
        mContentValues.put(MUSICSHARED_COL_FAVORITES, track.isFavorite());
        mContentValues.put(MUSICSHARED_COL_SHARED, true);
        mContentValues.put(MUSICSHARED_COL_DURATION, track.getDuration());

        mDB.insert(MUSICSHARED_TABLE_NAME, null, mContentValues);
        mDB.close();
    }

    /**
     *      INSERISCE UNA NUOVA TRACCIA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *
     */
    private void insertSingleTrackInSharedMusic(InfoTrack track, Artist artist, Album album, Genre genre){
        mDB = this.getWritableDatabase();

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(MUSICSHARED_COL_PATH, track.getPath());
        mContentValues.put(MUSICSHARED_COL_TITLE, track.getTitle());
        mContentValues.put(MUSICSHARED_COL_ARTIST, insertArtist(artist));
        mContentValues.put(MUSICSHARED_COL_ALBUM, insertAlbum(album));
        mContentValues.put(MUSICSHARED_COL_GENRE, insertGenre(genre));
        mContentValues.put(MUSICSHARED_COL_FAVORITES, track.isFavorite());
        mContentValues.put(MUSICSHARED_COL_SHARED, track.isShared());
        mContentValues.put(MUSICSHARED_COL_DURATION, track.getDuration());

        mDB.insert(MUSICSHARED_TABLE_NAME, null, mContentValues);

        mDB.close();
    }

    /**
     *
     *  RIMUOVE UNA TRACCIA DAL DATABASE DELLE CANZONI CONDIVISE
     */

    public void removeTrackFromSharedMusic(String track_path){
        mDB = this.getWritableDatabase();

        String remove = "DELETE FROM " + MUSICSHARED_TABLE_NAME +
                " WHERE " + MUSICSHARED_COL_PATH + " = " + "\"" + track_path + "\"";

        mDB.execSQL(remove);
        mDB.close();
    }


    /**
     *  RESTITUISCE TUTTE LE INFORMAZIONI DELLA TRACCIA DI UNA DETERMINATA PLAYLIST
     *
     */
    public ArrayList<InfoTrack> getAllTracksFromPlaylist(int playlist_id) {
        ArrayList<InfoTrack> mListPlaylistTracks = new ArrayList<>();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + TRACKS_TABLE_NAME +
                            " INNER JOIN " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME + " ON " +
                            TRACKS_TABLE_NAME + "." + TRACK_COL_ID + " = " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME + "." + ASSOCIATE_TRACK_PLAYLIST_ID_TRACK +
                            " WHERE " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME + "." + ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " = " + playlist_id;

        Cursor mCursor = mDB.rawQuery(select_all, null);
        boolean bool, bool2;

        if(mCursor.moveToFirst())
            do{
                if(mCursor.getInt(6) == 1)
                    bool = true;
                else
                    bool = false;

                if(mCursor.getInt(7) == 1)
                    bool2 = true;
                else
                    bool2 = false;

                InfoTrack mTrack = new InfoTrack(mCursor.getString(1), mCursor.getString(2), getArtistFromTrackID(mCursor.getInt(3)), getAlbumFromTrackID(mCursor.getInt(4)), getGenreFromTrackID(mCursor.getInt(5)), bool, bool2, mCursor.getLong(8));
                mListPlaylistTracks.add(mTrack);
            }while(mCursor.moveToNext());

        mDB.close();

        return mListPlaylistTracks;
    }


    /**
     *  RESTITUISCE TUTTE LE INFORMAZIONI DELL'ALBUM DI UNA DETERMINATA TRACCIA
     *
     */
    public Album getAlbumFromTrackID(int track_id) {
        Album album = new Album();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + ALBUM_TABLE_NAME +
                " WHERE " + ALBUM_TABLE_NAME + "." + ALBUM_COL_ID + " = " + track_id;

        Cursor mCursor = mDB.rawQuery(select_all, null);

        if(mCursor.moveToFirst())
            do{
                album.setName(mCursor.getString(1));
                album.setNTracks(mCursor.getString(2));
                album.setAlbumArt(mCursor.getString(3));
                Artist artist = new Artist();
                artist.setName(mCursor.getString(4));
                album.setArtist(artist);
            }while(mCursor.moveToNext());

        mDB.close();

        return album;
    }

    /**
     *  RESTITUISCE TUTTE LE INFORMAZIONI DELL'ALBUM DI UNA DETERMINATA TRACCIA
     *
     */
    public Artist getArtistFromTrackID(int track_id) {
        Artist artist = new Artist();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + ARTIST_TABLE_NAME +
                " WHERE " + ARTIST_TABLE_NAME + "." + ARTIST_COL_ID + " = " + track_id;

        Cursor mCursor = mDB.rawQuery(select_all, null);

        if(mCursor.moveToFirst())
            do{
                artist.setName(mCursor.getString(1));
                artist.setCountAlbums(mCursor.getString(2));
                artist.setCountTracks(mCursor.getString(3));
            }while(mCursor.moveToNext());

        mDB.close();

        return artist;
    }

    /**
     *  RESTITUISCE TUTTE LE INFORMAZIONI DEL GENERE DI UNA DETERMINATA TRACCIA
     *
     */
    public Genre getGenreFromTrackID(int track_id) {
        Genre genre = new Genre();
        mDB = this.getReadableDatabase();

        String select_all = "SELECT * FROM " + GENRE_TABLE_NAME +
                " WHERE " + GENRE_TABLE_NAME + "." + GENRE_COL_ID + " = " + track_id;

        Cursor mCursor = mDB.rawQuery(select_all, null);

        if(mCursor.moveToFirst())
            do{
                genre.setName(mCursor.getString(1));
                genre.setNTracks(mCursor.getString(2));
            }while(mCursor.moveToNext());

        mDB.close();

        return genre;
    }

    public void removeAllTracksFromPlaylist(int playlist_id) {
        mDB = this.getWritableDatabase();

        String remove = "DELETE FROM " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME +
                " WHERE " + ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " = " + playlist_id;

        mDB.execSQL(remove);
        mDB.close();
    }

    public void removePlaylist(int playlist_id) {
        mDB = this.getWritableDatabase();

        String remove = "DELETE FROM " + PLAYLIST_TABLE_NAME +
                " WHERE " + PLAYLIST_COL_ID + " = " + playlist_id + ";";

        mDB.execSQL(remove);
        mDB.close();
    }

    public void updatePlaylistName(int playlist_id, String value) {
        mDB = this.getWritableDatabase();

        String update = "UPDATE " + PLAYLIST_TABLE_NAME +
                " SET " + PLAYLIST_COL_NAME + " = \"" + value + "\"" +
                " WHERE " + PLAYLIST_COL_ID + " = " + playlist_id + ";";

        mDB.execSQL(update);
        mDB.close();
    }

    public void removeTrackFromPlaylist(int playlist_id, int track_id){
        mDB = this.getWritableDatabase();

        String remove = "DELETE FROM " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME +
                        " WHERE " + ASSOCIATE_TRACK_PLAYLIST_ID_TRACK + " = " + track_id + " AND " +
                        ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " = " + playlist_id + ";";

        mDB.execSQL(remove);
        mDB.close();
    }

    /**
     *
     *      INSERISCE UNA SERIE DI TRACCE NEL DATABASE E LA ASSOCIA ALLA PLAYLIST
     *
     */

    public void insertTracksInPlayList(ArrayList<InfoTrack> tracks, int playlist_id){
        mDB = this.getWritableDatabase();

        Iterator<InfoTrack> tracksIterator = tracks.iterator();

        while(tracksIterator.hasNext()){
            InfoTrack temp = tracksIterator.next();

            int track_id = insertTrack(temp, temp.getArtist(), temp.getAlbum(), temp.getGenre());
            insertAssociateTrackPlaylist(playlist_id, track_id);
        }

        mDB.close();
    }

    /**
     *
     *      INSERISCE UNA TRACCIA NEL DATABASE E LA ASSOCIA ALLA PLAYLIST
     *
     */

    public void insertOneTrackInPlayList(InfoTrack track, int playlist_id){
        mDB = this.getWritableDatabase();

        int track_id = insertTrack(track, track.getArtist(), track.getAlbum(), track.getGenre());
        insertAssociateTrackPlaylist(playlist_id, track_id);

        mDB.close();
    }

    /**
     *      ASSOCIA LA TRACCIA ALLA PLAYLIST E INSERISCE LE INFORMAZIONI
     *      NELLA TABELLA DEL DATABASE
     *
     */

    private void insertAssociateTrackPlaylist(int playlist_id, int track_id){
        ContentValues mAssociateValues = new ContentValues();

        mAssociateValues.put(ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST, playlist_id);
        mAssociateValues.put(ASSOCIATE_TRACK_PLAYLIST_ID_TRACK, track_id);

        mDB.insert(ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME, null, mAssociateValues);
    }

    /**
     *      INSERISCE UN NUOVO GENERE (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *      E RESTITUISCE L'ID DEL GENRE APPENA INSERITO
     */
    private int insertGenre(Genre genre){
        ContentValues mContentValues = new ContentValues();

        int genre_id = -1;

        mContentValues.put(GENRE_COL_NAME, genre.getName());
        mContentValues.put(GENRE_COL_NTRACKS, genre.getNTracks());

        mDB.insert(GENRE_TABLE_NAME, null, mContentValues);

        String select_album = "SELECT * FROM " + GENRE_TABLE_NAME +
                " WHERE " + GENRE_COL_NAME + " = \""+ mContentValues.get(GENRE_COL_NAME) + "\";";

        Cursor mCursor = mDB.rawQuery(select_album, null);
        if (mCursor.moveToFirst())
            do {
                genre_id = mCursor.getInt(0);
            } while (mCursor.moveToNext());

        return genre_id;
    }


    /**
     *      INSERISCE UN NUOVO ARTISTA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *      E RESTITUISCE L'ID DELL'ARTISTA APPENA INSERITO
     */
    private int insertArtist(Artist artist){
        ContentValues mContentValues = new ContentValues();

        int artist_id = -1;

        mContentValues.put(ARTIST_COL_NAME, artist.getName());
        mContentValues.put(ARTIST_COL_NALBUMS, artist.getCountAlbums());
        mContentValues.put(ARTIST_COL_NTRACKS, artist.getCountTracks());

        mDB.insert(ARTIST_TABLE_NAME, null, mContentValues);

        String select_album = "SELECT * FROM " + ARTIST_TABLE_NAME +
                " WHERE " + ARTIST_COL_NAME + " = \""+ mContentValues.get(ARTIST_COL_NAME) + "\";";

        Cursor mCursor = mDB.rawQuery(select_album, null);
        if (mCursor.moveToFirst())
            do {
                artist_id = mCursor.getInt(0);
            } while (mCursor.moveToNext());

        return artist_id;
    }

    /**
     *      INSERISCE UN NUOVO ALBUM (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *      E RESTITUISCE L'ID DELL'ALBUM APPENA INSERITO
     */
    private int insertAlbum(Album album){
        ContentValues mContentValues = new ContentValues();

        int album_id = 0;

        mContentValues.put(ALBUM_COL_NAME, album.getName());
        mContentValues.put(ALBUM_COL_NTRACKS, album.getNTracks());
        mContentValues.put(ALBUM_COL_ALBUM_ART_PATH, album.getAlbumArt());
        mContentValues.put(ALBUM_COL_ARTIST, album.getArtist().getName());

        mDB.insert(ALBUM_TABLE_NAME, null, mContentValues);

        String select_album = "SELECT * FROM " + ALBUM_TABLE_NAME +
                " WHERE " + ALBUM_COL_NAME + " = \""+ mContentValues.get(ALBUM_COL_NAME) + "\";";

        Cursor mCursor = mDB.rawQuery(select_album, null);
        if (mCursor.moveToFirst())
            do {
                album_id = mCursor.getInt(0);
            } while (mCursor.moveToNext());

        return album_id;
    }

    /**
     *      INSERISCE UNA NUOVA TRACCIA (SE NON ESISTE) NELLA TABELLA DEL DATABASE
     *      E RESTITUISCE L'ID DELLA TRACCIA APPENA INSERITA
     */
    private int insertTrack(InfoTrack track, Artist artist, Album album, Genre genre){

        ContentValues mContentValues = new ContentValues();

        int track_id = 0;

        mContentValues.put(TRACK_COL_PATH, track.getPath());
        mContentValues.put(TRACK_COL_TITLE, track.getTitle());
        mContentValues.put(TRACK_COL_ARTIST, insertArtist(artist));
        mContentValues.put(TRACK_COL_ALBUM, insertAlbum(album));
        mContentValues.put(TRACK_COL_GENRE, insertGenre(genre));
        mContentValues.put(TRACK_COL_FAVORITES, track.isFavorite());

        mDB.insert(TRACKS_TABLE_NAME, null, mContentValues);

        String select_track = "SELECT * FROM " + TRACKS_TABLE_NAME +
                " WHERE " + TRACK_COL_PATH + " = \""+ mContentValues.get(TRACK_COL_PATH) + "\";";

        Cursor mCursor = mDB.rawQuery(select_track, null);
        if (mCursor.moveToFirst())
            do {
                track_id = mCursor.getInt(0);
            } while (mCursor.moveToNext());

        return track_id;
    }

    public int getPlaylistID(String PlaylistName){
        mDB = this.getReadableDatabase();
        int id = 0;

        String ID_query = "SELECT " + PLAYLIST_COL_ID  + " FROM " + PLAYLIST_TABLE_NAME
                + " WHERE " + PLAYLIST_COL_NAME + " = \""+ PlaylistName +"\";";

        Cursor mCursor = mDB.rawQuery(ID_query, null);

        if(mCursor.moveToFirst())
            do{
                id = mCursor.getInt(0);
            }while(mCursor.moveToNext());

        mDB.close();

        return id;
    }

    public int getTrackID(String TrackPath){
        mDB = this.getReadableDatabase();
        int id = -1;

        String ID_query = "SELECT " + TRACK_COL_ID  + " FROM " + TRACKS_TABLE_NAME
                + " WHERE " + TRACK_COL_PATH + " = \""+ TrackPath + "\";";

        Cursor mCursor = mDB.rawQuery(ID_query, null);

        if(mCursor.moveToFirst())
            do{
                id = mCursor.getInt(0);
            }while(mCursor.moveToNext());

        mDB.close();

        return id;
    }


    public boolean insertPlaylist(String name_playlist) {
        mDB = this.getWritableDatabase();

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(PLAYLIST_COL_NAME, name_playlist);

        long i = mDB.insert(PLAYLIST_TABLE_NAME, null, mContentValues);

        mDB.close();

        if(i == -1)
            return false;
        else
            return true;
    }

    public int countTracksInPlaylist(int playlist_id) {
        mDB = this.getReadableDatabase();
        int num_rows = 0;

        String query = "SELECT * FROM " + ASSOCIATE_TRACK_PLAYLIST_TABLE_NAME +
                          " WHERE " + ASSOCIATE_TRACK_PLAYLIST_ID_PLAYLIST + " = "+ playlist_id +";";

        Cursor mCursor = mDB.rawQuery(query, null);

        if(mCursor.moveToFirst())
            do{
                num_rows++;
            }while(mCursor.moveToNext());

        mDB.close();

        return num_rows;
    }
}
