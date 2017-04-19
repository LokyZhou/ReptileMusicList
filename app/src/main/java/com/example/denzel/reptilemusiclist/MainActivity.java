package com.example.denzel.reptilemusiclist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private List<MusicListItem> list = new ArrayList<>();

    private Button button;
    private TextView textView;
    EditText editText;
    private String DEFAULT_URL = "https://y.qq.com/n/yqq/playlist/3559762465.html#";
    private String URL_HEADER = "https://y.qq.com/n/yqq/playlist/";
    private String URL_FOOTER = ".html#";
    private long folderId = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                textView.setText(list.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editText);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            list.clear();
            String url = editText.getText().toString();
            if (url == null || !url.startsWith("http") || haveNoFolderId(url)) {
                url = DEFAULT_URL;
            } else {
                if (folderId > 0) {
                    url = URL_HEADER + folderId + URL_FOOTER;
                } else {
                    url = DEFAULT_URL;
                }
            }
            org.jsoup.Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 获取tbody元素下的所有tr元素
            Elements elements = doc.select("div.songlist__item");
            for (Element element : elements) {
                String songName = element.select("div.songlist__songname").select("span.songlist__songname_txt").select("a").attr("title");
                String singerName = element.select("div.songlist__artist").select("a.singer_name").text();
                String albumName = element.select("div.songlist__album").select("a").attr("title");
                MusicListItem item = new MusicListItem();
                item.songName = songName;
                item.singerName = singerName;
                item.albumName = albumName;
                list.add(item);
            }
            handler.sendEmptyMessage(0);

        }
    };

    private boolean haveNoFolderId(String url) {
        Uri uri = Uri.parse(url);
        if (uri.getQueryParameter("id") != null) {
            folderId = Long.parseLong(uri.getQueryParameter("id"));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            textView.setText("");
            new Thread(runnable).start();
        }
    }
}
