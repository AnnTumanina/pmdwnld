import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "src\\inFile.txt";//файл с сайтом
    private static final String MUSIC_OUTPUT_FILE_TXT = "src\\MusicOutputFile.txt";//файл с ссылками на скачивание музыки
    private static final String PICTURE_OUTPUT_FILE_TXT = "src\\PictureOutputFile.txt";//файл с ссылками на скачивание картинки
    private static final String PATH_TO_MUSIC = "src\\Music\\music";//файл с музыкой
    private static final String PATH_TO_PICTURE = "src\\Pictures\\picture";//файл с картинкой

    public static void main(String[] args) {
        String Url;
//тут поиск ссылок на скачивание музыки
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(MUSIC_OUTPUT_FILE_TXT))) {//потоки чтения записи ссылок
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));
                }
                Pattern email_pattern = Pattern.compile("href=\"\\/\\/mp3uks.ru\\/mp3\\/files\\/(.+-mp3).mp3");//регулярное выражение для поиска ссылок
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                while (matcher.find() && i < 5) {
                    outFile.write(matcher.group().replaceAll("href=\"", "") + "\n");//запись ссылок
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//тут обращение к методу скачивания музыки с передачей парамеров
        try (BufferedReader musicFile = new BufferedReader(new FileReader(MUSIC_OUTPUT_FILE_TXT))) {//открытие потока чтения файла с ссылками на скачивание музыки
            String music;
            int count = 0;
            try {
                while ((music = musicFile.readLine()) != null) {
                    downloadUsingNIO("https:" + music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");//обращение к методу скачивания
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//тут поиск ссылок на скачивание картинок
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(PICTURE_OUTPUT_FILE_TXT))) {//потоки чтения записи ссылок
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));
                }
                Pattern email_pattern = Pattern.compile("//img2.akspic.ru/previews/(.+?).jpg");//регулярное выражение для поиска картинок
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                while (matcher.find() && i < 5) {//счётчик что их будет 5
                    outFile.write(matcher.group() + "\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//тут обращение к методу скачивания картинок с передачей парамеров
        try (BufferedReader musicFile = new BufferedReader(new FileReader(PICTURE_OUTPUT_FILE_TXT))) {
            String picture;
            int count = 0;
            try {
                while ((picture = musicFile.readLine()) != null) {
                    downloadUsingNIO("https:" + picture, PATH_TO_PICTURE + String.valueOf(count) + ".jpg");//открытие потока чтения файла с ссылками на скачивание картинок
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream inputStream = new FileInputStream("src\\Music\\music0.mp3")) {//прослушивание музыки
            try {
                Player player = new Player(inputStream);
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//само скачивание с сайта
    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);//запись в папку которая была передана
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }

}
