package com.example.denzel.reptilemusiclist;

/**
 * Created by zhouzhirui on 2017/4/19.
 * 歌曲信息存储
 */

public class MusicListItem {
    public String songName = "";
    public String singerName = "";
    public String albumName = "";

    @Override
    public String toString() {
        return "songName = " + songName + " singerName " + singerName + " albumName " + albumName + "\n";
    }
}
