# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

## Latar Belakang

Server memberikan 3 tempat untuk dicari dengan menggunakan bantuan Google Maps di platform Android. Ketiga tempat tersebut berada di ITB dan merupakan salah satu dari tempat-tempat berikut :

* gku_barat
* gku_timur
* intel
* cc_barat
* cc_timur
* dpr
* sunken
* perpustakaan
* pau
* kubus

## Lokasi Source Code dan File Binary
File .java source code ada di folder app\src\main\java\com\example\asus\locationfinder

File .xml layout ada di app\src\main\res\layout

File .apk binary ada di app\build\outputs\apk

## Spesifikasi Aplikasi
1. Mengirim dan menerima pesan jawaban dari tempat yang dicari dalam format JSON seperti yang dicontohkan pada interaksi client-server

2. Replay server ditampilkan dalam toast/alert dialog.

3. Kompas yang menunjuk ke utara dengan sensor geomagnetik

4. Dapat mengirim intent kamera dan mengambil gambar

5. Minimal Android API 16

6. Tampilan tombol mengikuti orientasi dari device

7. Menghentikan sensor pada activity sementara ketika berpindah activity

8. Memakai socket dengan server di 167.205.34.132 port 3111

## Interaksi Client-Server
**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13512999”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Client Request**
Longitude dan Latitude menggunakan tipe Long.
```sh
{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban **benar dan sudah berada dilokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```
**No NIM**
```sh
{“status”:”err”,”nim”:””,”token”:”124fewfm32r32ifmwder42”}
```
**No Com command**
```sh
{“status”:”err”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”}
```

## Screenshot
**Tampilan vertikal**

![alt text](http://i.imgur.com/Kjo56CP.png)

**Tampilan horizontal**

![alt text](http://i.imgur.com/8Hl7bxs.png)

**Tampilan *submit* jawaban vertikal**

![alt text](http://i.imgur.com/ZvpIyXW.png)

**Tampilan *submit* jawaban horizontal**

![alt text](http://i.imgur.com/nsNcgdM.png)