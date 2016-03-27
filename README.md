# AndroidMap ITB by Levanji Prahyudy - 13513052

## Latar Belakang

Halo! Perkenalkan saya Levanji Prahyudy, mahasiswa Teknik Informatika Institut Teknologi Bandung tahun ke-3

Untuk memenuhi kebutuhan tugas mata kuliah IF3111 (Pengembangan Aplikasi pada Platform Khusus), saya membuat aplikasi di atas platform Android.
Aplikasi ini menggunakan bantuan Google Maps API dan software Android Studio. Versi Android yang dipakai dalam pengembangan aplikasi ini adalah Android Kitkat (versi 4.4.4).

## Tentang Aplikasi

Aplikasi ini bertujuan untuk menemukan beberapa lokasi yang dikirimkan oleh server.

Pertama - tama, aplikasi mengirimkan permintaan lokasi kepada server beserta identitas berupa nomor induk mahasiswa. Server akan menjawab permintaan lokasi dengan mengirimkan lokasi berupa latitude dan longitude, beserta token untuk hak akses aplikasi.

Kemudian, aplikasi dapat mengirimkan jawaban berupa latitude dan longitude dari lokasi di mana platform digunakan dengan menekan tombol yang disediakan.
Tampilan depan dapat dilihat pada gambar di bawah ini :

**Tampilan Portrait**

![portrait](http://i.imgur.com/MO4Tn3d.jpg=20x50)

**Tampilan Horizontal**

![landscape](http://i.imgur.com/CaxLJAJ.jpg)


Sedangkan tampilan untuk mengirimkan jawaban dapat dilihat pada gambar di bawah ini :
![submit](http://i.imgur.com/GRce6YE.jpg)


Selain itu, ada fitur kamera untuk berfoto ria ketika kami telah menemukan lokasi yang diminta oleh server

## Teknis Aplikasi

Komunikasi dengan server dilakukan dengan mengirimkan pesan dengan format JSON, antara lain berisi latitude, longitude, status, dan token.
Ada 3 kemungkinan respon dari server ketika mengirimkan jawaban lokasi :
* ok : aplikasi akan memunculkan toast "Jawaban benar", kemudian menerima lokasi selanjutnya yang dikirim oleh server.
* wrong answer : aplikasi akan memunculkan toast "Jawaban salah", kemudian kembali ke lokasi tersebut untuk dijawab kembali.
* finish : aplikasi akan memunculkan toast "Selesaiii!!" yang berarti seluruh lokasi telah terjawab

Contoh komunikasi JSON dengan server :
**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13513052”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13513052”,”latitude”:”-6.784123132”,”longitude”:”107.652234”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13513052”,”answer”:”intel”, ”longitude”:”-6.78407723”,”latitude”:”107.652237”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
```sh
{“status”:”ok”,”nim”:”13513052,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
^

^

Server mengirimkan lokasi yang lain

Jika jawaban salah, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13513052”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban benar dan sudah berada di lokasi terakhir, maka:
```sh
{“status”:”finish”,”nim”:”13513052”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```

Selain itu, ada fitur kompas yang selalu menunjuk arah utara. Ya memang cukup memberatkan memori pada platform.
Maka dari itu, aplikasi dapat melepas sensor magnetik tersebut ketika berpindah aplikasi, sehingga lebih menghemat memori.

## Asumsi
- Lokasi yang dikirimkan sebagai jawaban merupakan lokasi android pada saat ini
- JSON tidak ditampilkan pada android
- Log JSON akan ditunjukkan dengan cara melakukan copy - paste logcat selama keberjalanan aplikasi


## File Location

**bin**

Tubes1-Android\app\build\outputs\apk

**src utama**

Tubes1-Android\app\src\main\java\com\example\vanji\androidmap

**src lain**

Tubes1-Android\app\src
