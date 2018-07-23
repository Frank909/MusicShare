package com.sms.musicshare.helper;

import android.app.ProgressDialog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.sms.musicshare.R;
import com.sms.musicshare.helper.customTaskPackage.CustomTask;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MusicLibraryScanner{

    /**
     * private class fields
     * */

    private ArrayList<InfoTrack> songsList = new ArrayList<>();
    private ProgressDialog dialog;
    private CustomTask<Object,?,List<InfoTrack>> createTracksList = new CustomTask<>(getContext());
    private Context currentContext;
    private final String unknown = "<unknown>";

    private InjectOnPreExecute injectOnPreExecute;
    private InjectOnDoInBackground injectOnDoInBackground;
    private InjectOnFinishTask injectOnFinishTask;
    private InjectOnTaskCancelled injectOnTaskCancelled;

    /**
     * Constructor
     */
    public MusicLibraryScanner(Context context){
        super();
        this.currentContext = context;

        setPreExecute();
        setDoInBackground(0);
        setFinishTask();
        setOnCancelTask();
    }

    /**
     * Title: setInjectOnPreExecute()
     * @param injectOnPreExecute
     * This method allows to inject code blocks in the pre-execute task phase
     * **/
    public void setInjectOnPreExecute(InjectOnPreExecute injectOnPreExecute){
        this.injectOnPreExecute = injectOnPreExecute;
    }

    /**
     * Title: setInjectOnDoInBackground()
     * @param injectOnDoInBackground
     * This method allows to inject code blocks in the background task phase
     * **/
    public void setInjectOnDoInBackground(InjectOnDoInBackground injectOnDoInBackground){
        this.injectOnDoInBackground = injectOnDoInBackground;
    }


    /**
     * Title: setInjectOnFinishTask()
     * @param injectOnFinishTask
     * This method allows to inject code blocks in the post-execute task phase
     * **/
    public void setInjectOnFinishTask(InjectOnFinishTask injectOnFinishTask){
        this.injectOnFinishTask = injectOnFinishTask;
    }


    /**
     * Title: setInjectOnTaskCancelled()
     * @param injectOnTaskCancelled
     * This method allows to inject code blocks when the task is canceled
     * */
    public void setInjectOnTaskCancelled(InjectOnTaskCancelled injectOnTaskCancelled){
        this.injectOnTaskCancelled = injectOnTaskCancelled;
    }

    private Context getContext(){
        return this.currentContext;
    }

    /**
     * Title : retrieveTracksList()
     * This method allows retrieving the entire collection of music list in your device
     * */
    public ArrayList<InfoTrack> retrieveTracksList(){

        this.dialog = new ProgressDialog(getContext());

        createTracksList.execute();

        return this.songsList;
    }


    /**
     * Title: setPreExecute()
     * preExecuteTask() setter
     * */
    private void setPreExecute(){
        createTracksList.setOnPreExecuteListener(new CustomTask.onPreExecuteListener() {
            @Override
            public void preExecuteTask() {
                if(injectOnPreExecute != null)
                    injectOnPreExecute.onPreExecuteTask();
            }
        });
    }

    private void updateArtist_AlbumInformation(ArrayList<InfoTrack> songs_list, String[] artist_param, String[] album_param) {

        Album[] temp_album = new Album[songs_list.size()];
        ArrayList<String> temp = new ArrayList<>();

        Cursor artistCursor = getContext().getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, artist_param, null, null, null
        );

        int artist_id = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
        int artist_name = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
        int n_album_artist = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
        int n_track_artist = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

        temp.clear();
        if (artistCursor.moveToFirst()) {
            do {
                Artist artist = new Artist();

                if(artistCursor.getString(artist_name) != null)
                    artist.setName(artistCursor.getString(artist_name));
                else
                    artist.setName(unknown);

                if(artistCursor.getString(n_album_artist) != null)
                    artist.setCountAlbums(artistCursor.getString(n_album_artist));
                else
                    artist.setCountAlbums(unknown);

                if(artistCursor.getString(n_track_artist) != null)
                    artist.setCountTracks(artistCursor.getString(n_track_artist));
                else
                    artist.setCountTracks(unknown);

                int artistID = Integer.parseInt(artistCursor.getString(artist_id));
                Uri albumUri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistID);

                Cursor albumCursor = getContext().getContentResolver().query(
                        albumUri, album_param, null, null, null
                );

                int album_name = albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM);
                int album_artist = albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ARTIST);
                int album_art = albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM_ART);
                int n_songs_album = albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST);

                int count = 0;
                temp.clear();
                if(albumCursor.moveToFirst()) {
                    do {
                        Album album = new Album();

                        if(albumCursor.getString(album_name) != null)
                            album.setName(albumCursor.getString(album_name));
                        else
                            album.setName(unknown);

                        if(!temp.contains(album.getName())){
                            temp.add(album.getName());
                            count++;
                        }

                        if(albumCursor.getString(album_artist) != null)
                            album.setArtist(artist);

                        if(albumCursor.getString(album_art) != null)
                            album.setAlbumArt(albumCursor.getString(album_art));
                        else
                            album.setAlbumArt(unknown);

                        if(albumCursor.getString(n_songs_album) != null)
                            album.setNTracks(albumCursor.getString(n_songs_album));
                        else
                            album.setNTracks(unknown);

                        int j = 0;
                        for (InfoTrack track : songs_list) {
                            if (track.getAlbum().getName().equals(album.getName())){
                                track.setAlbum(album);
                                temp_album[j] = album;
                            }
                            else
                                temp_album[j] = null;
                            j++;
                        }
                    }while (albumCursor.moveToNext());
                }

                artist.setCountAlbums(Integer.toString(count));

                for(int i = 0; i < temp_album.length; i++) {
                    if(temp_album[i] != null)
                        if(songs_list.get(i).getAlbum().getName().equals(temp_album[i].getName()))
                            songs_list.get(i).getAlbum().getArtist().setCountAlbums(Integer.toString(count));
                }

                for (InfoTrack track : songs_list) {
                    if (track.getArtist().getName().equals(artist.getName())) {
                        track.setArtist(artist);
                    }
                }

            }while (artistCursor.moveToNext());
        }

        artistCursor.close();
    }

    private void updateGenreInformation(ArrayList<InfoTrack> songs_list) {

        int count = 0;

        ArrayList<Genre> genresList = new ArrayList<>();
        Iterator<InfoTrack> infoTrackIterator = songs_list.iterator();
        while(infoTrackIterator.hasNext()){
            boolean isInserted = false;
            Genre genre = infoTrackIterator.next().getGenre();

            if(genresList.isEmpty())
                genresList.add(genre);
            else {
                for (Genre mGenre : genresList) {
                    if (genre.getName().equals(mGenre.getName())) {
                        isInserted = true;
                        break;
                    }
                }
                if(!isInserted)
                    genresList.add(genre);
            }
        }

        for(Genre genre : genresList){
            for(InfoTrack infoTrack : songs_list){
                if(infoTrack.getGenre().getName().equals(genre.getName()))
                    count++;
            }
            for(InfoTrack infoTrack : songs_list){
                if(infoTrack.getGenre().getName().equals(genre.getName()))
                    infoTrack.getGenre().setNTracks(Integer.toString(count));
            }
            count = 0;
        }
    }



    /**
     * Title: setDoInBackground()
     * @param millis
     * backgroundDoing() setter
     * */
    private void setDoInBackground(final int millis){
        createTracksList.setOnBackGroundListener(new CustomTask.onBackGroundListener<Object, List<InfoTrack>>() {
            @Override
            public List<InfoTrack> backgroundDoing(Object... params) throws InterruptedException {
                if(injectOnDoInBackground != null)
                    injectOnDoInBackground.onDoInBackground(params);

                String[] media_projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Media.DURATION
                };

                String[] genre_projection = new String[]{
                        MediaStore.Audio.Genres._ID,
                        MediaStore.Audio.Genres.NAME,
                };

                String[] artist_projection = new String[]{
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                };

                String[] album_artist_projection = new String[]{
                        MediaStore.Audio.Artists.Albums.ALBUM,
                        MediaStore.Audio.Artists.Albums.ARTIST,
                        MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST,
                        MediaStore.Audio.Artists.Albums.ALBUM_ART
                };

                try {
                    /************************************************************************/

                    /************************CURSOR INFOTRACK AND GENRE**********************/

                    /************************************************************************/

                    Cursor mediaCursor = getContext().getContentResolver().query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_projection, MediaStore.Audio.Media.IS_MUSIC, null, MediaStore.Audio.Media.TITLE + " ASC"
                    );

                    int artist_column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                    int album_column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                    int title_column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int id_column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int path_column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int duration_column_index = mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    if (mediaCursor.moveToFirst()) {
                        do{
                            InfoTrack infotrack = new InfoTrack();
                            Album album = new Album();
                            Artist artist = new Artist();
                            Genre genre = new Genre();

                            if(mediaCursor.getString(artist_column_index) != null)
                                artist.setName(mediaCursor.getString(artist_column_index));
                            else
                                artist.setName(unknown);
                            if(mediaCursor.getString(album_column_index) != null)
                                album.setName(mediaCursor.getString(album_column_index));
                            else
                                album.setName(unknown);
                            album.setArtist(artist);

                            infotrack.setPath(mediaCursor.getString(path_column_index));
                            infotrack.setTitle(mediaCursor.getString(title_column_index));
                            infotrack.setArtist(artist);
                            infotrack.setAlbum(album);
                            infotrack.setDuration(mediaCursor.getLong(duration_column_index));
                            infotrack.setShared(false);
                            infotrack.setFavorite(false);

                            int songID = Integer.parseInt(mediaCursor.getString(id_column_index));

                            Uri uriGenre = MediaStore.Audio.Genres.getContentUriForAudioId("external", songID);
                            Cursor genresCursor = getContext().getContentResolver().query(uriGenre,
                                    genre_projection, null, null, null);
                            int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                            if (genresCursor.moveToFirst()) {
                                do {
                                    genre.setName(genresCursor.getString(genre_column_index));
                                } while (genresCursor.moveToNext());
                            } else {
                                genre.setName(unknown);
                            }
                            infotrack.setGenre(genre);
                            genresCursor.close();

                            songsList.add(infotrack);

                        }while (mediaCursor.moveToNext());
                    }
                    mediaCursor.close();

                    /************************************************************************/

                    /************************CURSOR COUNT ALBUM******************************/

                    /************************************************************************/

                    //updateAlbumInformation(songsList, album_projection);

                    /************************************************************************/

                    /************************CURSOR COUNT ARTIST*****************************/

                    /************************************************************************/

                    updateArtist_AlbumInformation(songsList, artist_projection, album_artist_projection);

                    /************************************************************************/

                    /************************CURSOR COUNT GENRE******************************/

                    /************************************************************************/

                    updateGenreInformation(songsList);

                }catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                Thread.sleep(millis);

                return songsList;
            }
        });
    }


    /**
     * Title: setFinishTask()
     * OnFinishTask(), OnFinishTaskException() setter
     * */
    private void setFinishTask(){
        createTracksList.setOnFinishListener(new CustomTask.onFinishListener<List<InfoTrack>>() {
            @Override
            public List<InfoTrack> OnFinishTask(List<InfoTrack> infoTracks) throws Exception {
                if(injectOnFinishTask != null)
                    injectOnFinishTask.onFinishTask(infoTracks);
                if(dialog.isShowing())
                    dialog.dismiss();
                return null;
            }

            @Override
            public String OnFinishTaskException(Exception e) {
                if(injectOnFinishTask != null)
                    injectOnFinishTask.onFinishTask(e);
                if(dialog.isShowing())
                    dialog.dismiss();
                return null;
            }
        });
    }

    /**
     * Title: setOnCancelTask()
     * onTaskCancelled() setter
     * */
    private void setOnCancelTask(){
        createTracksList.setCancelledListener(new CustomTask.onCancelledListener<List<InfoTrack>>() {
            @Override
            public void onTaskCancelled(List<InfoTrack> infoLists) {
                if(injectOnTaskCancelled != null)
                    injectOnTaskCancelled.onCancel(infoLists);

                if(dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onTaskCancelled() {
                if(injectOnTaskCancelled != null)
                    injectOnTaskCancelled.onCancel();

                if(dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }


    /**
     * This interfaces allow external code blocks injection
     * **/
    public interface InjectOnFinishTask {
        void onFinishTask(List<InfoTrack> infoLists);

        void onFinishTask(Exception e);
    }

    public interface InjectOnDoInBackground {
        void onDoInBackground(Object... params);
    }

    public interface InjectOnPreExecute {
        void onPreExecuteTask();
    }

    public interface InjectOnTaskCancelled {
        void onCancel();
        void onCancel(List<InfoTrack> infoLists);
    }
}
